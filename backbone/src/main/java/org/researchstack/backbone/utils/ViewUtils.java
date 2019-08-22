package org.researchstack.backbone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ViewUtils {

    private ViewUtils() {
    }

    public static InputFilter[] addFilter(InputFilter[] filters, InputFilter filter) {
        if (filters == null || filters.length == 0) {
            return new InputFilter[]{filter};
        } else {
            // Overwrite value if the filter to be inserted already exists in the filters array
            for (int i = 0, size = filters.length; i < size; i++) {
                if (filters[i].getClass().isInstance(filter)) {
                    filters[i] = filter;
                    return filters;
                }
            }

            // If our loop fails to find filter class type, create a new array and insert that
            // filter at the end of the array.
            int newSize = filters.length + 1;
            InputFilter newFilters[] = new InputFilter[newSize];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            newFilters[newSize - 1] = filter;

            return newFilters;
        }
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    public static void hideSoftInputMethod(Context context) {
        if (context instanceof Activity) {
            View currentFocus = ((Activity) context).getCurrentFocus();

            if (currentFocus != null && currentFocus.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromInputMethod(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void hideSoftInputMethod(ViewGroup parent) {
        try {
            InputMethodManager imm = (InputMethodManager) parent.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                parent.clearFocus();
            }
        } catch (NullPointerException e) {
            Log.e("CSSL", "NPE: " + e.getMessage());
        }
    }

    public static void showSoftInputMethod(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static Fragment createFragment(String className) {
        try {
            Class fragmentClass = Class.forName(className);
            return createFragment(fragmentClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static Fragment createFragment(Class fragmentClass) {
        try {
            Constructor<?> fragConstructor = fragmentClass.getConstructor();
            Object fragment = fragConstructor.newInstance();
            return (Fragment) fragment;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find all views of this type.
     *
     * @param root root view to start with.
     * @param classType class to find.
     * @param recursive recursive through root.
     * @param <T> class type.
     * @return all found views.
     */
    @NonNull
    public static <T> List<T> findViewsOf(ViewGroup root, Class<T> classType, boolean recursive) {
        List<T> collection = new ArrayList<>();
        findViewsOf(root, classType, recursive, collection);
        return collection;
    }

    private static <T> void findViewsOf(ViewGroup root, Class<T> classType, boolean recursive, List<T> collection) {
        for(int i=0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if(classType.isAssignableFrom(child.getClass())) {
                collection.add((T)child);
            } else if(child instanceof ViewGroup && recursive) {
                findViewsOf((ViewGroup)child, classType, recursive, collection);
            }
        }
    }
}

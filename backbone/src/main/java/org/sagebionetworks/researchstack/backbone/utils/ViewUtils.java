package org.sagebionetworks.researchstack.backbone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Constructor;

public class ViewUtils {

    private ViewUtils() {
    }

    /**
     * @param parent the view to be search within
     * @return the first EditText that is the parent, or is within the parent
     */
    public static EditText findFirstEditText(View parent) {
        if (parent instanceof EditText) {
            return (EditText)parent;
        }
        if (!(parent instanceof ViewGroup)) {
            return null;
        }
        ViewGroup parentViewGroup = (ViewGroup)parent;
        for (int i = 0; i < parentViewGroup.getChildCount(); i++) {
            EditText editText = findFirstEditText(parentViewGroup.getChildAt(i));
            if (editText != null) {
                return editText;
            }
        }
        return null;
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
     * @param context can be app or activity, used for Resources class
     * @param dp the size in dp as the input
     * @return the dp converted to px for this device based on its display metrics
     */
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, displayMetrics);
    }
}

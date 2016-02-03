package co.touchlab.researchstack.backbone.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ViewUtils
{

    public static InputFilter[] addFilter(InputFilter[] filters, InputFilter filter)
    {
        if(filters == null || filters.length == 0)
        {
            return new InputFilter[] {filter};
        }
        else
        {
            // Overwrite value if the filter to be inserted already exists in the filters array
            for(int i = 0, size = filters.length; i < size; i++)
            {
                if(filters[i].getClass().isInstance(filter))
                {
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

    public static void disableSoftInputFromAppearing(EditText editText)
    {
        if(Build.VERSION.SDK_INT >= 11)
        {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        }
        else
        {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    public static void hideSoftInputMethod(Context context)
    {
        if(context instanceof Activity)
        {
            View currentFocus = ((Activity) context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(currentFocus.getWindowToken(), 0);
        }
    }

    public static void showSoftInputMethod(EditText editText)
    {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static Drawable getDrawable(Context context, int resId)
    {
        //TODO use AppCompatDrawableManager?
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            return context.getResources().getDrawable(resId, context.getTheme());
        }
        else
        {
            return context.getResources().getDrawable(resId);
        }
    }
}

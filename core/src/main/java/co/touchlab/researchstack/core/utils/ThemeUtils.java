package co.touchlab.researchstack.core.utils;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;

public class ThemeUtils
{

    public static int getTextColorPrimary(Context context)
    {
        TypedValue typedValue = new TypedValue();
        int[] attribute = new int[] { android.R.attr.textColorPrimary };
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        int color = array.getColor(0, Color.BLACK);
        array.recycle();
        return color;
    }
}

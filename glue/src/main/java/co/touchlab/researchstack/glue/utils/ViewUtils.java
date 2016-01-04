package co.touchlab.researchstack.glue.utils;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import co.touchlab.researchstack.glue.R;

public class ViewUtils
{
    public static int fetchAccentColor(Context context)
    {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data,
                new int[] {R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
}

package org.researchstack.backbone.ui;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;

@Deprecated
public class LocalImageGetter implements Html.ImageGetter
{

    private Context context;
    private int     screenWidth;

    public LocalImageGetter(View host)
    {
        this.context = host.getContext();

        int tvWidth = host.getWidth();

        if(tvWidth == 0)
        {
            host.measure(0, 0);
        }

        // TODO finding the measurements of screen width instead of the tv host is hacky. Find
        // a better way
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        Point size = new Point();
        manager.getDefaultDisplay().getSize(size);
        screenWidth = size.x - host.getPaddingLeft() - host.getPaddingRight();
    }

    @Override
    public Drawable getDrawable(String source)
    {
        Resources res = context.getResources();
        int drawableId = res.getIdentifier(source, "drawable", context.getPackageName());
        Drawable drawable = res.getDrawable(drawableId);

        int[] dimens = getDrawableDimensForScreen(drawable);
        drawable.setBounds(0, 0, dimens[0], dimens[1]);

        return drawable;
    }

    private int[] getDrawableDimensForScreen(Drawable drawable)
    {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        if(width > screenWidth)
        {
            height = (height * screenWidth) / width;
            width = screenWidth;
        }

        return new int[] {width, height};
    }
}

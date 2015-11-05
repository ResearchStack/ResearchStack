package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;

public class ConsentVisualScene extends Scene
{

    public ConsentVisualScene(Context context)
    {
        super(context);
    }

    public ConsentVisualScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentVisualScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public ConsentVisualScene(Context context, ConsentSection data)
    {
        super(context);

        int accentColor = fetchAccentColor(context);

        String title = TextUtils.isEmpty(data.getTitle()) ?
                getResources().getString(data.getType().getTitleResId()) : data.getTitle();
        setTitle(title);

        if (!TextUtils.isEmpty(data.getSummary()))
        {
            setSummary(data.getSummary());
        }

        String imageName = !TextUtils.isEmpty(data.getCustomImageName()) ? data.getCustomImageName() :
                data.getType().getImageName();
        if (!TextUtils.isEmpty(imageName))
        {
            int imageResId = getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());
            setImage(imageResId);
            getImageView().setColorFilter(accentColor, PorterDuff.Mode.ADD);
        }

        setMoreInfo(data.getType().getMoreInfoResId(), v -> {
            String path = data.getHtmlContent();
            String webTitle = getResources().getString(R.string.consent_section_more_info);
            Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
            getContext().startActivity(webDoc);
        });
        getMoreInfo().setTextColor(accentColor);

        setSkip(false);

//        TODO self.continueSkipContainer.continueEnabled = YES;
//        TODO [self.continueSkipContainer updateContinueAndSkipEnabled];
    }


    @Override
    public StepResult getResult()
    {
        // We can ignore this, as we don't return a result for a visual consent scenen
        return null;
    }

//    TODO Make into static helper method (ViewUtils?)
    private int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

}

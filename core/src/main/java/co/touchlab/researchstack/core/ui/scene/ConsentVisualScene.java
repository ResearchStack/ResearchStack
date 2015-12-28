package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;

public class ConsentVisualScene extends SceneImpl
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

    @Override
    public void initializeScene()
    {
        super.initializeScene();

        ConsentVisualStep visualStep = (ConsentVisualStep) getStep();
        ConsentSection data = visualStep.getSection();

        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                                                           new int[] {R.attr.colorAccent});
        int accentColor = a.getColor(0, 0);
        a.recycle();

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
            String webTitle = getResources().getString(R.string.rsc_consent_section_more_info);
            Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
            getContext().startActivity(webDoc);
        });
        getMoreInfo().setTextColor(accentColor);

        setSkip(false);

        setNextButtonText(visualStep.getNextButtonString());
    }

    @Deprecated
    public void initialize(Step step, ConsentSection data)
    {
        super.initialize(step);
    }
}

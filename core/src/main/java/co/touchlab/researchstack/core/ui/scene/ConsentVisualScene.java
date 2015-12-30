package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.utils.ResUtils;

public class ConsentVisualScene extends RelativeLayout implements Scene
{

    private SceneCallbacks callbacks;
    private ConsentVisualStep step;

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
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentVisualStep) step;
        initializeScene();
    }

    private void initializeScene()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.scene_consent_visual, this, true);

        ConsentSection data = step.getSection();

        // Set Image
        String imageName = !TextUtils.isEmpty(data.getCustomImageName()) ? data.getCustomImageName() :
                data.getType().getImageName();
        if (!TextUtils.isEmpty(imageName))
        {
            TypedValue typedValue = new TypedValue();
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                                                               new int[] {R.attr.colorAccent});
            int accentColor = a.getColor(0, 0);
            a.recycle();

            int imageResId = ResUtils.getDrawableResourceId(getContext(), imageName);
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageResource(imageResId);
            imageView.setColorFilter(accentColor, PorterDuff.Mode.ADD);
        }

        // Set Title
        TextView titleView = (TextView) findViewById(R.id.title);
        String title = TextUtils.isEmpty(data.getTitle()) ?
                getResources().getString(data.getType().getTitleResId()) : data.getTitle();
        titleView.setText(title);

        // Set Summary
        TextView summaryView = (TextView) findViewById(R.id.summary);
        summaryView.setText(data.getSummary());

        // Set more info
        TextView moreInfoView = (TextView) findViewById(R.id.more_info);
        moreInfoView.setText(data.getType().getMoreInfoResId());
        RxView.clicks(moreInfoView).subscribe(v -> {
            String path = data.getHtmlContent();
            String webTitle = getResources().getString(R.string.rsc_consent_section_more_info);
            Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
            getContext().startActivity(webDoc);
        });

        // Set Next
        TextView next = (TextView) findViewById(R.id.next);
        next.setText(step.getNextButtonString());
        RxView.clicks(next).subscribe(v -> {
            callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, null);
        });
    }

    @Override
    public View getView()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}

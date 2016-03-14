package org.researchstack.backbone.ui.step.layout;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

import rx.functions.Action1;

@Deprecated
public abstract class StepLayoutImpl <T> extends RelativeLayout implements StepLayout
{
    public static final String TAG = StepLayoutImpl.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private Step       step;
    private StepResult stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private ImageView    image;
    private TextView     title;
    private TextView     summary;
    private TextView     moreInfo;
    private TextView     next;
    private TextView     skip;
    private LinearLayout container;

    public StepLayoutImpl(Context context)
    {
        super(context);
    }

    public StepLayoutImpl(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StepLayoutImpl(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(Step step)
    {
        initialize(step, null);
    }

    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.stepResult = result;

        if(this.stepResult == null)
        {
            this.stepResult = new StepResult<T>(step);
        }

        initializeLayout();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    /**
     * Method allowing a step layout to consume a back event.
     *
     * @return
     */
    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, getStep(), getStepResult());
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public StepCallbacks getCallbacks()
    {
        return callbacks;
    }

    public void initializeLayout()
    {
        LogExt.i(getClass(), "initializeLayout()");

        if(getContext() instanceof StepCallbacks)
        {
            setCallbacks((StepCallbacks) getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        LogExt.i(getClass(), "onCreateLayout()");
        View layout = onCreateLayout(inflater, this);

        LogExt.i(getClass(), "onLayoutCreated()");
        onLayoutCreated(layout);

        LogExt.i(getClass(), "onCreateBody()");
        View body = onCreateBody(inflater, container);
        if(body != null)
        {
            View oldView = container.findViewById(R.id.step_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.step_body);

            LogExt.i(getClass(), "onBodyCreated()");
            onBodyCreated(body);
        }
    }

    public View onCreateLayout(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(getRootLayoutResourceId(), parent, true);
    }

    public void onLayoutCreated(View layout)
    {
        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.getViewTreeObserver().addOnPreDrawListener(() -> {
            int layoutHeight = StepLayoutImpl.this.getHeight();
            int infoContainerHeight = container.getHeight();

            //TODO Add additional check to see if the infoContainerHeight is > than layoutHeight. If it is, subtract difference from fillerHeight
            if(layoutHeight > 0 && infoContainerHeight > 0 &&
                    layoutHeight > infoContainerHeight)
            {
                int fillerHeight = layoutHeight - infoContainerHeight;
                if(fillerHeight >= 0 && fillerHeight != filler.getHeight())
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            fillerHeight);
                    filler.setLayoutParams(params);
                    LogExt.d(getClass(), "onPreDraw - Returning False, setting filler height");
                    return false;
                }
            }

            return true;

        });

        image = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.text);
        moreInfo = (TextView) findViewById(R.id.more_info);
        next = (TextView) findViewById(R.id.next);
        RxView.clicks(next).subscribe(v -> onNextClicked());

        skip = (TextView) findViewById(R.id.skip);

        if(step != null)
        {
            title.setText(step.getTitle());

            if(! TextUtils.isEmpty(step.getText()))
            {
                setSummary(step.getText());
            }

            skip.setVisibility(step.isOptional() ? View.VISIBLE : View.GONE);
            RxView.clicks(skip).subscribe(v -> onSkipClicked());
        }
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.depricated_step_layout_impl;
    }

    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return null;
    }

    public void onBodyCreated(View body)
    {
        LogExt.i(getClass(), "onBodyCreated()");
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), getStepResult());
        return super.onSaveInstanceState();
    }

    protected void onNextClicked()
    {
        if(isAnswerValid())
        {
            if(callbacks != null)
            {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, getStep(), getStepResult());
            }
            else
            {
                //TODO Review whether we should force a crash or just log the message through Logcat.
                throw new IllegalStateException("StepCallbacks must be set on class");
            }
        }
        else
        {
            Toast.makeText(getContext(), R.string.rsb_please_complete_step, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onSkipClicked()
    {
        if(callbacks != null)
        {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, getStep(), null);
        }
    }

    public Step getStep()
    {
        return step;
    }

    public String getString(@StringRes int stringResId)
    {
        return getResources().getString(stringResId);
    }

    public StepResult<T> getStepResult()
    {
        return stepResult;
    }

    public void setStepResult(StepResult<T> result)
    {
        this.stepResult = result;
    }

    /**
     * @return true to call through to host and start next step.
     */
    public boolean isAnswerValid()
    {
        return true;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Setters for UI
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setImage(@DrawableRes int drawableResid)
    {
        if(image.getVisibility() != View.VISIBLE)
        {
            image.setVisibility(View.VISIBLE);
        }

        image.setImageResource(drawableResid);
    }

    public ImageView getImageView()
    {
        return image;
    }

    public void setTitle(@StringRes int stringRes)
    {
        String title = getResources().getString(stringRes);
        setTitle(title);
    }

    public void setTitle(CharSequence string)
    {
        title.setText(string);
    }

    public void setSummary(@StringRes int stringRes)
    {
        String summary = getResources().getString(stringRes);
        setSummary(summary);
    }

    public void setSummary(String string)
    {
        if(summary.getVisibility() != View.VISIBLE)
        {
            summary.setVisibility(View.VISIBLE);
        }

        summary.setText(string);
    }

    public void setMoreInfo(@StringRes int stringRes, Action1<? super Object> action)
    {
        if(moreInfo.getVisibility() != View.VISIBLE)
        {
            moreInfo.setVisibility(View.VISIBLE);
        }

        moreInfo.setText(stringRes);

        if(action != null)
        {
            RxView.clicks(moreInfo).subscribe(action);
        }
    }

    public TextView getMoreInfo()
    {
        return moreInfo;
    }

    public void setSkip(boolean isOptional)
    {
        setSkip(isOptional, 0, null);
    }

    public void setSkip(boolean isOptional, @StringRes int stringRes, Action1<? super Object> action)
    {
        skip.setVisibility(isOptional ? View.VISIBLE : View.GONE);

        if(stringRes != 0)
        {
            skip.setText(stringRes);
        }

        if(action != null)
        {
            RxView.clicks(skip).subscribe(action);
        }
    }

    public void setNextButtonText(@StringRes int stringRes)
    {
        String string = getResources().getString(stringRes);
        setNextButtonText(string);
    }

    public void setNextButtonText(String string)
    {
        next.setText(string);
    }

    protected void hideNextButtons()
    {
        next.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
    }

}

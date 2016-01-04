package co.touchlab.researchstack.core.ui.step.layout;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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

import java.lang.reflect.Constructor;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.body.StepBody;
import rx.functions.Action1;

public class SurveyStepLayout extends RelativeLayout implements StepLayout
{
    public static final String TAG = SurveyStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeScene and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private QuestionStep step;
    private StepResult   stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private SceneCallbacks callbacks;

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
    private StepBody     stepBody;

    public SurveyStepLayout(Context context)
    {
        super(context);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(Step step)
    {
        initialize(step, null);
    }

    public void initialize(Step step, StepResult result)
    {
        if(! (step instanceof QuestionStep))
        {
            throw new RuntimeException("Step being used in SurveyScene is not a QuestionStep");
        }

        this.step = (QuestionStep) step;
        this.stepResult = result;

        initializeScene();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    /**
     * Method allowing a scene to consume a back event.
     *
     * @return
     */
    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, getStep(), stepBody.getStepResult());
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public void initializeScene()
    {
        LogExt.i(getClass(), "initializeScene()");

        if(getContext() instanceof SceneCallbacks)
        {
            setCallbacks((SceneCallbacks) getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        LogExt.i(getClass(), "onCreateScene()");
        View scene = onCreateScene(inflater, this);

        LogExt.i(getClass(), "onSceneCreated()");
        onSceneCreated(scene);

        LogExt.i(getClass(), "onCreateBody()");
        stepBody = createStepBody(step);
        View body = stepBody.initView(inflater, this, step);
        if(stepResult != null)
        {
            stepBody.prefillResult(stepResult);
        }

        if(body != null)
        {
            View oldView = container.findViewById(R.id.scene_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.scene_body);

            LogExt.i(getClass(), "onBodyCreated()");
            onBodyCreated(body);
        }
    }

    @NonNull
    private StepBody createStepBody(Step step)
    {
        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor();
            return (StepBody) constructor.newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(getRootLayoutResourceId(), parent, true);
    }

    public void onSceneCreated(View scene)
    {
        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.getViewTreeObserver().addOnPreDrawListener(() -> {
            int sceneHeight = SurveyStepLayout.this.getHeight();
            int infoContainerHeight = container.getHeight();

            //TODO Add additional check to see if the infoContainerHeight is > than sceneHeight. If it is, subtract difference from fillerHeight
            if(sceneHeight > 0 && infoContainerHeight > 0 &&
                    sceneHeight > infoContainerHeight)
            {
                int fillerHeight = sceneHeight - infoContainerHeight;
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
        return R.layout.scene;
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
        callbacks.onSaveStep(SceneCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult());
        return super.onSaveInstanceState();
    }

    protected void onNextClicked()
    {
        if(stepBody.isAnswerValid())
        {
            if(callbacks != null)
            {
                callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT,
                        getStep(),
                        stepBody.getStepResult());
            }
            else
            {
                //TODO Review whether we should force a crash or just log the message through Logcat.
                throw new IllegalStateException("SceneCallbacks must be set on class");
            }
        }
        else
        {
            Toast.makeText(getContext(), R.string.rsc_please_complete_step, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onSkipClicked()
    {
        if(callbacks != null)
        {
            callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, getStep(), null);
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

package co.touchlab.researchstack.core.ui.step.layout;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.step.body.StepBody;
import co.touchlab.researchstack.core.ui.views.SubmitBar;

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
    private LinearLayout container;
    private TextView     title;
    private TextView     summary;
    private SubmitBar    submitBar;
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

        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.text);
        submitBar = (SubmitBar) findViewById(R.id.submit_bar);

        submitBar.setPositiveAction(v -> onNextClicked());

        if(step != null)
        {
            title.setText(step.getTitle());

            if(! TextUtils.isEmpty(step.getText()))
            {
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(step.getText()));
                summary.setMovementMethod(new TextViewLinkHandler(){
                    @Override
                    public void onLinkClick(String url)
                    {
                        Intent intent = ViewWebDocumentActivity.newIntent(
                                getContext(), step.getTitle(), url);
                        getContext().startActivity(intent);
                    }
                });
            }

            if (step.isOptional())
            {
                submitBar.setExitAction(R.string.rsc_step_skip, v -> onSkipClicked());
            }
            else
            {
                submitBar.hideExitAction();
            }
        }
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.step_layout;
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

}

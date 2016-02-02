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
import android.view.ViewTreeObserver;
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
import co.touchlab.researchstack.core.ui.callbacks.StepCallbacks;
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
    private StepCallbacks callbacks;

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
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, getStep(), stepBody.getStepResult());
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public void initializeScene()
    {
        LogExt.i(getClass(), "initializeScene()");

        if(getContext() instanceof StepCallbacks)
        {
            setCallbacks((StepCallbacks) getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View scene = onCreateScene(inflater, this);
        onSceneCreated(scene);

        View body = onCreateBody(inflater, this);
        onBodyCreated(body);
    }

    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        LogExt.i(getClass(), "onCreateScene()");
        return inflater.inflate(getRootLayoutResourceId(), parent, true);
    }

    public void onSceneCreated(View scene)
    {
        LogExt.i(getClass(), "onSceneCreated()");

        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
                {
                    @Override
                    public boolean onPreDraw()
            {
                int stepLayoutHeight = SurveyStepLayout.this.getHeight();
                int contentHeight = container.getHeight();

                // Make sure we have layout and height to measure
                if(stepLayoutHeight == 0 || contentHeight == 0)
                {
                    return true;
                }

                boolean proceedToDraw = true;

                // If our content does not take up the entire height of the screen, increase height
                // of the filler space
                if(contentHeight < stepLayoutHeight)
                {
                    filler.getLayoutParams().height = stepLayoutHeight - contentHeight;
                    filler.requestLayout();

                    proceedToDraw = false;
                }

                // If our content exceeds the height of the screen, adjust filler. If adjustment is
                // less than minHeight of filler, proceed to draw.
                else if(contentHeight > stepLayoutHeight)
                {
                    int contentBleedHeight = contentHeight - stepLayoutHeight;

                    if(filler.getHeight() > 0)
                    {
                        int newFillerHeight = filler.getHeight() - contentBleedHeight;
                        filler.getLayoutParams().height = newFillerHeight > 0 ? newFillerHeight : 0;
                        filler.requestLayout();

                        proceedToDraw = false;
                    }
                }

                //Only modify height once, ignore any future attempts to modify hierarchy ... for now
                container.getViewTreeObserver().removeOnPreDrawListener(this);

                LogExt.i("SurveyStepLayout", "onPreDraw - Returning " + proceedToDraw);
                return proceedToDraw;
            }
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
                summary.setMovementMethod(new TextViewLinkHandler()
                {
                    @Override
                    public void onLinkClick(String url)
                    {
                        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                                step.getTitle(),
                                url);
                        getContext().startActivity(intent);
                    }
                });
            }

            if(step.isOptional())
            {
                submitBar.setNegativeTitle(R.string.rsc_step_skip)
                        .setNegativeAction(v -> onSkipClicked());
            }
            else
            {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.step_layout;
    }


    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        LogExt.i(getClass(), "onCreateBody()");

        stepBody = createStepBody(step, stepResult);
        View body = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, inflater, parent);

        if(body != null)
        {
            View oldView = container.findViewById(R.id.scene_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.scene_body);
        }

        return body;
    }

    @NonNull
    private StepBody createStepBody(Step step, StepResult result)
    {
        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(step, result);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void onBodyCreated(View body)
    {
        if(body != null)
        {
            LogExt.i(getClass(), "onBodyCreated()");
        }
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult());
        return super.onSaveInstanceState();
    }

    protected void onNextClicked()
    {
        if(stepBody.isAnswerValid())
        {
            if(callbacks != null)
            {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
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

}

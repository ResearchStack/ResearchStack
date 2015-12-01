package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.Result;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.StepCallbacks;
import rx.functions.Action1;

/***************************************************************************************************
 * TODO List
 * - Remove initialize() from constructor. Make into a public facing method so that users can
 *   define layout in XML.
 ***************************************************************************************************/
public abstract class Scene extends RelativeLayout
{
    public static final String TAG = Scene.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initialize and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private Step step;
    private StepResult stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host (activity/fragment)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private ImageView image;
    private TextView title;
    private TextView summary;
    private TextView moreInfo;
    private TextView next;
    private TextView skip;
    private LinearLayout container;

    public Scene(Context context, Step step)
    {
        this(context,
                step,
                null);
    }

    public Scene(Context context, Step step, StepResult result)
    {
        super(context);

        this.step = step;
        this.stepResult = result;

        onPreInitialized();
        initialize();
    }

    public void onPreInitialized()
    {

    }

    public void initialize()
    {
        LogExt.i(getClass(), "initialize()");

        if (getContext() instanceof StepCallbacks)
        {
            setCallbacks((StepCallbacks) getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View scene = onCreateScene(inflater, this);
        onSceneCreated(scene);
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
        container.getViewTreeObserver().addOnPreDrawListener(() -> {
            int sceneHeight = Scene.this.getHeight();
            int infoContainerHeight = container.getHeight();

            //TODO Add additional check to see if the infoContainerHeight is > than sceneHeight. If it is, subtract difference from fillerHeight
            if(sceneHeight > 0 && infoContainerHeight > 0 && sceneHeight > infoContainerHeight)
            {
                int fillerHeight = sceneHeight - infoContainerHeight;
                if (fillerHeight >= 0 && fillerHeight != filler.getHeight())
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fillerHeight);
                    filler.setLayoutParams(params);
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

        if (step != null)
        {
            title.setText(step.getTitle());

            if(! TextUtils.isEmpty(step.getText()))
            {
                setSummary(step.getText());
            }

            skip.setVisibility(step.isOptional() ? View.VISIBLE : View.GONE);
            RxView.clicks(skip).subscribe(v -> onSkipClicked());
        }

        initBody();
    }

    public void onNextClicked()
    {
        if (callbacks != null && isAnswerValid())
        {
            if (getStep() != null){
                callbacks.onStepResultChanged(getStep(), getStepResult());
            }

            callbacks.onNextPressed(step);
        }
        else if (!isAnswerValid())
        {
            Toast.makeText(getContext(),
                    R.string.please_complete_step,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onSkipClicked()
    {
        if (callbacks != null)
        {
            callbacks.onSkipStep(step);
        }
    }

    private void initBody()
    {
        LogExt.i(getClass(), "initBody()");

        LayoutInflater inflater = LayoutInflater.from(getContext());

        LogExt.i(getClass(), "onCreateBody()");
        View body = onCreateBody(inflater, container);

        //TODO This stinks when you want to dynamically
        if (body != null)
        {
            int bodyIndex = getPositionToInsertBody();
            container.addView(body, bodyIndex);

            onBodyCreated(body);
        }
    }

    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return null;
    }

    public void onBodyCreated(View body) {
        LogExt.i(getClass(), "onBodyCreated()");
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.fragment_step;
    }

    //TODO Not sure how i feel about this method. Part of me says "OK", another says just return an
    //TODO integer (aka magic number).
    //TODO Consult Brad.
    protected int getPositionToInsertBody()
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);
        return stepViewContainer.indexOfChild(moreInfo) + 1;
    }

    public StepResult getStepResult()
    {
        if (stepResult == null)
        {
            // First, lets get it back from the activity
            stepResult = callbacks.getResultStep(step.getIdentifier());

            // Create a new result if the activity has no record of a result
            if(stepResult == null)
            {
                stepResult = createNewStepResult(step.getIdentifier());
            }
        }

        return stepResult;
    }

    public void setStepResult(Result result)
    {
        // TODO this is bad and we should feel bad
        Map<String, Result> results = new HashMap<>();
        results.put(result.getIdentifier(), result);
        getStepResult().setResults(results);

        callbacks.onStepResultChanged(step, stepResult);
    }

    public abstract StepResult createNewStepResult(String id);


    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Setters for UI
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setImage(@DrawableRes int drawableResid)
    {
        if (image.getVisibility() != View.VISIBLE)
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
        if (summary.getVisibility() != View.VISIBLE)
        {
            summary.setVisibility(View.VISIBLE);
        }

        summary.setText(string);
    }

    public void setMoreInfo(@StringRes int stringRes, Action1< ? super Object> action)
    {
        if (moreInfo.getVisibility() != View.VISIBLE)
        {
            moreInfo.setVisibility(View.VISIBLE);
        }

        moreInfo.setText(stringRes);

        if (action != null)
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

    public void setSkip(boolean isOptional, @StringRes int stringRes,  Action1<? super Object> action)
    {
        skip.setVisibility(isOptional ? View.VISIBLE : View.GONE);

        if (stringRes != 0)
        {
            skip.setText(stringRes);
        }

        if (action != null)
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

    /**
     * @return true to call through to host and start next scene.
     */
    public boolean isAnswerValid()
    {
        return true;
    }

    public StepCallbacks getCallbacks()
    {
        return callbacks;
    }

    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    /**
     * Method allowing a scene to consume a back event.
     * @return
     */
    public boolean isBackEventConsumed()
    {
        return false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        LogExt.i(getClass(), "onSaveInstanceState()");

        Parcelable superState = super.onSaveInstanceState();
        SceneSavedState ss = new SceneSavedState(superState);

        if (step != null)
        {
            LogExt.i(getClass(), "onSaveInstanceState() - " + step.toString());
        }
        ss.step = step;

        if (stepResult != null)
        {
            LogExt.i(getClass(), "onSaveInstanceState() - " + stepResult.toString());
        }
        ss.result = stepResult;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        LogExt.i(getClass(), "onRestoreInstanceState()");

        if(!(state instanceof SceneSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SceneSavedState ss = (SceneSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.step = ss.step;
        if (step != null)
        {
            LogExt.i(getClass(), "onRestoreInstanceState() - " + step.toString());
        }

        this.stepResult = ss.result;
        if (stepResult != null)
        {
            LogExt.i(getClass(), "onRestoreInstanceState() - " + stepResult.toString());
        }

        //TODO Make sure this works properly.
        initialize();
    }

    public Step getStep()
    {
        return step;
    }

    public String getString(@StringRes int stringResId)
    {
        return getResources().getString(stringResId);
    }

    private static class SceneSavedState extends BaseSavedState {

        Step step;
        StepResult result;

        SceneSavedState(Parcelable superState) {
            super(superState);
        }

        private SceneSavedState(Parcel in) {
            super(in);
            step = (Step) in.readSerializable();
            result = (StepResult) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSerializable(step);
            out.writeSerializable(result);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SceneSavedState> CREATOR =
                new Parcelable.Creator<SceneSavedState>() {
                    public SceneSavedState createFromParcel(Parcel in) {
                        return new SceneSavedState(in);
                    }
                    public SceneSavedState[] newArray(int size) {
                        return new SceneSavedState[size];
                    }
                };
    }

    /**
     * TODO Implement
     */
    public static class StepSceneBuilder
    {
        private String titleText;
        private String summaryText;
        private String moreInfoText;
        private Action1 moreInfoAction;
    }

}

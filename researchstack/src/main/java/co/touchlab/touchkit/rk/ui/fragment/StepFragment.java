package co.touchlab.touchkit.rk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.Result;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.callbacks.StepCallbacks;

@Deprecated
public abstract class StepFragment extends Fragment
{
    //TODO We are now using this in multiple place (ie. see ConsentStepFragment). Rename to something more generic.
    public static final String KEY_QUESTION_STEP = "KEY_STEP";

    protected Step step;
    protected StepResult stepResult;
    protected StepCallbacks callbacks;
    private TextView next;
    private TextView skip;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        callbacks = ((StepCallbacks) context);
    }

    public StepFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = (Step) getArguments().getSerializable(KEY_QUESTION_STEP);

        stepResult = callbacks.getResultStep(step.getIdentifier());
        if(stepResult == null)
        {
            stepResult = createNewStepResult(step.getIdentifier());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        Step step = getStep();

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(step.getTitle());

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setVisibility(TextUtils.isEmpty(step.getText()) ? View.GONE : View.VISIBLE);
        text.setText(step.getText());

        TextView moreInfo = (TextView) view.findViewById(R.id.more_info);
        initMoreInfoView(moreInfo);

        next = (TextView) view.findViewById(R.id.next);
        RxView.clicks(next).subscribe(v -> onNextPressed());

        skip = (TextView) view.findViewById(R.id.skip);
        skip.setVisibility(step.isOptional() ? View.VISIBLE : View.GONE);
        RxView.clicks(skip).subscribe(v -> onSkipPressed());

        LinearLayout stepViewContainer = (LinearLayout) view.findViewById(R.id.content_container);
        View bodyView = getBodyView(inflater);
        stepViewContainer.addView(bodyView, getBodyIndex(stepViewContainer));

        return view;
    }

    protected void onSkipPressed()
    {
        callbacks.onSkipStep(getStep());
    }

    protected void onNextPressed()
    {
        if (isAnswerValid())
        {
            callbacks.onNextPressed(getStep());
        }
    }

    protected boolean isAnswerValid()
    {
        return true;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = null;
    }

    public abstract StepResult createNewStepResult(String stepIdentifier);

    //TODO add ViewGroup as second parameter
    public abstract View getBodyView(LayoutInflater inflater);

    public Step getStep()
    {
        return step;
    }

    public StepResult getStepResult()
    {
        return stepResult;
    }

    public void setStepResult(Result result)
    {
        // TODO this is bad and we should feel bad
        Map<String, Result> results = new HashMap<>();
        results.put(result.getIdentifier(), result);
        stepResult.setResults(results);

        callbacks.onStepResultChanged(step, stepResult);
    }

    protected void hideNextButtons()
    {
        next.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
    }

    /**
     * Default view visibility is set to GONE
     * @param moreInfo
     */
    protected void initMoreInfoView(TextView moreInfo)
    {
    }

    //TODO Not sure how i feel about this method. Part of me says "OK", another says just return an
    //TODO integer (aka magic number)
    public int getBodyIndex(ViewGroup container)
    {
        View view = container.findViewById(R.id.more_info);
        if (view == null)
        {
            throw new RuntimeException("Field 'more_info' not found in layout. If overriding onCreateView," +
                                       "you must also override getBodyIndex and return a correct index");
        }
        return container.indexOfChild(view) + 1;
    }

}

package co.touchlab.touchkit.rk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public abstract class StepFragment extends Fragment
{

    private StepCallbacks callbacks;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // TODO send result
        callbacks = ((StepCallbacks) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_step,
                container,
                false);
        Step step = getStep();

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(step.getTitle());

        LinearLayout stepViewContainer = (LinearLayout) view.findViewById(R.id.step_view_container);
        int bodyIndex = stepViewContainer.indexOfChild(title) + 1;
        View bodyView = getBodyView(inflater);
        stepViewContainer.addView(bodyView,
                bodyIndex);

        TextView next = (TextView) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO send result
                callbacks.onNextPressed(getStep(), null);
            }
        });
        TextView skip = (TextView) view.findViewById(R.id.skip);

        return view;
    }

    public abstract View getBodyView(LayoutInflater inflater);

    public abstract Step getStep();

    public interface StepCallbacks {
        void onNextPressed(Step step, StepResult result);
    }
}

package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.ui.views.HeightPicker;

public class SignUpAdditionalInfoScene extends Scene
{
    private User user;

    public SignUpAdditionalInfoScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_additional_info, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);

        user = ResearchStackApplication.getInstance()
                .getCurrentUser();

        HeightPicker height = (HeightPicker) body.findViewById(R.id.height);
        height.setUserHeight(user.getHeight());
        height.getObservable()
                .subscribe(user::setHeight);

        AppCompatEditText weight = (AppCompatEditText) body.findViewById(R.id.weight);
        int userWeight = user.getWeight();
        if(userWeight > 0)
        {
            weight.setText(String.valueOf(userWeight));
        }
        RxTextView.textChanges(weight)
                .filter(input -> input.length() > 0)
                .map(charSequence -> Integer.valueOf(charSequence.toString()))
                .subscribe(user::setWeight);
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}

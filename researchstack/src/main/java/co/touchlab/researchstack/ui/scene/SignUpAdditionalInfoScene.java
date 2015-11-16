package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

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

        AppCompatTextView height = (AppCompatTextView) body.findViewById(R.id.height);
        height.setText(formatHeightString(user.getHeight()));
        RxView.clicks(height)
                .subscribe(view -> showDatePicker());

        AppCompatEditText weight = (AppCompatEditText) body.findViewById(R.id.weight);
        weight.setText(String.valueOf(user.getWeight()));
        RxTextView.textChanges(weight)
                .filter(input -> input.length() > 0)
                .map(charSequence -> Integer.valueOf(charSequence.toString()))
                .subscribe(user:: setWeight);
    }

    private String formatHeightString(int height)
    {
        int feet = height / 12;
        int inches = height % 12;
        return String.format("%d\' %d\"", feet, inches);
    }

    /**
     * TODO Launch image picker
     */
    private void showDatePicker()
    {
        Toast.makeText(getContext(),
                "TODO: launch image picker",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}

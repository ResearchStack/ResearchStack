package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.BooleanAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;

public class QuestionStepFragment extends StepFragment
{
    public static final String KEY_QUESTION_STEP = "KEY_STEP";
    private QuestionStep step;

    public QuestionStepFragment()
    {
    }

    public static Fragment newInstance(QuestionStep step)
    {
        QuestionStepFragment fragment = new QuestionStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = getArguments().getParcelable(KEY_QUESTION_STEP);
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        BooleanAnswerFormat answerFormat = (BooleanAnswerFormat) step.getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        for(TextChoice textChoice : answerFormat.getTextChoices())
        {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_checkbox, radioGroup, false);
            radioButton.setText(textChoice.getText());
            radioGroup.addView(radioButton);
        }

        return radioGroup;
    }

    @Override
    public Step getStep()
    {
        return step;
    }
}

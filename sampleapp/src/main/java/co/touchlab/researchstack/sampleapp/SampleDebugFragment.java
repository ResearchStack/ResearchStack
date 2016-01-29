package co.touchlab.researchstack.sampleapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.util.Arrays;

import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;

public class SampleDebugFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sample_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(view.findViewById(R.id.debug_form_task)).subscribe(v -> {

            // Create Consent form step, to get users first & last name
            FormStep formStep = new FormStep("debug_form_step",
                    "Form Title",
                    "Form step description");
            formStep.setSceneTitle(R.string.rsc_consent);

            QuestionStep debug1 = new QuestionStep("debug_1", "Text", new TextAnswerFormat());

            QuestionStep debug2 = new QuestionStep("debug_2",
                    "Date",
                    new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date));

            QuestionStep debug3 = new QuestionStep("debug_3",
                    "Integer",
                    new IntegerAnswerFormat(0, 3));

            AnswerFormat debug4Format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                    new Choice<>("Zero", 0),
                    new Choice<>("One", 1),
                    new Choice<>("Two", 2));
            QuestionStep debug4 = new QuestionStep("debug_4", "Multi", debug4Format);

            AnswerFormat debug5Format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    new Choice<>("Zero", 0),
                    new Choice<>("One", 1));
            QuestionStep debug5 = new QuestionStep("debug_5", "Single", debug5Format);

            formStep.setFormSteps(Arrays.asList(debug1, debug2, debug3, debug4, debug5));

            Task task = new OrderedTask("id", formStep);

            Intent intent = ViewTaskActivity.newIntent(getContext(), task);
            startActivityForResult(intent, 200);
        });
    }
}

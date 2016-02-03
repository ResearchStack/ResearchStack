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

import co.touchlab.researchstack.backbone.answerformat.AnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.backbone.model.Choice;
import co.touchlab.researchstack.backbone.step.FormStep;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.task.OrderedTask;
import co.touchlab.researchstack.backbone.task.Task;
import co.touchlab.researchstack.backbone.ui.ViewTaskActivity;
import co.touchlab.researchstack.skin.task.InitialTask;

public class SampleDebugFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sample_debug, container, false);
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

        RxView.clicks(view.findViewById(R.id.debug_form_task_initial)).subscribe(v -> {
            InitialTask initialTask = new InitialTask("task");
            Intent intent = ViewTaskActivity.newIntent(getContext(), initialTask);
            startActivityForResult(intent, 200);
        });

    }
}

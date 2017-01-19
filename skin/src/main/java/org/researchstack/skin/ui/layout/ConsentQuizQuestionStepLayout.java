package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.skin.R;
import org.researchstack.skin.model.ConsentQuizModel;
import org.researchstack.skin.step.ConsentQuizQuestionStep;

import java.util.ArrayList;
import java.util.List;

public class ConsentQuizQuestionStepLayout extends LinearLayout implements StepLayout {
    private ConsentQuizQuestionStep step;
    private StepResult<Boolean> result;
    private StepCallbacks callbacks;

    private TextView resultSummary;
    private TextView resultTitle;
    private RadioGroup radioGroup;
    private SubmitBar submitBar;
    private View radioItemBackground;
    private Choice<String> expectedChoice;

    public ConsentQuizQuestionStepLayout(Context context) {
        super(context);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        this.step = (ConsentQuizQuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    public void initializeStep() {
        setOrientation(VERTICAL);

        ConsentQuizModel.QuizQuestion question = step.getQuestion();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.rss_layout_quiz_question, this, true);

        ((TextView) findViewById(R.id.title)).setText(step.getTitle());

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.getNegativeActionView().setVisibility(GONE);

        resultTitle = (TextView) findViewById(R.id.quiz_result_title);
        resultSummary = (TextView) findViewById(R.id.quiz_result_summary);

        radioItemBackground = findViewById(R.id.quiz_result_item_background);

        if (question.getType().equals("instruction")) {
            TextView instructionText = (TextView) findViewById(R.id.instruction_text);
            instructionText.setText(question.getText());
            instructionText.setVisibility(VISIBLE);

            // instruction steps don't need submit, also always count as correct answer
            submitBar.setPositiveTitle(R.string.rsb_next);
            submitBar.setPositiveAction(v -> onNext(true));
        } else {
            submitBar.setPositiveTitle(R.string.rsb_submit);
            submitBar.setPositiveAction(v -> onSubmit());

            for (Choice<String> choice : getChoices(question)) {
                AppCompatRadioButton button = (AppCompatRadioButton) inflater.inflate(R.layout.rss_item_radio_quiz,
                        radioGroup,
                        false);
                button.setText(choice.getText());
                button.setTag(choice);
                radioGroup.addView(button);

                if (question.getExpectedAnswer().equals(choice.getValue())) {
                    expectedChoice = choice;
                }
            }
        }
    }

    @NonNull
    private List<Choice<String>> getChoices(ConsentQuizModel.QuizQuestion question) {
        List<Choice<String>> choices = new ArrayList<>();

        if (question.getType().equals("boolean")) {
            // json expected answer is a string of either "true" or "false"
            choices.add(new Choice<>(getContext().getString(R.string.rss_btn_true), "true"));
            choices.add(new Choice<>(getContext().getString(R.string.rss_btn_false), "false"));
        } else if (question.getType().equals("singleChoiceText")) {
            // json expected answer is a string of the index ("0" for the first choice)
            List<String> textChoices = question.getTextChoices();
            for (int i = 0; i < textChoices.size(); i++) {
                choices.add(new Choice<>(textChoices.get(i), String.valueOf(i)));
            }
        }
        return choices;
    }

    public void onSubmit() {
        if (isAnswerValid()) {
            int buttonId = radioGroup.getCheckedRadioButtonId();
            RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(buttonId);
            Choice<String> selectedChoice = (Choice<String>) checkedRadioButton.getTag();
            boolean answerCorrect = expectedChoice.equals(selectedChoice);

            if (resultTitle.getVisibility() == View.GONE) {
                int resultTextColor = answerCorrect ? 0xFF67bd61 : 0xFFc96677;
                int radioBackground = Color.argb(51,
                        //20% alpha
                        Color.red(resultTextColor),
                        Color.green(resultTextColor),
                        Color.blue(resultTextColor));

                // Disable buttons to prevent further action
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }

                // Set the drawable of the current checked button, with correct color tint
                int resId = answerCorrect ? R.drawable.rsb_ic_check : R.drawable.rsb_ic_x;
                Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, resultTextColor);
                checkedRadioButton.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        drawable,
                        null);

                // This is a work around for phones with api > 17 (but applies to all api versions).
                // The horizontal offset of a radioButton cannot be set but we need our RadioButtons
                // to have an offset of 48dp (to align w/ ToolBar title). Padding can be set but
                // that will only push the text, not button-drawable. To solve this issue, we have a
                // view that is floating behind out RadioGroup and position and height is set
                // dynamically
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) radioItemBackground
                        .getLayoutParams();
                params.setMargins(0, checkedRadioButton.getTop(), 0, 0);
                params.height = checkedRadioButton.getHeight();
                radioItemBackground.setBackgroundColor(radioBackground);
                radioItemBackground.setVisibility(View.VISIBLE);

                //Set our Result-title
                String resultTitle = answerCorrect
                        ? getContext().getString(R.string.rss_quiz_evaluation_correct)
                        : getContext().getString(R.string.rss_quiz_evaluation_incorrect);

                this.resultTitle.setVisibility(View.VISIBLE);
                this.resultTitle.setText(resultTitle);
                this.resultTitle.setTextColor(resultTextColor);

                //Build and set our result-summary
                String explanation;

                if (answerCorrect) {
                    explanation = getContext().getString(R.string.rss_quiz_question_explanation_correct,
                            step.getQuestion().getPositiveFeedback());
                } else {
                    explanation = getContext().getString(R.string.rss_quiz_question_explanation_incorrect,
                            expectedChoice.getText(),
                            step.getQuestion().getNegativeFeedback());
                }


                resultSummary.setText(Html.fromHtml(explanation));
                resultSummary.setVisibility(View.VISIBLE);

                setSubmitBarNext();
            } else {
                onNext(answerCorrect);
            }
        }

    }

    private void onNext(boolean answerCorrect) {
        // Save the result and go to the next question
        result.setResult(answerCorrect);
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
    }

    private void setSubmitBarNext() {
        // Change the submit bar positive-title to "next"
        submitBar.setPositiveTitle(R.string.rsb_next);
    }

    public boolean isAnswerValid() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), R.string.rss_error_select_answer, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

}

package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.StepCallbacks;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.core.ui.views.SubmitBar;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.step.ConsentQuizQuestionStep;

public class ConsentQuizQuestionStepLayout extends RelativeLayout implements StepLayout
{
    private ConsentQuizQuestionStep step;
    private StepResult<Boolean>     result;
    private StepCallbacks callbacks;

    private TextView    resultSummary;
    private TextView    resultTitle;
    private RadioGroup  radioGroup;
    private SubmitBar   submitBar;
    private RadioButton radioFalse;
    private RadioButton radioTrue;
    private View        radioItemBackground;

    public ConsentQuizQuestionStepLayout(Context context)
    {
        super(context);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentQuizQuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        initializeScene();
    }

    public void initializeScene()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.item_quiz_question, this, true);

        ((TextView) findViewById(R.id.title)).setText(step.getTitle());

        radioGroup = (RadioGroup) findViewById(R.id.rdio_group);

        radioTrue = (RadioButton) findViewById(R.id.btn_true);
        radioTrue.setText(R.string.btn_true);

        radioFalse = (RadioButton) findViewById(R.id.btn_false);
        radioFalse.setText(R.string.btn_false);

        submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction(v -> onSubmit());
        submitBar.setNegativeAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_END,
                step,
                result));

        resultTitle = (TextView) findViewById(R.id.quiz_result_title);
        resultSummary = (TextView) findViewById(R.id.quiz_result_summary);

        radioItemBackground = findViewById(R.id.quiz_result_item_background);
    }

    public void onSubmit()
    {
        if(isAnswerValid())
        {
            boolean answer = step.getQuestion().constraints.validation.answer.equals("true");
            boolean selected = radioGroup.getCheckedRadioButtonId() == R.id.btn_true;
            boolean answerCorrect = answer == selected;

            if(resultTitle.getVisibility() == View.GONE)
            {
                //TODO get from resources / theme
                int resultTextColor = answerCorrect ? 0xFF67bd61 : 0xFFc96677;
                int radioBackground = Color.argb(51,
                        //20% alpha
                        Color.red(resultTextColor),
                        Color.green(resultTextColor),
                        Color.blue(resultTextColor));

                // Disable both buttons to prevent further action
                radioFalse.setEnabled(false);
                radioTrue.setEnabled(false);

                // Set the drawable of the current checked button, with correct color tint
                Drawable drawable = getContext().getDrawable(answerCorrect
                        ? R.drawable.ic_check
                        : R.drawable.ic_window_close);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, resultTextColor);
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        radioItemBackground.getLayoutParams();
                params.addRule(ALIGN_TOP, radioTrue.isChecked() ? R.id.rdio_group : 0);
                params.addRule(ALIGN_BOTTOM, radioTrue.isChecked() ? 0 : R.id.rdio_group);
                params.height = checkedRadioButton.getHeight();
                radioItemBackground.setBackgroundColor(radioBackground);
                radioItemBackground.setVisibility(View.VISIBLE);

                //Set our Result-title
                String resultTitle = answerCorrect
                        ? step.getProperties().correctTitle
                        : step.getProperties().incorrectTitle;

                this.resultTitle.setVisibility(View.VISIBLE);
                this.resultTitle.setText(resultTitle);
                this.resultTitle.setTextColor(resultTextColor);

                //Build and set our result-summary
                String part1;
                String part2;
                String part3;

                if(answerCorrect)
                {
                    part1 = step.getProperties().correctCliffhanger;
                    part2 = step.getProperties().correct;
                    part3 = step.getQuestion().positiveFeedback;
                }
                else
                {
                    part1 = step.getProperties().incorrectCliffhanger;
                    part2 = Boolean.toString(answer) + ". ";
                    part3 = step.getQuestion().negativeFeedback;
                }

                SpannableString explanation = new SpannableString(part1 + part2 + part3);
                explanation.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),
                        part1.length(),
                        part1.length() + part2.length(),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                resultSummary.setText(explanation);
                resultSummary.setVisibility(View.VISIBLE);

                // Change the submit bar positive-title to "next"
                submitBar.setPositiveTitle(R.string.rsc_next);
            }
            else
            {
                // Save the result and go to the next question
                result.setResult(answerCorrect);
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
            }
        }

    }

    public boolean isAnswerValid()
    {
        if(radioGroup.getCheckedRadioButtonId() == - 1)
        {
            //TODO get string from res
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

}

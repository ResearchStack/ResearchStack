package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;

public class ConsentQuizQuestionScene extends SceneImpl<Boolean>
{

    private ConsentQuizModel.QuestionProperties properties;
    private ConsentQuizModel.QuizQuestion question;

    private TextView answerExplanation;
    private ImageView answerIcon;
    private RadioGroup radioGroup;

    public ConsentQuizQuestionScene(Context context)
    {
        super(context);
    }

    public ConsentQuizQuestionScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentQuizQuestionScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(Step step, ConsentQuizModel.QuestionProperties properties, ConsentQuizModel.QuizQuestion question)
    {
        this.properties = properties;
        this.question = question;

        super.initialize(step);
    }

    @Override
    public void initializeScene()
    {
        super.initializeScene();

        final SpannableString boldSpan = new SpannableString(
                properties.introText + "\n\n" + question.question);
        boldSpan.setSpan(new RelativeSizeSpan(.6f), 0, properties.introText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(boldSpan);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_quiz_question, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        radioGroup = (RadioGroup) body.findViewById(R.id.rdio_group);
        answerIcon = (ImageView) body.findViewById(R.id.icon_answer_result);
        answerExplanation = (TextView) body.findViewById(R.id.txt_answer_explanation);
    }

    @Override
    public boolean isAnswerValid()
    {
        if (radioGroup.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.isAnswerValid();
    }

    @Override
    public void onNextClicked()
    {
        if (!isAnswerValid())
        {
            return;
        }

        boolean answer = question.constraints.validation.answer.equals("true");
        boolean selected = radioGroup.getCheckedRadioButtonId() == R.id.btn_true;
        boolean answerCorrect = answer == selected;

        if(answerIcon.getVisibility() == View.GONE)
        {
            radioGroup.setEnabled(false);

            String iconName = answerCorrect ? properties.correctBadge : properties.incorrectBadge;
            int iconResId = getResources()
                    .getIdentifier(iconName, "drawable", getContext().getPackageName());
            answerIcon.setVisibility(View.VISIBLE);
            answerIcon.setImageResource(iconResId);

            answerExplanation.setVisibility(View.VISIBLE);

            String part1;
            String part2;
            String part3;

            if(answerCorrect)
            {
                part1 = properties.correctCliffhanger;
                part2 = properties.correct;
                part3 = question.positiveFeedback;
            }
            else
            {
                part1 = properties.incorrectCliffhanger;
                part2 = Boolean.toString(answer) + ". ";
                part3 = question.negativeFeedback;
            }

            SpannableString explanation = new SpannableString(part1 + part2 + part3);
            explanation.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), part1.length(),
                                part1.length() + part2.length(),
                                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            answerExplanation.setText(explanation);

            setNextButtonText(R.string.next);
        }
        else
        {
            super.onNextClicked();
        }

    }

    @Override
    public StepResult<Boolean> getStepResult()
    {
        boolean answer = radioGroup.getCheckedRadioButtonId() == R.id.btn_true;
        StepResult<Boolean> booleanStepResult = new StepResult<>(getStep().getIdentifier());
        booleanStepResult.setResultForIdentifier(StepResult.DEFAULT_KEY, answer);
        return booleanStepResult;
    }

}

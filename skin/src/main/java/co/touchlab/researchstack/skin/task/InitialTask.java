package co.touchlab.researchstack.skin.task;
import android.content.Context;

import co.touchlab.researchstack.backbone.answerformat.AnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.backbone.model.Choice;
import co.touchlab.researchstack.backbone.step.FormStep;
import co.touchlab.researchstack.backbone.step.InstructionStep;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.task.OrderedTask;
import co.touchlab.researchstack.glue.R;

public class InitialTask extends OrderedTask
{

    public InitialTask(String identifier)
    {
        super(identifier);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Intro step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep step = new InstructionStep("intro",
                "About You",
                "We'd like to ask you a few questions to better understand potential robot risks\n\nThese questions should take less than 5 minutes");

        // Add to Task
        addStep(step);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Basic Info Form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep basicInfoForm = new FormStep("basicInfo", "About You", "");

        // TODO dateOfBirthItem.placeholder = @"DOB"; FormItem has a placeholder field. Which would translate to the hint field on TextView
        // Date of Birth
        DateAnswerFormat dateOfBirthFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        QuestionStep dateOfBirthStep = new QuestionStep("dateOfBirth",
                "Date of Birth",
                dateOfBirthFormat);

        // Gender
        AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Female", 0),
                new Choice<>("Male", 1),
                new Choice<>("Other", 2));
        QuestionStep genderStep = new QuestionStep("gender", "Gender", genderFormat);

        // Zip Code
        // TODO max value for zip
        IntegerAnswerFormat zipCodeFormat = new IntegerAnswerFormat(0, 99999);
        QuestionStep zipCodeStep = new QuestionStep("zipCode",
                "What is your zip code?",
                zipCodeFormat);

        // Set items on FormStep
        basicInfoForm.setOptional(true);
        basicInfoForm.setFormSteps(dateOfBirthStep, genderStep, zipCodeStep);

        // Add to Task
        addStep(basicInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Eye and Hair color form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep hairEyesForm = new FormStep("hairEyesInfo", "Natural Hair and Eye Color", "");

        // Hair Color
        // TODO iOS defines ORKValuePickerAnswerFormat
        AnswerFormat hairColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Red Hair", "redHair"),
                new Choice<>("Blond Hair", "blondeHair"),
                new Choice<>("Brown Hair", "brownHair"),
                new Choice<>("Black Hair", "blackHair"));
        QuestionStep hairColorStep = new QuestionStep("hairColor", "Hair Color", hairColorFormat);

        // TODO iOS defines ORKValuePickerAnswerFormat
        AnswerFormat eyeColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Blue Eyes", "blueEyes"),
                new Choice<>("Green Eyes", "greenEyes"),
                new Choice<>("Brown Eyes", "brownEyes"));
        QuestionStep eyeColorStep = new QuestionStep("eyeColor", "Eye Color", eyeColorFormat);

        // Set items on FormStep
        hairEyesForm.setOptional(true);
        hairEyesForm.setFormSteps(hairColorStep, eyeColorStep);

        // Add to Task
        addStep(hairEyesForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Profession Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        // iOS defines this as a single choice, should be MultiChoice
        AnswerFormat professionFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Chocolate", "chocolate"),
                new Choice<>("Vanilla", "vanilla"),
                new Choice<>("Strawberry", "strawberry"),
                new Choice<>("Cookies & Cream", "cookies_cream"),
                new Choice<>("I am Robot, what is ice cream?", "robot"));
        QuestionStep professionStep = new QuestionStep("profession",
                "What is your favorite flavor of ice cream?",
                professionFormat);
        professionStep.setOptional(true);

        // Add to Task
        addStep(professionStep);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Medical Info Form Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep medicalInfoForm = new FormStep("medicalInfo", "Medical Information", "");

        BooleanAnswerFormat booleanAnswerFormat = new BooleanAnswerFormat();

        QuestionStep robotStep = new QuestionStep("confirmRobot",
                "Are you a robot?",
                booleanAnswerFormat);
        QuestionStep autoImmuneStep = new QuestionStep("feelings",
                "Does your robot body feel?",
                booleanAnswerFormat);
        QuestionStep immunocompromisedStep = new QuestionStep("arnold",
                "Are you stronger than a T-1000?",
                booleanAnswerFormat);

        // Set items on FormStep
        medicalInfoForm.setOptional(true);
        medicalInfoForm.setFormSteps(robotStep,
                autoImmuneStep,
                immunocompromisedStep);

        // Add to Task
        addStep(medicalInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Thank You Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep thankYouStep = new InstructionStep("thankYou",
                "Thank You!",
                "Your participation in this study is helping us to better understand risks of becoming a robot\n\nYour task now is to take robot surveys each month. You don't have to get them all, but the more the better!\n\nHappy robot-ing!");
        // Add to Task
        addStep(thankYouStep);

    }

    @Override
    public String getTitleForStep(Context context, Step step)
    {
        int currentIndex = getSteps().indexOf(step) + 1;
        return context.getString(R.string.rss_format_step_title, currentIndex, getSteps().size());
    }

}

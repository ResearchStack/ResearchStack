package co.touchlab.researchstack.skin.task;
import android.content.Context;

import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.InstructionStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.glue.R;

public class InitialTask extends OrderedTask
{

    public static final String TASK_ID = "initialTask";

    public InitialTask(String identifier)
    {
        super(identifier);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Intro step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep step = new InstructionStep("intro",
                "About You",
                "We'd like to ask you a few questions to better understand potential melanoma risk\n\nThese questions should take less than 5 minutes");

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
        IntegerAnswerFormat zipCodeFormat = new IntegerAnswerFormat(0, 1000);
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
                // American English defines adjective-form as Blond where Noun type is based on sex
                // of person (Male -> Blond, Female -> Blonde). I looked this up. We will keep the
                // ID as blondeHair as thats the ID used in mole-mapper-ios.
                new Choice<>("Blond Hair", "blondeHair"),
                new Choice<>("Brown Hair", "brownHair"),
                new Choice<>("Black Hair", "blackHair"));
        QuestionStep hairColorStep = new QuestionStep("hairColor", "Hair Color", hairColorFormat);

        // TODO iOS defines ORKValuePickerAnswerFormat
        AnswerFormat eyeColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Blue Hair", "blueEyes"),
                new Choice<>("Green Hair", "greenEyes"),
                new Choice<>("Brown Hair", "brownEyes"));
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
                new Choice<>("Pilot or flight crew", "pilot"),
                new Choice<>("Dental professional", "dental"),
                new Choice<>("Construction", "construction"),
                new Choice<>("Radiology Technician", "radiology"),
                new Choice<>("Farming", "farming"),
                new Choice<>("TSA Agent", "tsaAgent"),
                new Choice<>("Coal/Oil/Gas Extraction", "coalOilGas"),
                new Choice<>("Military Veteran", "veteran"),
                new Choice<>("Doctor/Nurse", "doctor"),
                new Choice<>("Welding/Soldering", "welding"),
                new Choice<>("Electrician", "electrician"),
                new Choice<>("Biomedical Researcher", "researcher"),
                new Choice<>("None of the above choices", "none"));
        QuestionStep professionStep = new QuestionStep("profession",
                "Have you worked in any of the following professions?",
                professionFormat);
        professionStep.setOptional(true);

        // Add to Task
        addStep(professionStep);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Medical Info Form Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep medicalInfoForm = new FormStep("medicalInfo", "Medical Information", "");

        BooleanAnswerFormat booleanAnswerFormat = new BooleanAnswerFormat();

        QuestionStep melanomaStep = new QuestionStep("historyMelanoma",
                "Have you ever been diagnosed with melanoma",
                booleanAnswerFormat);
        QuestionStep familyHistoryStep = new QuestionStep("familyHistory",
                "Has a blood relative (parent, sibling, child) ever had melanoma?",
                booleanAnswerFormat);
        QuestionStep moleRemovedStep = new QuestionStep("moleRemoved",
                "Have you ever had a mole removed?",
                booleanAnswerFormat);
        QuestionStep autoImmuneStep = new QuestionStep("autoImmune",
                "Do you have an autoimmune condition (Psoriasis, Crohn's disease, or others)?",
                booleanAnswerFormat);
        QuestionStep immunocompromisedStep = new QuestionStep("immunocompromised",
                "Do you have a weakened immune system for any reason (transplant recipient, lupus, prescribed drugs that suppress the immune system)?",
                booleanAnswerFormat);

        // Set items on FormStep
        medicalInfoForm.setOptional(true);
        medicalInfoForm.setFormSteps(melanomaStep,
                familyHistoryStep,
                moleRemovedStep,
                autoImmuneStep,
                immunocompromisedStep);

        // Add to Task
        addStep(medicalInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Thank You Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep thankYouStep = new InstructionStep("thankYou",
                "Thank You!",
                "Your participation in this study is helping us to better understand melanoma risk and skin health\n\nYour task now is to map and measure your moles each month. You don't have to get them all, but the more the better!\n\nHappy Mapping!");

        // Add to Task
        addStep(thankYouStep);

    }

    @Override
    public String getTitleForStep(Context context, Step step)
    {
        int currentIndex = getSteps().indexOf(step) + 1;
        return context.getString(R.string.format_step_title, currentIndex, getSteps().size());
    }

}

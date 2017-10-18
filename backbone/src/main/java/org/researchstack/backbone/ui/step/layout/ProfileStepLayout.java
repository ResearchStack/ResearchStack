package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.ProfileStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.utils.StepHelper;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by TheMDP on 1/14/17.
 *
 * ProfileStepLayout is used to display fields that relate to a user's profile
 * and the QuestionSteps were created from
 * see {@link org.researchstack.backbone.model.ProfileInfoOption} objects,
 * which can be found in the @see {@link org.researchstack.backbone.step.ProfileStep}
 */

public class ProfileStepLayout extends FormStepLayout {

    protected User user;

    /** Used to map steps identifiers to error messages */
    protected Map<String, String> identifierErrorMap;

    public ProfileStepLayout(Context context) {
        super(context);
    }

    public ProfileStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProfileStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected ProfileStep getProfileStep() {
        if (!(formStep instanceof ProfileStep)) {
            throw new IllegalStateException("ProfileStepLayout must contain a ProfileStep");
        }
        return (ProfileStep)formStep;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStepAndResult(step, result);  // also sets formStep variable
        initializeErrorMap();
        // needed to have object passed in by reference in method below
        if (result == null) {
            result = new StepResult<StepResult>(step);
        }
        prePopulateUserProfileResults(stepResult);
        super.initialize(step, result);
    }

    protected void initializeErrorMap() {
        identifierErrorMap = new HashMap<>();
        identifierErrorMap.put(ProfileInfoOption.NAME.getIdentifier(), getString(R.string.rsb_error_invalid_name));
        identifierErrorMap.put(ProfileInfoOption.EMAIL.getIdentifier(), getString(R.string.rsb_error_invalid_email));
        identifierErrorMap.put(ProfileInfoOption.PASSWORD.getIdentifier(), getString(R.string.rsb_error_invalid_password));
        // TODO: add the rest of the user profile error messages for options
    }

    /**
     * @param result to add pre-populated user profile results that are create from the User object
     */
    protected void prePopulateUserProfileResults(StepResult<StepResult> result) {
        user = DataProvider.getInstance().getUser(getContext());
        if (user == null) {
            user = new User();  // first time controlling the user object
        }
        for (ProfileInfoOption option : getProfileStep().getProfileInfoOptions()) {
            // Look to see if the step result for this profile option already exists
            StepResult profileResult = StepResultHelper.findStepResult(result, option.getIdentifier());
            // If it doesn't exist, create one matching the profile info option type from User object
            if (profileResult == null) {
                Step profileStep = StepHelper.getStepWithIdentifier(formStep.getFormSteps(), option.getIdentifier());
                StepResult stepResult = null;
                if (profileStep != null) {
                    switch (option) {
                        case NAME:
                            if (user.getName() != null) {
                                StepResult<String> nameResult = new StepResult<>(profileStep);
                                nameResult.setResult(user.getName());
                                stepResult = nameResult;
                            }
                            break;
                        case EMAIL:
                            if (user.getEmail() != null) {
                                StepResult<String> nameResult = new StepResult<>(profileStep);
                                nameResult.setResult(user.getEmail());
                                stepResult = nameResult;
                            }
                            break;
                        case BIRTHDATE:
                            if (user.getBirthDate() != null) {
                                StepResult<Long> nameResult = new StepResult<>(profileStep);
                                nameResult.setResult(user.getBirthDate().getTime());
                                stepResult = nameResult;
                            }
                            break;
                    }
                }
                // Add the step result to our result object so that the profile step bodies will
                // be pre-populated with the designated user's profile info
                if (stepResult != null) {
                    result.setResultForIdentifier(stepResult.getIdentifier(), stepResult);
                }
            }
        }
    }

    @Override
    protected void onNextClicked()
    {
        boolean isAnswerValid = isAnswerValid(true);
        if (isAnswerValid)
        {
            // Profile will be updated
            for (ProfileInfoOption option : getProfileStep().getProfileInfoOptions()) {
                switch (option) {
                    case NAME:
                        user.setName(getName());
                        break;
                    case EMAIL:
                        user.setEmail(getEmail());
                        break;
                    case BIRTHDATE:
                        user.setBirthDate(getBirthdate());
                        break;
                }
            }
            try {
                DataProvider.getInstance().setUser(getContext(), user);
            } catch (NullPointerException nullException) {
                Log.d(getClass().getCanonicalName(), "Encryption Data Provider is not initialized yet" +
                        "this means you are trying to save user data before the user has" +
                        "registered or logged in");
            }
            super.onNextClicked();
        }
    }

    protected boolean isAnswerValid(List<FormStepData> stepDataList, boolean showErrorAlertOnInvalid) {
        return super.isAnswerValid(stepDataList, showErrorAlertOnInvalid, identifierErrorMap);
    }

                                    /**
     * @return Name if this profile form step has it, null otherwise
     */
                                    protected String getName() {
        return getTextAnswer(ProfileInfoOption.NAME.getIdentifier());
    }

    /**
     * @return Email if this profile form step has it, null otherwise
     */
    protected String getEmail() {
        return getTextAnswer(ProfileInfoOption.EMAIL.getIdentifier());
    }

    /**
     * @return Email QuestionStep if this profile form step has it, null otherwise
     */
    protected QuestionStep getEmailStep() {
        return getQuestionStep(ProfileInfoOption.EMAIL.getIdentifier());
    }

    /**
     * @return Password if this profile form step has it, null otherwise
     */
    protected String getPassword() {
        return getTextAnswer(ProfileInfoOption.PASSWORD.getIdentifier());
    }

    /**
     * @return Confirm Password if this profile form step has it, null otherwise
     */
    protected String getConfirmPassword() {
        return getTextAnswer(SurveyFactory.PASSWORD_CONFIRMATION_IDENTIFIER);
    }

    /**
     * @return User's birthday if this profile form step has it, null otherwise
     */
    protected Date getBirthdate() {
        return getDateAnswer(ProfileInfoOption.BIRTHDATE.getIdentifier());
    }

    /**
     * @param stepIdentifier the identifier for the step
     * @return String answer of step body, null if one doesn't exist or it is not a String
     */
    protected String getTextAnswer(String stepIdentifier) {
        Object result = findStepResult(stepIdentifier);
        if (result != null && result instanceof String) {
            return (String)result;
        }
        return null;
    }

    /**
     * @param stepIdentifier the identifier for the step
     * @return Date answer of step body, null if one doesn't exist or it is not a Date
     */
    protected Date getDateAnswer(String stepIdentifier) {
        Object result = findStepResult(stepIdentifier);
        if (result != null && result instanceof Long) {
            return new Date((Long)result);
        }
        return null;
    }

    protected Object findStepResult(String stepIdentifier) {
        StepBody matchingStepBody = getStepBody(stepIdentifier);
        if (matchingStepBody != null) {
            return matchingStepBody.getStepResult(false).getResult();
        }
        return null;
    }
}

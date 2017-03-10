package org.researchstack.backbone.step;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;

import java.util.Map;

/**
 * Created by TheMDP on 2/11/17.
 *
 * Needed so InstructionStep and CustomInstructionStep can both use InstructionStepLayout
 */

public interface InstructionStepInterface {
    void setMoreDetailText(String detailText);
    String getMoreDetailText();

    void setFootnote(String footnote);
    String getFootnote();

    void setImage(String newImage);
    String getImage();

    void setIconImage(String image);
    String getIconImage();

    void setNextStepIdentifier(String identifier);
    String getNextStepIdentifier();

    void setIsImageAnimated(boolean isImageAnimated);
    boolean getIsImageAnimated();

    void setAnimationRepeatDuration(long animationRepeatDuration);
    long getAnimationRepeatDuration();

    void setSubmitBarNegativeActionSkipRule(String taskIdentifier, String title, String skipIdentifier);
    SubmitBarNegativeActionSkipRule getSubmitBarNegativeActionSkipRule();

    class SubmitBarNegativeActionSkipRule {

        public static final String SKIP_RESULT_KEY = "skip";

        private String taskIdentifier;
        private String title;
        private String skipToStepIdentifier;

        public SubmitBarNegativeActionSkipRule(String taskIdentifier, String title, String skipToStepIdentifier) {
            this.taskIdentifier = taskIdentifier;
            this.title = title;
            this.skipToStepIdentifier = skipToStepIdentifier;
        }

        public void onNegativeActionClicked(InstructionStepInterface stepInterface, StepResult stepResult) {
            // Set the next step identifier
            stepInterface.setNextStepIdentifier(skipToStepIdentifier);

            // add a result to this step view controller to mark that the task was skipped
            Map<String, Object> stepResultMap = stepResult.getResults();
            stepResultMap.put(SKIP_RESULT_KEY, taskIdentifier);
        }

        public String getTitle() {
            return title;
        }
    }
}

package org.researchstack.backbone.step;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.ui.step.layout.InstructionStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/12/17.
 */

public class CustomInstructionStep extends CustomStep implements NavigableOrderedTask.NavigationRule {
    /*
     * Additional detailed text to display
     */
    String moreDetailText;

    /**
     Additional text to display for the step in a localized string at the bottom of the view.

     The footnote is displayed in a smaller font below the continue button. It is intended to be used
     in order to include disclaimer, copyright, etc. that is important to display in the step but
     should not distract from the main purpose of the step.
     */
    String footnote;


    /**
     An image that provides visual context for the instruction.

     The image is displayed with aspect fit. Depending on the device, the screen area
     available for this image can vary. For exact
     metrics, see `ORKScreenMetricIllustrationHeight`.
     */
    String image;


    /**
     An image that provides visual context for the instruction that will allow for showing
     a two-part composite image where the `image` is tinted and the `auxiliaryImage` is
     shown with light grey.

     The image is displayed with the same frame as the `image` so both the `auxiliaryImage`
     and `image` should have transparently to allow for overlay.
     */
    // int auxiliaryImageRes; // TODO: do we need this? Also does Android easily support this?


    /**
     Optional icon image to show above the title and text.
     */
    String iconImage;

    /**
     * Pointer to the next step to show after this one. If nil, then the next step
     * is determined by the navigation rules setup by NavigableOrderedTask.
     */
    String nextStepIdentifier;

    /* Default constructor needed for serilization/deserialization of object */
    CustomInstructionStep() {
        super();
    }

    public CustomInstructionStep(String identifier, String title, String text, String customTypeIdentifier)
    {
        super(identifier, title, customTypeIdentifier);
        setText(text);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass()
    {
        return InstructionStepLayout.class;
    }

    public void setMoreDetailText(String detailText) {
        moreDetailText = detailText;
    }
    public String getMoreDetailText() {
        return moreDetailText;
    }

    public void setFootnote(String newFootnote) {
        footnote = newFootnote;
    }
    public String getFootnote() {
        return footnote;
    }

    public void setImage(String newImage) {
        image = newImage;
    }
    public String getImage() {
        return image;
    }

    public void setIconImage(String image) {
        iconImage = image;
    }
    public String getIconImage() {
        return iconImage;
    }

    public void setNextStepIdentifier(String identifier) {
        nextStepIdentifier = identifier;
    }
    public String getNextStepIdentifier() {
        return nextStepIdentifier;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        return nextStepIdentifier;
    }
}

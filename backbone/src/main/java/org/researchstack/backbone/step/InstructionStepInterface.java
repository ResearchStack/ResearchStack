package org.researchstack.backbone.step;

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
}

package org.researchstack.backbone.answerformat;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TheMDP on 3/14/17.
 *
 * The `ImageChoiceAnswerFormat` class represents an answer format that lets participants choose
 * one image from a fixed set of images in a single choice question.
 *
 * For example, you might use the image choice answer format to represent a range of moods that range
 * from very sad to very happy.
 *
 * The image choice answer format produces an `ChoiceQuestionResult` object.
 */

public class ImageChoiceAnswerFormat extends AnswerFormat {

    /**
     * An array of `ImageChoice` objects that represent the available choices.
     *
     * The text of the currently selected choice is displayed on screen. The text for
     * each choice is spoken by VoiceOver when an image is highlighted.
     */
    private List<ImageChoice> imageChoiceList;

    /* Default constructor needed for serialization/deserialization of object */
    public ImageChoiceAnswerFormat() {
        super();
    }

    /**
     * Returns an initialized image choice answer format using the specified array of images.
     *
     * @param imageChoiceList List of `ImageChoice` objects.
     *
     * @return An initialized image choice answer format.
     */
    public ImageChoiceAnswerFormat(List<ImageChoice> imageChoiceList) {
        this.imageChoiceList = imageChoiceList;
    }

    public List<ImageChoice> getImageChoiceList() {
        return imageChoiceList;
    }

    public void setImageChoiceList(List<ImageChoice> imageChoiceList) {
        this.imageChoiceList = imageChoiceList;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.ImageChoice;
    }

    /**
     * The `ImageChoice` class defines a choice that can be included in an `ImageChoiceAnswerFormat` object.
     *
     * Typically, image choices are displayed in a horizontal row, so you need to use appropriate sizes.
     * For example, when five image choices are displayed in an `ImageChoiceAnswerFormat`, image sizes
     * of about 45 to 60 points allow the images to look good in apps that run on all versions of iPhone.
     *
     * The text that describes an image choice should be reasonably short. However, only the text for the
     * currently selected image choice is displayed, so text that wraps to more than one line
     * is supported.
     */
    public static class ImageChoice implements Serializable {

        private String text;

        /**
         * The image to display when the choice is not selected.
         *
         * The size of the unselected image depends on the number of choices you need to display. As a
         * general rule, it's recommended that you start by creating an image that measures 44 x 44 points,
         * and adjust it if necessary.
         *
         * String representation of a drawable resource
         * for instance, if your image is R.drawable.image, this would just be "image"
         */
        private String normalImageRes;

        /**
         * The image to display when the choice is selected.
         *
         * For best results, the selected image should be the same size as the unselected image
         * (that is, the value of the `normalImageRes` member variable).
         * If you don't specify a selected image, the default tintColor is used to
         * indicate the selection state of the item.
         *
         * String representation of a drawable resource
         * for instance, if your image is R.drawable.image, this would just be "image"
         */
        private String selectedImageRes;

        /**
         * The value to return when the image is selected.
         * The value of this variable is expected to be a scalar property list type, such as `Number` or
         * `String`. If no value is provided, the index of the option as an 'Integer'
         * in the `ImageChoiceAnswerFormat` options list is used.
         */
        private Serializable value;

        /* Default constructor needed for serialization/deserialization of object */
        public ImageChoice() {
            super();
        }

        /**
         * Returns an image choice that includes the specified images and text.
         *
         * @param normalImageRes      The image drawable resource name to display in the unselected state.
         * @param selectedImageRes    The image drawable resource name to display in the selected state.
         * @param text                The text to display when the image is selected.
         * @param value               The value to record in a result object when the image is selected.
         *
         * @return An image choice instance.
         */
        public ImageChoice (String normalImageRes, String selectedImageRes, String text, Serializable value) {
            this.normalImageRes = normalImageRes;
            this.selectedImageRes = selectedImageRes;
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getNormalImageRes() {
            return normalImageRes;
        }

        public void setNormalImageRes(String normalImageRes) {
            this.normalImageRes = normalImageRes;
        }

        public String getSelectedImageRes() {
            return selectedImageRes;
        }

        public void setSelectedImageRes(String selectedImageRes) {
            this.selectedImageRes = selectedImageRes;
        }

        public Serializable getValue() {
            return value;
        }

        public void setValue(Serializable value) {
            this.value = value;
        }
    }
}

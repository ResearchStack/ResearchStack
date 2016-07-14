package org.researchstack.backbone.answerformat;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.body.BodyAnswer;

/**
 * Created by Anita on 6/15/2016.
 */
public class SliderAnswerFormat extends AnswerFormat {

    public static final String SOLID_VIEW = "solid";
    public static final String GRADIENT_VIEW = "gradient";
    public static final String TICKED_VIEW = "ticked";

    private int minVal;
    private int maxVal;
    private String color;
    private boolean showVal;
    private String minImage;
    private String maxImage;
    private String minText;
    private String maxText;
    private String sliderView;

    public SliderAnswerFormat(int minVal, int maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.sliderView = SOLID_VIEW;
        this.showVal = true;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Slider;
    }

    public BodyAnswer validateAnswer(Integer sliderOutput) {

        if (sliderOutput == null) {
            return BodyAnswer.INVALID;
        } else {
            if (sliderOutput < getMinVal()) {

                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_under,
                        String.valueOf(getMinVal()));

            } else if (sliderOutput > getMaxVal()) {

                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_over,
                        String.valueOf(getMaxVal()));
            }
        }
        return BodyAnswer.VALID;
    }

    public int getMinVal() {
        return minVal;
    }

    public void setMinVal(int minVal) {
        this.minVal = minVal;
    }

    public int getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isShowVal() {
        return showVal;
    }

    public void setShowVal(boolean showVal) {
        this.showVal = showVal;
    }

    public String getMinImage() {
        return minImage;
    }

    public void setMinImage(String minImage) {
        this.minImage = minImage;
    }

    public String getMaxImage() {
        return maxImage;
    }

    public void setMaxImage(String maxImage) {
        this.maxImage = maxImage;
    }

    public String getMinText() {
        return minText;
    }

    public void setMinText(String minText) {
        this.minText = minText;
    }

    public String getMaxText() {
        return maxText;
    }

    public void setMaxText(String maxText) {
        this.maxText = maxText;
    }

    public String getSliderView() {
        return sliderView;
    }

    public void setSliderView(String sliderView) {
        this.sliderView = sliderView;
    }

}

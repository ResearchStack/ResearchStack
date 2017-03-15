package org.researchstack.backbone.ui.step.body;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.ImageChoiceAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ResUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 3/14/17.
 *
 * The ImageChoiceBody displays "X" number of images horizontally in a LinearLayout,
 * these images act like RadioButtons, except, all of them can be deselected
 *
 * Also, hint text below the images changes based on which one is selected
 */

public class ImageChoiceBody implements StepBody {

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private ImageChoiceAnswerFormat answerFormat;
    private StepResult<Serializable> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private LinearLayout linearLayout;
    private List<ImageButton> imageViewList;
    private TextView hintTextView;

    private static final float DEFAULT_UNSELECTED_ALPHA = 0.5f;
    private float hintTextViewUnselectedAlpha = DEFAULT_UNSELECTED_ALPHA;

    private boolean hasSetSelectedColor;
    private int selectedColorWhenSelectedImageIsNull;

    private static final int NO_SELECTION = -1;
    private int selectedIndex;

    public ImageChoiceBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = (result == null) ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(R.layout.rsb_step_body_image_choice, parent, false);

        linearLayout = (LinearLayout)body.findViewById(R.id.rsb_step_body_image_choice_layout);
        hintTextView = (TextView) body.findViewById(R.id.rsb_step_body_image_choice_text_hint);
        hintTextView.setText(R.string.rsb_PLACEHOLDER_IMAGE_CHOICES);
        hintTextView.setAlpha(hintTextViewUnselectedAlpha);

        if (!(step.getAnswerFormat() instanceof ImageChoiceAnswerFormat)) {
            throw new IllegalStateException("ImageChoiceBody must have ImageChoiceAnswerFormat as the QuestionStep AnswerFormat");
        }

        answerFormat = (ImageChoiceAnswerFormat)step.getAnswerFormat();

        imageViewList = new ArrayList<>();
        selectedIndex = NO_SELECTION;
        linearLayout.setWeightSum(answerFormat.getImageChoiceList().size());
        for (int i = 0; i < answerFormat.getImageChoiceList().size(); i++) {
            ImageChoiceAnswerFormat.ImageChoice imageChoice = answerFormat.getImageChoiceList().get(i);
            ImageButton imageButton = (ImageButton)
                    inflater.inflate(R.layout.rsb_item_image_button, linearLayout, false);
            setNormalImage(imageButton, imageChoice);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            linearLayout.addView(imageButton, params);
            imageViewList.add(imageButton);
            setupImageViewClickListener(i, imageButton, imageChoice);
        }

        // Loop through and apply the step result if it exists
        if (result.getResult() != null) {
            for (int i = 0; i < answerFormat.getImageChoiceList().size(); i++) {
                ImageChoiceAnswerFormat.ImageChoice imageChoice = answerFormat.getImageChoiceList().get(i);
                if (imageChoice.getValue().equals(result.getResult())) {
                    imageViewList.get(i).callOnClick();
                }
            }
        }

        return body;
    }

    protected void setupImageViewClickListener(final int index,
                                               final ImageButton button,
                                               final ImageChoiceAnswerFormat.ImageChoice imageChoice) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex != NO_SELECTION) {
                    ImageButton oldButton = imageViewList.get(selectedIndex);
                    setNormalImage(oldButton, answerFormat.getImageChoiceList().get(selectedIndex));
                }
                // The previous selectedIndex was selected again, so deselect it
                if (selectedIndex == index) {
                    selectedIndex = NO_SELECTION;
                    setNormalImage(button, imageChoice);
                    hintTextView.setText(R.string.rsb_PLACEHOLDER_IMAGE_CHOICES);
                    hintTextView.setAlpha(hintTextViewUnselectedAlpha);
                    result.setResult(null);
                } else { // otherwise select the new image
                    selectedIndex = index;
                    setSelectedImage(button, imageChoice);
                    hintTextView.setText(imageChoice.getText());
                    hintTextView.setAlpha(1.0f);
                    result.setResult(imageChoice.getValue());
                }
            }
        });
    }

    protected void setNormalImage(ImageButton button, ImageChoiceAnswerFormat.ImageChoice imageChoice) {
        int drawableInt = ResUtils.getDrawableResourceId(button.getContext(), imageChoice.getNormalImageRes());
        if (drawableInt != 0) {
            button.setImageResource(drawableInt);
        }
    }

    protected void setSelectedImage(ImageButton button, ImageChoiceAnswerFormat.ImageChoice imageChoice) {
        int drawableInt = ResUtils.getDrawableResourceId(button.getContext(), imageChoice.getSelectedImageRes());
        if (drawableInt != 0) {
            button.setImageResource(drawableInt);
        } else {
            // If we do not have a selected image, we will just tint the current one to the primary color
            drawableInt = ResUtils.getDrawableResourceId(button.getContext(), imageChoice.getNormalImageRes());
            if (drawableInt != 0) {
                Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(button.getContext(), drawableInt));
                // Wrap the drawable so that future tinting calls work on pre-v21 devices. Always use the returned drawable.
                drawable = DrawableCompat.wrap(drawable);

                int color;
                if (hasSetSelectedColor) {
                    color = selectedColorWhenSelectedImageIsNull;
                } else {
                    color = ResourcesCompat.getColor(button.getResources(), R.color.rsb_colorPrimary, null);
                }

                // We can now set a tint, the "mutate()" is important as it makes sure the normal drawable remains untinted
                DrawableCompat.setTint(drawable.mutate(), color);
                button.setImageDrawable(drawable);
            }
        }
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (selectedIndex == NO_SELECTION) {
            return BodyAnswer.INVALID;
        }

        return BodyAnswer.VALID;
    }

    public float getHintTextViewUnselectedAlpha() {
        return hintTextViewUnselectedAlpha;
    }

    public void setHintTextViewUnselectedAlpha(float hintTextViewUnselectedAlpha) {
        this.hintTextViewUnselectedAlpha = hintTextViewUnselectedAlpha;
        if (hintTextView != null && selectedIndex == NO_SELECTION) {
            hintTextView.setAlpha(hintTextViewUnselectedAlpha);
        }
    }

    public int getSelectedColorWhenSelectedImageIsNull() {
        return selectedColorWhenSelectedImageIsNull;
    }

    public void setSelectedColorWhenSelectedImageIsNull(int selectedColorWhenSelectedImageIsNull) {
        hasSetSelectedColor = true;
        this.selectedColorWhenSelectedImageIsNull = selectedColorWhenSelectedImageIsNull;
    }
}

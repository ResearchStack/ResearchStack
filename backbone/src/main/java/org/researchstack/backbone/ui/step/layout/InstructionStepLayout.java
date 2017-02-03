package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;

public class InstructionStepLayout extends FixedSubmitBarLayout implements StepLayout {
    protected StepCallbacks callbacks;
    protected InstructionStep step;

    protected TextView titleTextView;
    protected TextView textTextView;
    protected AppCompatImageView imageView;
    protected TextView moreDetailTextView;

    public InstructionStepLayout(Context context) {
        super(context);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public InstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetStep(step);
        initializeStep();
    }

    protected void validateAndSetStep(Step step) {
        if (!(step instanceof InstructionStep)) {
            throw new IllegalStateException("InstructionStepLayout only works with InstructionStep");
        }
        this.step = (InstructionStep)step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_step_layout_instruction;
    }

    private void initializeStep() {

        titleTextView       = (TextView)findViewById(R.id.rsb_intruction_title);
        textTextView        = (TextView)findViewById(R.id.rsb_intruction_text);
        imageView           = (AppCompatImageView) findViewById(R.id.rsb_image_view);
        moreDetailTextView  = (TextView)findViewById(R.id.rsb_instruction_more_detail_text);

        if (step != null) {
            String title = step.getTitle();
            String text  = step.getText();

            if (TextUtils.isEmpty(title) &&
                !TextUtils.isEmpty(text) && !TextUtils.isEmpty(step.getMoreDetailText()))
            {
                // With no Title, we can assume text and detail text is equla to title and text
                title = text;
                text = step.getMoreDetailText();
            }

            // Set Title
            if (! TextUtils.isEmpty(title)) {
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(title);
            }

            // Set Summary
            if(! TextUtils.isEmpty(text)) {
                textTextView.setVisibility(View.VISIBLE);
                textTextView.setText(Html.fromHtml(text));
                final String htmlDocTitle = title;
                textTextView.setMovementMethod(new TextViewLinkHandler() {
                    @Override
                    public void onLinkClick(String url) {
                        String path = ResourcePathManager.getInstance().
                                generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url);
                        Intent intent = ViewWebDocumentActivity.newIntentForPath(
                                getContext(), htmlDocTitle, path);
                        getContext().startActivity(intent);
                    }
                });
            }

            // Set Next / Skip
            submitBar.setPositiveTitle(R.string.rsb_next);
            submitBar.setPositiveAction(v -> onComplete());

            if (step.isOptional()) {
                submitBar.setNegativeTitle(R.string.rsb_step_skip);
                submitBar.setNegativeAction(v -> {
                    if (callbacks != null) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                    }
                });
            } else {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }

            refreshImage(step.getImage(), step.getIsImageAnimated());
            refreshDetailText(step.getMoreDetailText(), moreDetailTextView.getCurrentTextColor());
        }
    }

    protected void refreshImage(String imageName, boolean isAnimated) {
        // Setup the Imageview, is compatible with normal, vector, and animated drawables
        if (imageName != null) {
            int drawableInt = ResUtils.getDrawableResourceId(getContext(), imageName);
            if (drawableInt != 0) {

                // TODO: is there anyway to automatically check if an image is animatible
                // TODO: other than setting a flag on the Step?
                // TODO: catch exceptions maybe?
                if (isAnimated) {
                    AnimatedVectorDrawableCompat animatedVector =
                            AnimatedVectorDrawableCompat.create(getContext(), drawableInt);
                    imageView.setImageDrawable(animatedVector);
                    if (animatedVector != null) {
                        animatedVector.start();
                    }
                } else {
                    imageView.setImageResource(drawableInt);
                }

                imageView.setVisibility(View.VISIBLE);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    protected void refreshDetailText(String detailText, int detailTextColor) {
        moreDetailTextView.setVisibility(detailText == null ? View.GONE : View.VISIBLE);
        if (detailText != null) {
            moreDetailTextView.setText(detailText);
        }
        moreDetailTextView.setTextColor(detailTextColor);
    }

    protected void onComplete() {
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
    }
}

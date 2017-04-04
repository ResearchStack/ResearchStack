package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;

public class InstructionStepLayout extends FixedSubmitBarLayout implements StepLayout {
    protected StepCallbacks callbacks;

    protected InstructionStep instructionStep;
    protected Step step;

    protected TextView  titleTextView;
    protected TextView  textTextView;
    protected ImageView imageView;
    protected TextView  moreDetailTextView;

    protected Handler mainHandler;
    protected Runnable animationRepeatRunnbale;

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
        this.instructionStep = (InstructionStep)step;
        this.step = step;
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

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initializeStep() {

        titleTextView       = (TextView)findViewById(R.id.rsb_intruction_title);
        textTextView        = (TextView)findViewById(R.id.rsb_intruction_text);
        imageView           = (ImageView) findViewById(R.id.rsb_image_view);
        moreDetailTextView  = (TextView)findViewById(R.id.rsb_instruction_more_detail_text);

        if (step != null) {
            String title = step.getTitle();
            String text  = step.getText();

            if (TextUtils.isEmpty(title) &&
                !TextUtils.isEmpty(text) && !TextUtils.isEmpty(instructionStep.getMoreDetailText()))
            {
                // With no Title, we can assume text and detail text is equla to title and text
                title = text;
                text = instructionStep.getMoreDetailText();
            }

            // Set Title
            if (! TextUtils.isEmpty(title)) {
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(title);
            }

            // Set Summary
            if(! TextUtils.isEmpty(text)) {
                textTextView.setVisibility(View.VISIBLE);

                // There is an odd bug where endlines do not show up with Html.fromHtml correctly,
                // so we should use the old school text when we find one and assume it is not html,
                // because html does not use "\n" it uses line breaks
                if (text.contains("\n")) {
                    textTextView.setText(text);
                } else {
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
            }

            // Set Next / Skip
            submitBar.setVisibility(View.VISIBLE);
            submitBar.setPositiveTitle(R.string.rsb_next);
            submitBar.setPositiveAction(v -> onComplete());

            if (instructionStep.getSubmitBarNegativeActionSkipRule() != null) {
                final InstructionStep.SubmitBarNegativeActionSkipRule rule =
                        instructionStep.getSubmitBarNegativeActionSkipRule();
                submitBar.setNegativeTitle(rule.getTitle());
                submitBar.setNegativeAction(v -> {
                    StepResult stepResult = new StepResult(step);
                    rule.onNegativeActionClicked(instructionStep, stepResult);
                    if (callbacks != null) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                    }
                });
            } else if (step.isOptional()) {
                submitBar.setNegativeTitle(R.string.rsb_step_skip);
                submitBar.setNegativeAction(v -> {
                    if (callbacks != null) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                    }
                });
            } else {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }

            refreshImage(instructionStep.getImage(), instructionStep.getIsImageAnimated());
            refreshDetailText(instructionStep.getMoreDetailText(), moreDetailTextView.getCurrentTextColor());
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
                    final AnimatedVectorDrawableCompat animatedVector =
                            AnimatedVectorDrawableCompat.create(getContext(), drawableInt);
                    imageView.setImageDrawable(animatedVector);
                    if (animatedVector != null) {
                        animatedVector.start();
                        startAnimationRepeat(animatedVector);
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

    protected void startAnimationRepeat(final AnimatedVectorDrawableCompat animatedVector) {
        if (instructionStep.getAnimationRepeatDuration() > 0) {
            if (mainHandler == null) {
                mainHandler = new Handler();
            }
            mainHandler.removeCallbacksAndMessages(null);
            final long repeatDuration = instructionStep.getAnimationRepeatDuration();
            animationRepeatRunnbale = new Runnable() {
                @Override
                public void run() {
                    animatedVector.stop();
                    animatedVector.start();
                    mainHandler.postDelayed(animationRepeatRunnbale, repeatDuration);
                }
            };
            mainHandler.postDelayed(animationRepeatRunnbale, repeatDuration);
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

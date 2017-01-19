package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by TheMDP on 1/18/17.
 */

public class CompletionStepLayout extends InstructionStepLayout {

    public CompletionStepLayout(Context context) {
        super(context);
    }

    public CompletionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // TODO: show animated check mark
}

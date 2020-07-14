package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class FixedSubmitBarLayout extends ConstraintLayout implements StepLayout {
    public FixedSubmitBarLayout(Context context) {
        super(context);
        init();
    }

    public FixedSubmitBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixedSubmitBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public abstract int getContentResourceId();

    private void init() {
        // Init root
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.rsb_layout_fixed_submit_bar, this, true);

        // Add contentContainer to the layout
        ViewGroup contentContainer = findViewById(R.id.rsb_content_container);
        inflater.inflate(getContentResourceId(), contentContainer, true);
    }
}

package org.researchstack.backbone.ui.graph;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;

public class EmptyView extends FrameLayout {
    public EmptyView(Context context) {
        super(context);
        init(null, R.attr.emptyviewStyle, R.style.Widget_Backbone_EmptyView);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.emptyviewStyle);
        init(attrs, R.attr.emptyviewStyle, R.style.Widget_Backbone_EmptyView);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Widget_Backbone_EmptyView);
    }

    @TargetApi(21)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_empty, this, true);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.EmptyView,
                defStyleAttr,
                defStyleRes);

        TextView emptyTextView = (TextView) findViewById(R.id.view_empty_tv);
        String emptyText = a.getString(R.styleable.EmptyView_emptyText);
        emptyTextView.setText(emptyText);

        int emptyTextAppearance = a.getResourceId(R.styleable.EmptyView_emptyTextAppearance, 0);
        emptyTextView.setTextAppearance(emptyTextView.getContext(), emptyTextAppearance);

        int emptyIconTint = a.getColor(R.styleable.EmptyView_emptyIconTint, 0);
        Drawable emptyIcon = a.getDrawable(R.styleable.EmptyView_emptyIcon);

        if (emptyIcon != null) {
            if (emptyIconTint != 0) {
                emptyIcon = DrawableCompat.wrap(emptyIcon);
                DrawableCompat.setTint(emptyIcon, emptyIconTint);
            }

            emptyTextView.setCompoundDrawablesWithIntrinsicBounds(null, emptyIcon, null, null);
        }

        a.recycle();
    }
}

package org.researchstack.backbone.ui.views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * This extension of a {@link NestedScrollView} exists solely to support API 23<
 * The only addition is the proxy scroll listener that the original ScrollView didn't have without extending and that
 * NestedScrollView only offers to API 23+.
 * This class should be removed once the min API is increased to 23 and callers should use a pure NestedScrollView.
 */
public class ObservableScrollView extends NestedScrollView {
    @Nullable
    private OnScrollListener onScrollListener;

    public ObservableScrollView(@NonNull final Context context) {
        super(context);
    }

    public ObservableScrollView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(t);
        }
    }

    public void setScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    public interface OnScrollListener {
        void onScrollChanged(int scrollY);
    }
}
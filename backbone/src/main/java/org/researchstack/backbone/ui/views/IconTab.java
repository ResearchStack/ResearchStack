package org.researchstack.backbone.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.R;


public class IconTab extends RelativeLayout implements View.OnLongClickListener {
    private TextView title;
    private ImageView icon;
    private ImageView indicator;

    public IconTab(Context context) {
        super(context);
        init();
    }

    public IconTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public IconTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_icon_tab, this, true);

        title = (TextView) findViewById(R.id.title);
        icon = (ImageView) findViewById(R.id.icon);
        indicator = (ImageView) findViewById(R.id.indicator);

        // Adjust visibility and layout params
        adjustSelectedView();

        // Show anchored toast when long pressing in selected state
        setOnLongClickListener(this);
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        adjustSelectedView();
    }

    private void adjustSelectedView() {
        if (getParent() == null) {
            icon.setAlpha(isSelected() ? 1f : 0f);
            title.setAlpha(isSelected() ? 0 : 1);
            adjustIndicatorPosition();
        } else {
            indicator.animate().setStartDelay(0).alpha(0).withEndAction(() -> {
                adjustIndicatorPosition();
                indicator.animate().setStartDelay(200).alpha(1);
            });

            icon.animate()
                    .alpha(isSelected() ? 1 : 0)
                    .scaleX(isSelected() ? 1 : 0)
                    .scaleY(isSelected() ? 1 : 0)
                    .setDuration(150)
                    .setInterpolator(isSelected() ? new OvershootInterpolator() : new AccelerateInterpolator())
                    .setStartDelay(isSelected() ? 150 : 0);

            title.animate()
                    .alpha(isSelected() ? 0 : 1)
                    .setDuration(150)
                    .setStartDelay(isSelected() ? 0 : 150);
        }
    }

    private void adjustIndicatorPosition() {
        View anchor = isSelected() ? icon : title;
        LayoutParams params = (LayoutParams) indicator.getLayoutParams();
        params.addRule(ALIGN_TOP, anchor.getId());
        params.addRule(ALIGN_RIGHT, anchor.getId());
        indicator.requestLayout();
    }

    public void setText(@StringRes int textResId) {
        title.setText(textResId);
    }

    public void setTextColor(int textColor) {
        title.setTextColor(textColor);
    }

    public void setIcon(@DrawableRes int iconResId) {
        icon.setImageResource(iconResId);
    }

    public void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
    }

    public void setIconTint(int iconTint) {
        Drawable drawable = icon.getDrawable();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, iconTint);
        icon.setImageDrawable(drawable);
    }

    public void setIsIndicatorShow(boolean visible) {
        indicator.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setIndicatorTint(int indicatorTint) {
        Drawable drawable = indicator.getDrawable();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, indicatorTint);
        indicator.setImageDrawable(drawable);
    }

    @Override
    public boolean onLongClick(View v) {
        if (isSelected()) {
            final int[] screenPos = new int[2];
            getLocationOnScreen(screenPos);

            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            Toast cheatSheet = Toast.makeText(context, title.getText(), Toast.LENGTH_SHORT);
            // Show under the tab
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    (screenPos[0] + width / 2) - screenWidth / 2,
                    screenPos[1] + height);

            cheatSheet.show();
            return true;
        }
        return false;
    }
}

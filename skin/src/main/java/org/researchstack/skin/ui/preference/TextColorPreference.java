package org.researchstack.skin.ui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import org.researchstack.skin.R;

public class TextColorPreference extends Preference {
    private TextView titleView;
    private int color;

    public TextColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public TextColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public TextColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TextColorPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextColorPreference,
                defStyleAttr,
                R.style.TextColorPreference);

        color = a.getColor(R.styleable.TextColorPreference_preferenceTextColor, Color.BLACK);

        a.recycle();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        titleView = (TextView) holder.itemView.findViewById(android.R.id.title);
        titleView.setTextColor(color);
    }

    public void setTitleColor(int color) {
        this.color = color;

        if (titleView != null) {
            titleView.setTextColor(color);
        }

    }
}

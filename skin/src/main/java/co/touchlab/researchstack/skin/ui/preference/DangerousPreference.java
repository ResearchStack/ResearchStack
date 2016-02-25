package co.touchlab.researchstack.skin.ui.preference;
import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import co.touchlab.researchstack.glue.R;

public class DangerousPreference extends Preference
{
    public DangerousPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DangerousPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public DangerousPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DangerousPreference(Context context)
    {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);

        TextView titleView = (TextView) holder.itemView.findViewById(android.R.id.title);
        titleView.setTextColor(getContext().getResources().getColor(R.color.error));
    }
}

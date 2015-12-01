package co.touchlab.researchstack.glue.ui.views;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.common.model.StudyOverviewModel;
import co.touchlab.researchstack.glue.ui.ViewVideoActivity;

public class StudyVideoLayout extends ScrollView
{

    private TextView titleView;
    private TextView subtitleView;
    private ImageButton videoButton;

    public StudyVideoLayout(Context context)
    {
        super(context);
        init();
    }

    public StudyVideoLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public StudyVideoLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_study_video, this, true);

        titleView = (TextView) findViewById(R.id.layout_studyoverview_video_title);
        subtitleView = (TextView) findViewById(R.id.layout_studyoverview_video_subtitle);
        videoButton = (ImageButton) findViewById(R.id.layout_studyoverview_video_logo);
    }

    public void setData(StudyOverviewModel.Question data)
    {
        titleView.setText(data.getTitle());
        subtitleView.setText(data.getDetails());
        videoButton.setOnClickListener(v -> {
            Intent intent = ViewVideoActivity.newIntent(getContext(), data.getVideoName());
            getContext().startActivity(intent);
        });

        int color = Color.parseColor(data.getTintColor());
        videoButton.setColorFilter(color);
        videoButton.setImageResource(R.drawable.video_icon);
    }
}

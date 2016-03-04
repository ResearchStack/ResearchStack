package org.researchstack.skin.ui.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.StudyOverviewModel;
import org.researchstack.skin.utils.ConsentFormUtils;

public class StudyLandingLayout extends ScrollView
{

    private TextView  titleView;
    private TextView  subtitleView;
    private ImageView logoView;
    private Button    readConsent;
    private Button    emailConsent;

    public StudyLandingLayout(Context context)
    {
        super(context);
        init();
    }

    public StudyLandingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public StudyLandingLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_study_landing, this, true);

        logoView = (ImageView) findViewById(R.id.layout_studyoverview_landing_logo);
        titleView = (TextView) findViewById(R.id.layout_studyoverview_landing_title);
        subtitleView = (TextView) findViewById(R.id.layout_studyoverview_landing_subtitle);
    }

    public void setData(StudyOverviewModel.Question data)
    {
        logoView.setImageResource(ResourceManager.getInstance().getLargeLogoDiseaseIcon());

        titleView.setText(data.getTitle());
        if(! TextUtils.isEmpty(data.getDetails()))
        {
            subtitleView.setText(data.getDetails());
        }
        else
        {
            subtitleView.setVisibility(View.GONE);
        }

        if("yes".equals(data.getShowConsent()))
        {
            readConsent.setOnClickListener(v -> {
                ConsentFormUtils.viewConsentForm(getContext());
            });
        }
        else
        {
            readConsent.setVisibility(View.GONE);
        }

        emailConsent.setOnClickListener(v -> {
            ConsentFormUtils.shareConsentForm(getContext());
        });
    }

}

package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.StudyOverviewModel;

public class StudyLandingLayout extends ScrollView
{

    private TextView titleView;
    private TextView subtitleView;
    private ImageView logoView;
    private Button readConsent;
    private Button emailConsent;

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
        readConsent = (Button) findViewById(R.id.layout_studyoverview_landing_read);
        emailConsent = (Button) findViewById(R.id.layout_studyoverview_landing_email);
    }

    public void setData(StudyOverviewModel.Question data)
    {
        logoView.setImageResource(AppDelegate.getInstance().getLargeLogoDiseaseIcon());

        titleView.setText(data.getTitle());
        subtitleView.setText(data.getDetails());

        readConsent.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Open PDF Viewer, WebView?", Toast.LENGTH_SHORT).show();

        });

        //TODO Add consent doc, change emial, subject, text
        emailConsent.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, "email@address.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "CONSENT");
            intent.putExtra(Intent.EXTRA_TEXT, "BODY");
            intent.putExtra(Intent.EXTRA_STREAM, "URI");

            String title = getContext().getString(R.string.send_email);
            getContext().startActivity(Intent.createChooser(intent, title));
        });

    }
}

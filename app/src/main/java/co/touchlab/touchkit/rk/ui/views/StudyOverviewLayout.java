package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.FrameLayout;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.StudyOverviewModel;

public class StudyOverviewLayout extends FrameLayout
{

    private WebView webView;

    public StudyOverviewLayout(Context context)
    {
        super(context);
        init();
    }

    public StudyOverviewLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public StudyOverviewLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_study_overview, this, true);
        webView = (WebView) findViewById(R.id.webview);
    }

    public void setData(StudyOverviewModel.Question data)
    {
        String uri = "file:///android_res/raw/" + data.getDetails() + ".html";
        webView.loadUrl(uri);
    }
}

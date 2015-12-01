package co.touchlab.researchstack.glue.ui.views;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStackApplication;
import co.touchlab.researchstack.glue.model.StudyOverviewModel;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.core.ui.scene.Scene;

/**
 * TODO Extend from {@link Scene} and use in {@link ViewWebDocumentActivity}
 */
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
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
        });
    }

    public void setData(StudyOverviewModel.Question data)
    {
        String uri = ResearchStackApplication.getInstance().getHTMLFilePath(data.getDetails());
        webView.loadUrl(uri);
    }
}

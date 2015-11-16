package co.touchlab.touchkit.rk.ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.TouchKitApplication;

public class ViewWebDocumentActivity extends AppCompatActivity
{

    public static final String TAG = ViewWebDocumentActivity.class.getSimpleName();
    public static final String KEY_DOC_NAME = TAG + ".DOC_NAME";
    public static final String KEY_TITLE = TAG + ".TITLE";

    public static Intent newIntent(Context context, String title, String docName)
    {
        Intent intent = new Intent(context, ViewWebDocumentActivity.class);
        intent.putExtra(KEY_DOC_NAME, docName);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (savedInstanceState == null)
        {
            super.overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_50);
        }

        super.onCreate(savedInstanceState);

        /**
         * TODO use {@link co.touchlab.touchkit.rk.ui.views.StudyOverviewLayout}
         * TODO This layout is the same as {@link co.touchlab.touchkit.rk.R.layout.layout_study_overview}. Consolidate.
         */
        super.setContentView(R.layout.activity_web_document);

        String title = getIntent().getStringExtra(KEY_TITLE);
        setTitle(title);

        String documentName = getIntent().getStringExtra(KEY_DOC_NAME);
        String path = TouchKitApplication.getInstance().getHTMLFilePath(documentName);

        WebView webView = (WebView) findViewById(R.id.webview);
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

        webView.loadUrl(path);
    }

    @Override
    public void finish()
    {
        super.finish();
        super.overridePendingTransition(R.anim.fade_in_50, R.anim.slide_out_down);
    }
}

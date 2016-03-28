package org.researchstack.backbone.ui.views;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.ui.ViewVideoActivity;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;

public class LocalWebView extends WebView
{
    public interface LocalWebViewCallbacks
    {

        void onTitleLoaded(String title);
    }

    private static final String SCHEMA_LOCAL_HTML = "file:///android_res/raw/";

    private LocalWebViewCallbacks callbacks;

    public LocalWebView(Context context)
    {
        super(context);
        init();
    }

    public LocalWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LocalWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                LogExt.i(getClass(), url);

                // Check if we should load local html / video
                if(url.startsWith(SCHEMA_LOCAL_HTML))
                {
                    String file = url.substring(SCHEMA_LOCAL_HTML.length(), url.length());
                    String fileName = file.split("\\.")[0];

                    if (file.endsWith(".pdf"))
                    {
                        throw new UnsupportedOperationException("LocalWebView does not currently " +
                                "support viewing PDF files. Its suggested you generate HTML version" +
                                " of PDF for viewing");
                    }
                    else if (file.endsWith(".mp4"))
                    {
                        Intent intent = ViewVideoActivity.newIntent(getContext(), fileName);
                        getContext().startActivity(intent);
                    }
                    else
                    {
                        Intent intent = ViewWebDocumentActivity.newIntent(getContext(), null, fileName);
                        getContext().startActivity(intent);
                    }
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                }

                return true;
            }
        });

        setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                if(callbacks != null)
                {
                    callbacks.onTitleLoaded(view.getTitle());
                }
            }
        });
    }

    public void setCallbacks(LocalWebViewCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

}

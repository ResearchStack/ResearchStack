package co.touchlab.researchstack.core.ui.views;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LocalWebView extends WebView
{

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
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext()
                    .startActivity(intent);
                return true;
            }
        });
    }

}

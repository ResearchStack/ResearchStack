package co.touchlab.researchstack.core.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.touchlab.researchstack.core.ui.views.LocalWebView;
import co.touchlab.researchstack.core.utils.ResUtils;

public class ViewWebDocumentActivity extends AppCompatActivity
{

    public static final String TAG          = ViewWebDocumentActivity.class.getSimpleName();
    public static final String KEY_DOC_NAME = TAG + ".DOC_NAME";
    public static final String KEY_TITLE    = TAG + ".TITLE";

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
        super.onCreate(savedInstanceState);

        String documentName = getIntent().getStringExtra(KEY_DOC_NAME);
        String path = ResUtils.getHTMLFilePath(documentName);

        LocalWebView webView = new LocalWebView(this);
        webView.loadUrl(path);
        setContentView(webView);

        String title = getIntent().getStringExtra(KEY_TITLE);
        setTitle(title);
    }
}

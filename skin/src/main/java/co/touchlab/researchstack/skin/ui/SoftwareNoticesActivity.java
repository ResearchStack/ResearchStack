package co.touchlab.researchstack.skin.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import co.touchlab.researchstack.backbone.ui.views.LocalWebView;
import co.touchlab.researchstack.backbone.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ResourceManager;

public class SoftwareNoticesActivity extends AppCompatActivity
{

    public static Intent newIntent(Context context)
    {
        return new Intent(context, SoftwareNoticesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_software_notices);

        int resId = ResourceManager.getInstance().getSoftwareNotices();
        String documentName = getResources().getResourceEntryName(resId);
        String path = ResUtils.getHTMLFilePath(documentName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LocalWebView webView = (LocalWebView) findViewById(R.id.webview);
        webView.loadUrl(path);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package org.researchstack.backbone.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.views.LocalWebView;
import org.researchstack.backbone.utils.ThemeUtils;

import java.net.URL;

/**
 * The ViewWebDocumentActivity is used for viewing both local and network HTML docs. This activity
 * has the ability to be themed by the calling activity. You are able to ignore this, and use the
 * default manifest theme by calling {@link #newIntentForPath(Context, String, String)}.
 */
public class ViewWebDocumentActivity extends AppCompatActivity {
    public static final String TAG = ViewWebDocumentActivity.class.getSimpleName();
    public static final String KEY_DOC_PATH = TAG + ".DOC_PATH";
    public static final String KEY_DOC_CONTENT = TAG + ".DOC_CONTENT";
    public static final String KEY_TITLE = TAG + ".TITLE";
    public static final String KEY_THEME = TAG + ".THEME";
    public static final String KEY_COLOR_PRIMARY = "colorPrimary";
    public static final String KEY_COLOR_PRIMARY_DARK = "colorPrimaryDark";
    public static final String KEY_CONTENT_URL = TAG + ".URL";

    public static Intent newIntentForContent(Context context, String title, URL contentUrl, boolean useCallingTheme) {
        Intent intent = newIntent(context, title, useCallingTheme);
        intent.putExtra(KEY_CONTENT_URL, contentUrl.toString());
        return intent;
    }

    public static Intent newIntentForContent(Context context, String title, String htmlContent) {
        return newIntentForContent(context, title, htmlContent, true);
    }

    public static Intent newIntentForContent(Context context, String title, String htmlContent, boolean useCallingTheme) {
        Intent intent = newIntent(context, title, useCallingTheme);
        intent.putExtra(KEY_DOC_CONTENT, htmlContent);
        return intent;
    }

    public static Intent newIntentForPath(Context context, String title, String absDocPath) {
        return newIntentForPath(context, title, absDocPath, true);
    }

    public static Intent newIntentForPath(Context context, String title, String absDocPath, boolean useCallingTheme) {
        Intent intent = newIntent(context, title, useCallingTheme);
        intent.putExtra(KEY_DOC_PATH, absDocPath);
        return intent;
    }

    private static Intent newIntent(Context context, String title, boolean useCallingTheme) {
        Intent intent = new Intent(context, ViewWebDocumentActivity.class);
        intent.putExtra(KEY_TITLE, title);
        if (useCallingTheme) {
            int theme = ThemeUtils.getTheme(context);
            if (theme != 0) {
                intent.putExtra(KEY_THEME, ThemeUtils.getTheme(context));
            }
        }
        return intent;
    }

    public static void addThemeColors(Intent intent, int colorPrimary, int colorPrimaryDark) {
        intent.putExtra(KEY_COLOR_PRIMARY, colorPrimary);
        intent.putExtra(KEY_COLOR_PRIMARY_DARK, colorPrimaryDark);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().hasExtra(KEY_THEME)) {
            setTheme(getIntent().getIntExtra(KEY_THEME, 0));
        }
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.rsb_activity_web_document);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try
        {
            setSupportActionBar(toolbar);
        } catch (Exception e)
        {
            //there is already an action bar
            toolbar.setVisibility(View.GONE);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(KEY_COLOR_PRIMARY)) {
            setToolbarTheme(getIntent().getIntExtra(KEY_COLOR_PRIMARY, Color.GRAY));
        }

        if (getIntent().hasExtra(KEY_COLOR_PRIMARY_DARK)) {
            setNotificationBarTheme(getIntent().getIntExtra(KEY_COLOR_PRIMARY_DARK, Color.DKGRAY));
        }

        if (getIntent().hasExtra(KEY_TITLE)) {
            String title = getIntent().getStringExtra(KEY_TITLE);
            actionBar.setTitle(title);
        }

        LocalWebView webView = (LocalWebView) findViewById(R.id.webview);

        if (getIntent().hasExtra(KEY_DOC_PATH))
        {
            String docPath = getIntent().getStringExtra(KEY_DOC_PATH);
            webView.loadUrl(docPath);
        }
        else if (getIntent().hasExtra(KEY_DOC_CONTENT))
        {
            String docContent = getIntent().getStringExtra(KEY_DOC_CONTENT);
//            webView.loadData(docContent, "text/html", "UTF-8");
            webView.loadDataWithBaseURL(null, docContent, "text/html", "UTF-8", null);
        }
        else if (getIntent().hasExtra(KEY_CONTENT_URL))
        {
            webView.loadUrl(getIntent().getStringExtra(KEY_CONTENT_URL));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbarTheme(int primaryColor) {
        runOnUiThread(() -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(primaryColor));
            }
        });
    }

    private void setNotificationBarTheme(int primaryColorDark) {
        runOnUiThread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();

                if (primaryColorDark == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                }
                window.setStatusBarColor(primaryColorDark);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}

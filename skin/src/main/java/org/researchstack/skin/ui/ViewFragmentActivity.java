package org.researchstack.skin.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.utils.ViewUtils;
import org.researchstack.skin.R;

@Deprecated
public class ViewFragmentActivity extends PinCodeActivity
{
    public static final String TAG            = ViewFragmentActivity.class.getSimpleName();
    public static final String KEY_TITLE      = TAG + ".TITLE";
    public static final String KEY_FRAG_CLASS = TAG + ".DOC_NAME";

    public static Intent newIntent(Context context, String title, String fragmentClass)
    {
        Intent intent = new Intent(context, ViewFragmentActivity.class);
        intent.putExtra(KEY_FRAG_CLASS, fragmentClass);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        String title = getIntent().getStringExtra(KEY_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null)
        {
            // Add Fragment
            String fragmentClass = getIntent().getStringExtra(KEY_FRAG_CLASS);
            Fragment fragment = ViewUtils.createFragment(fragmentClass);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, fragmentClass)
                    .commit();
        }
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

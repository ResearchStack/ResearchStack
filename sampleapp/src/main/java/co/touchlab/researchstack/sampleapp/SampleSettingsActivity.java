package co.touchlab.researchstack.sampleapp;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import co.touchlab.researchstack.backbone.ui.PinCodeActivity;

public class SampleSettingsActivity extends PinCodeActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(co.touchlab.researchstack.glue.R.layout.activity_fragment);

        Toolbar toolbar = (Toolbar) findViewById(co.touchlab.researchstack.glue.R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(co.touchlab.researchstack.glue.R.id.container, new SampleSettingsFragment())
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

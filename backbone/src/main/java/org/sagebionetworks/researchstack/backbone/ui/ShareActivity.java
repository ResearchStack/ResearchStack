package org.sagebionetworks.researchstack.backbone.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.sagebionetworks.researchstack.backbone.UiManager;


public class ShareActivity extends BaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(org.sagebionetworks.researchstack.backbone.R.layout.rsb_activity_fragment);

    Toolbar toolbar = (Toolbar) findViewById(org.sagebionetworks.researchstack.backbone.R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    if(savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction()
              .add(org.sagebionetworks.researchstack.backbone.R.id.container, UiManager.getInstance().getShareFragment())
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
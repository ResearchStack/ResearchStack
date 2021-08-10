package org.researchstack.backbone.ui;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import org.researchstack.backbone.UiManager;


public class ShareActivity extends BaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(org.researchstack.backbone.R.layout.rsb_activity_fragment);

    Toolbar toolbar = (Toolbar) findViewById(org.researchstack.backbone.R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    if(savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction()
              .add(org.researchstack.backbone.R.id.container, UiManager.getInstance().getShareFragment())
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
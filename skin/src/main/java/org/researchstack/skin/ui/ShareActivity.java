package org.researchstack.skin.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.researchstack.skin.UiManager;


public class ShareActivity extends BaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(org.researchstack.skin.R.layout.rss_activity_fragment);

    Toolbar toolbar = (Toolbar) findViewById(org.researchstack.skin.R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    if(savedInstanceState == null)
    {
      getSupportFragmentManager().beginTransaction()
              .add(org.researchstack.skin.R.id.container, UiManager.getInstance().getShareFragment())
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
package co.touchlab.researchstack.glue.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.glue.ui.fragment.ActivitiesFragment;
import co.touchlab.researchstack.glue.ui.fragment.DashboardFragment;
import co.touchlab.researchstack.glue.ui.fragment.LearnFragment;
import co.touchlab.researchstack.glue.ui.fragment.ProfileFragment;
import co.touchlab.researchstack.glue.ui.fragment.SettingsFragment;

/**
 * Created by bradleymcdermott on 10/27/15.
 */
public class MainActivity extends PassCodeActivity
{

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item)
                    {
                        int id = item.getItemId();
                        Fragment fragment;
                        if(id == R.id.dashboard)
                        {
                            LogExt.d(getClass(), "Dashboard clicked");
                            toolbar.setTitle(R.string.dashboard);
                            fragment = new DashboardFragment();

                        }
                        else if(id == R.id.learn)
                        {
                            LogExt.d(getClass(), "Learn clicked");
                            toolbar.setTitle(R.string.learn);
                            fragment = new LearnFragment();

                        }
                        else if(id == R.id.profile)
                        {
                            LogExt.d(getClass(), "Profile clicked");
                            toolbar.setTitle(R.string.profile);
                            fragment = new ProfileFragment();

                        }
                        else if(id == R.id.settings)
                        {
                            LogExt.d(getClass(), "Settings clicked");
                            toolbar.setTitle(R.string.settings);
                            fragment = new SettingsFragment();

                        }
                        else
                        {
                            LogExt.d(getClass(), "Activities/Default clicked");
                            toolbar.setTitle(R.string.activities);
                            fragment = new ActivitiesFragment();

                        }
                        showFragment(fragment);
                        navigationView.setCheckedItem(id);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

        View headerView = getLayoutInflater().inflate(R.layout.include_user_header,
                null);

        AppCompatTextView name = (AppCompatTextView) headerView.findViewById(R.id.name);
        name.setText(ResearchStack.getInstance()
                .getCurrentUser()
                .getName());
        AppCompatTextView email = (AppCompatTextView) headerView.findViewById(R.id.email);
        email.setText(ResearchStack.getInstance().getCurrentUser().getEmail());

        ImageView image = (ImageView) headerView.findViewById(R.id.profile_image);
        image.setOnLongClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Clear saved user data?")
                    .setPositiveButton("Yes",
                            (dialog, which) -> {
                                ResearchStack.getInstance()
                                        .clearUserData(MainActivity.this);
                                dialog.dismiss();
                                finish();
                                System.exit(0);
                            })
                    .setNegativeButton("No",
                            ((dialog1, which1) -> {
                                dialog1.dismiss();
                            }))
                    .show();
            return false;
        });

        navigationView.addHeaderView(headerView);

        initFileAccess();
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();
        Log.w("asdf", "onDataReady: " + getClass().getSimpleName());
        showFragment(new ActivitiesFragment());
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();
        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

    // TODO better fragment loading/switching logic, use tags
    private void showFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder,
                        fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

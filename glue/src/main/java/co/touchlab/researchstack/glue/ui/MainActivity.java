package co.touchlab.researchstack.glue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Constructor;

import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.glue.NavigationItem;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;

/**
 * Created by bradleymcdermott on 10/27/15.
 */
public class MainActivity extends PassCodeActivity
{

    private DrawerLayout   drawerLayout;
    private NavigationView navigationView;

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
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getIntent() != null)
            {
                String className = item.getIntent().getComponent().getClassName();
                showFragment(className);

                //TODO remove delay call once I/O is offloaded onto separate thread w/in fragments
                navigationView.postDelayed(drawerLayout:: closeDrawers, 100);
            }

            actionBar.setTitle(item.getTitle());

            return true;
        });
        initNavigationMenu(navigationView);

        View headerView = getLayoutInflater().inflate(R.layout.include_user_header, null);

        // TODO set header from user data
        AppCompatTextView name = (AppCompatTextView) headerView.findViewById(R.id.name);
        AppCompatTextView email = (AppCompatTextView) headerView.findViewById(R.id.email);

        ImageView image = (ImageView) headerView.findViewById(R.id.profile_image);
        image.setOnLongClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this).setMessage("Clear saved user data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        ResearchStack.getInstance().clearUserData(MainActivity.this);
                        dialog.dismiss();
                        finish();
                        System.exit(0);
                    })
                    .setNegativeButton("No", ((dialog1, which1) -> {
                        dialog1.dismiss();
                    }))
                    .show();
            return false;
        });

        navigationView.addHeaderView(headerView);
    }

    private void showFragment(String className)
    {
        try
        {
            Class<?> fragmentClass = Class.forName(className);
            Constructor<?> fragConstructor = fragmentClass.getConstructor();
            Object fragment = fragConstructor.newInstance();

            if(fragment instanceof Fragment)
            {
                showFragment((Fragment) fragment);
            }
        }
        catch(Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    // TODO better fragment loading/switching logic, use tags
    private void showFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationMenu(NavigationView view)
    {
        for(NavigationItem item : ResearchStack.getInstance().getNavigationItems())
        {
            MenuItem menuItem = view.getMenu()
                    .add(item.getGroupId(), item.getId(), item.getOrder(), item.getTitle());
            menuItem.setIcon(item.getIcon());
            menuItem.setIntent(new Intent(this, item.getClazz()));
            menuItem.setCheckable(true);
        }

        view.getMenu().setGroupCheckable(R.id.nav_group, true, true);
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();

        if (getSupportFragmentManager().findFragmentById(R.id.placeholder) == null)
        {
            MenuItem item = navigationView.getMenu().getItem(0);
            item.setChecked(true);

            String className = item.getIntent().getComponent().getClassName();
            showFragment(className);

            getSupportActionBar().setTitle(item.getTitle());
        }
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();

        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

}

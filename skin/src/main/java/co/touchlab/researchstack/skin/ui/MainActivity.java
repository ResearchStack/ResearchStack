package co.touchlab.researchstack.skin.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.ui.PinCodeActivity;
import co.touchlab.researchstack.backbone.ui.ViewTaskActivity;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.backbone.utils.UiThreadContext;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ActionItem;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.task.InitialTask;
import co.touchlab.researchstack.skin.ui.adapter.MainPagerAdapter;
import rx.Observable;

/**
 * Created by bradleymcdermott on 10/27/15.
 */
public class MainActivity extends PinCodeActivity
{
    private static final int REQUEST_CODE_INITIAL_TASK = 1010;

    //    private DrawerLayout   drawerLayout;
    //    private NavigationView navigationView;
    //    private ViewPager pager;
    //    private ;
    private MainPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView) findViewById(R.id.navigation);
//        navigationView.setNavigationItemSelectedListener(item -> {
//            if(item.getIntent() != null)
//            {
//                String className = item.getIntent().getComponent().getClassName();
//                showFragment(className);
//
//                //TODO remove delay call once I/O is offloaded onto separate thread w/in fragments
//                navigationView.postDelayed(drawerLayout:: closeDrawers, 100);
//            }
//
//            actionBar.setTitle(item.getTitle());
//
//            return true;
//        });
//        initNavigationMenu(navigationView);
//
//        View headerView = getLayoutInflater().inflate(R.layout.include_user_header, null);
//
//        // TODO set header from user data
//        AppCompatTextView name = (AppCompatTextView) headerView.findViewById(R.id.name);
//        AppCompatTextView email = (AppCompatTextView) headerView.findViewById(R.id.email);
//
//        ImageView image = (ImageView) headerView.findViewById(R.id.profile_image);
//        image.setOnLongClickListener(v -> {
//            new AlertDialog.Builder(MainActivity.this).setMessage("Clear saved user data?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        DataProvider.getInstance().clearUserData(MainActivity.this);
//                        dialog.dismiss();
//                        finish();
//                        System.exit(0);
//                    })
//                    .setNegativeButton("No", ((dialog1, which1) -> {
//                        dialog1.dismiss();
//                    }))
//                    .show();
//            return false;
//        });
//
//        navigationView.addHeaderView(headerView);

        // Check if we need to run initial Task
        Observable.create(subscriber -> {
            UiThreadContext.assertBackgroundThread();

            TaskResult result = StorageAccess.getInstance().getAppDatabase()
                    .loadLatestTaskResult(InitialTask.TASK_ID);
            subscriber.onNext(result == null);
        }).compose(ObservableUtils.applyDefault()).subscribe(needsInitialSurvey -> {
            if((boolean) needsInitialSurvey)
            {
                InitialTask task = new InitialTask(InitialTask.TASK_ID);
                Intent intent = ViewTaskActivity.newIntent(this, task);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_INITIAL_TASK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_INITIAL_TASK && resultCode == RESULT_OK)
        {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            taskResult.setEndDate(new Date());
            StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);
            //TODO DataProvider.getInstance().uploadTaskResult(this, taskResult);
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(ActionItem item : UiManager.getInstance().getMainActionBarItems())
        {
            MenuItem menuItem = menu.add(item.getGroupId(),
                    item.getId(),
                    item.getOrder(),
                    item.getTitle());
            menuItem.setIcon(item.getIcon());
            menuItem.setShowAsAction(item.getAction());
            menuItem.setIntent(new Intent(this, item.getClazz()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getIntent() != null)
        {
            ComponentName componentName = item.getIntent().getComponent();
            if (componentName != null)
            {
                try
                {
                    Class clazz = Class.forName(componentName.getClassName());

                    Intent intent = ViewFragmentActivity.newIntent(this,
                            item.getTitle().toString(),
                            componentName.getClassName());
                    startActivity(intent);
                    return true;

                }
                catch(ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        if (pagerAdapter == null)
        {
            List<ActionItem> items = UiManager.getInstance().getMainTabBarItems();
            pagerAdapter = new MainPagerAdapter(
                    getSupportFragmentManager(), items);
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    int index = tabLayout.getSelectedTabPosition();
                    viewPager.setCurrentItem(index);
                }

                @Override public void onTabUnselected(TabLayout.Tab tab) { }

                @Override public void onTabReselected(TabLayout.Tab tab) { }
            });

            for(ActionItem item : items)
            {
                tabLayout.addTab(tabLayout.newTab().setText(item.getTitle()));
            }
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
    }

    @Override
    public void onDataFailed()
    {
        super.onDataFailed();

        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

}

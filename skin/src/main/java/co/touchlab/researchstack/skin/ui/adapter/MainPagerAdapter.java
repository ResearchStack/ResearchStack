package co.touchlab.researchstack.skin.ui.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import co.touchlab.researchstack.backbone.utils.ViewUtils;
import co.touchlab.researchstack.skin.ActionItem;


public class MainPagerAdapter extends FragmentPagerAdapter
{
    private List<ActionItem> items;

    public MainPagerAdapter(FragmentManager fm, List<ActionItem> items)
    {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position)
    {
        ActionItem item = items.get(position);
        return ViewUtils.createFragment(item.getClazz());
    }


    @Override
    public int getCount()
    {
        return items.size();
    }

}

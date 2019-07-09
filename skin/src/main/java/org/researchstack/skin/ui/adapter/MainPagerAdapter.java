package org.researchstack.skin.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.researchstack.foundation.components.utils.ViewUtils;
import org.researchstack.skin.ActionItem;

import java.util.List;


public class MainPagerAdapter extends FragmentPagerAdapter {
    private List<ActionItem> items;

    public MainPagerAdapter(FragmentManager fm, List<ActionItem> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        ActionItem item = items.get(position);
        return ViewUtils.createFragment(item.getClazz());
    }


    @Override
    public int getCount() {
        return items.size();
    }

}

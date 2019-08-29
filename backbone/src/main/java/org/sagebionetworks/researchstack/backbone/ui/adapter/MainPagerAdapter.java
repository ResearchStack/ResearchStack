package org.sagebionetworks.researchstack.backbone.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.sagebionetworks.researchstack.backbone.utils.ViewUtils;
import org.sagebionetworks.researchstack.backbone.ActionItem;

import java.util.List;


public class MainPagerAdapter extends FragmentPagerAdapter {
    private List<ActionItem> items;

    /**
     * Keep a list of fragments so you can get access to one
     */
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}

package co.touchlab.touchkit.rk.ui.adapter;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.ui.views.ConsentSectionLayout;

@Deprecated
public class ConsentPagerAdapter extends PagerAdapter
{
    private final List<ConsentSection> items;
    private final LayoutInflater     inflater;

    public ConsentPagerAdapter(Context context, List<ConsentSection> items)
    {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        ConsentSection item = items.get(position);

        ConsentSectionLayout child = (ConsentSectionLayout) inflater.inflate(
                R.layout.item_consent_section, container, false);
        child.setData(item);

        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

}

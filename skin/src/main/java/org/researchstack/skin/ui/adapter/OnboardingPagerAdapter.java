package org.researchstack.skin.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.backbone.ui.ViewVideoActivity;
import org.researchstack.backbone.ui.views.LocalWebView;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.StudyOverviewModel;

import java.util.List;


public class OnboardingPagerAdapter extends PagerAdapter {
    private final List<StudyOverviewModel.Question> items;
    private final LayoutInflater inflater;

    public OnboardingPagerAdapter(Context context, List<StudyOverviewModel.Question> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Each case is the same block of code. Figure out what the layout-id needs to be, then upcast
     * to the super class that the layouts each share. If the layouts cant extend from the same parent
     * class, maybe have each implement an interface?
     * <p>
     * interface {
     * public getView()
     * public setFormSteps(Data d)
     * }
     * <p>
     * To discuss.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        StudyOverviewModel.Question item = items.get(position);

        if (!TextUtils.isEmpty(item.getVideoName())) {
            View layout = inflater.inflate(R.layout.rss_layout_study_html, container, false);
            container.addView(layout);

            StringBuilder builder = new StringBuilder("<h3>" + item.getTitle() + "</h3>");
            builder.append("<p>" + item.getDetails() + "</p>");

            TextView simpleView = (TextView) layout.findViewById(R.id.text);
            simpleView.setText(Html.fromHtml(builder.toString()));
            simpleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rss_ic_video);
            simpleView.setOnClickListener(v -> {
                String videoPath = ResourceManager.getInstance()
                        .generatePath(ResourceManager.Resource.TYPE_MP4, item.getVideoName());
                Intent intent = ViewVideoActivity.newIntent(container.getContext(), videoPath);
                container.getContext().startActivity(intent);
            });

            return layout;
        } else {
            LocalWebView layout = new LocalWebView(container.getContext());
            String path = ResourceManager.getInstance().generateAbsolutePath(
                    ResourceManager.Resource.TYPE_HTML, item.getDetails());
            layout.loadUrl(path);
            container.addView(layout);
            return layout;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}

package org.researchstack.skin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.skin.R;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.skin.model.SectionModel;
import org.researchstack.skin.ui.ShareActivity;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rss_fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SectionModel model = loadSections();
        if(model == null) return;

        ImageView logoView = (ImageView) view.findViewById(R.id.learn_logo_view);
        if(!TextUtils.isEmpty(model.getLogoName()))
        {
            int resId = ResUtils.getDrawableResourceId(view.getContext(), model.getLogoName());
            logoView.setImageResource(resId);
        }
        else
        {
            logoView.setVisibility(View.GONE);
        }

        TextView titleView = (TextView) view.findViewById(R.id.learn_title_view);
        if(!TextUtils.isEmpty(model.getTitle()))
        {
            titleView.setText(model.getTitle());
        }
        else
        {
            titleView.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(org.researchstack.skin.R.id.recycler_view);
        recyclerView.setAdapter(new LearnAdapter(getContext(), loadSections()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

    }

    private SectionModel loadSections() {
        SectionModel model = null;
        ResourcePathManager.Resource resource = ResourceManager.getInstance().getLearnSections();
        try
        {
            model = resource.create(getActivity());
        }
        catch (RuntimeException re)
        {
            LogExt.e(getClass(), "Error loading SectionModel for Learn: " + re.getMessage());
        }

        return model;
    }

    public static class LearnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM = 1;

        private Context        context;
        private List<Object> items;
        private LayoutInflater inflater;

        public LearnAdapter(Context ctx, SectionModel sections) {
            super();
            context = ctx;
            items = new ArrayList<>();
            for (SectionModel.Section section : sections.getSections()) {
                if(!TextUtils.isEmpty(section.getTitle()))
                {
                    items.add(section.getTitle());
                }
                items.addAll(section.getItems());

            }
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                View view = inflater.inflate(R.layout.preference_category_material, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = inflater.inflate(R.layout.rss_item_row_learn, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position) {
            if (hldr instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder) hldr;

                //Offset for header
                SectionModel.SectionRow item = (SectionModel.SectionRow) items.get(position);

                holder.title.setText(item.getTitle());

                if(!TextUtils.isEmpty(item.getIconImage()))
                {
                    holder.icon.setVisibility(View.VISIBLE);
                    int resId = ResUtils.getDrawableResourceId(context, item.getIconImage());
                    holder.icon.setImageResource(resId);
                    int colorId = ResUtils.getColorResourceId(context, item.getTintColor());
                    holder.icon.setColorFilter(ContextCompat.getColor(context, colorId));
                }
                else
                {
                    holder.icon.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(v -> {
                    if(SectionModel.SHARE_TYPE_DETAILS.equals(item.getDetails()))
                    {
                        Intent intent = new Intent(v.getContext(), ShareActivity.class);
                        v.getContext().startActivity(intent);
                    }
                    else {
                        String path = ResourceManager.getInstance().
                                generateAbsolutePath(ResourceManager.Resource.TYPE_HTML, item.getDetails());
                        Intent intent = ViewWebDocumentActivity.newIntentForPath(v.getContext(),
                                item.getTitle(),
                                path);
                        v.getContext().startActivity(intent);

                    }
                });
            } else {
                HeaderViewHolder holder = (HeaderViewHolder) hldr;
                String title = (String) items.get(position);
                holder.title.setText(title);
            }
        }

        @Override
        public int getItemViewType(int position) {
            Object item = items.get(position);
            return item instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            // Size of items + header
            return items.size();
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {

            TextView title;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView;
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.learn_item_title);
                icon = (ImageView) itemView.findViewById(R.id.learn_item_icon);
            }
        }
    }
}

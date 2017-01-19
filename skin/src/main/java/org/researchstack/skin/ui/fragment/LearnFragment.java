package org.researchstack.skin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.SectionModel;

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(org.researchstack.skin.R.id.recycler_view);
        recyclerView.setAdapter(new LearnAdapter(getContext(), loadSections()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }

    private SectionModel loadSections() {
        return ResourceManager.getInstance().getLearnSections().create(getActivity());
    }

    public static class LearnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM = 1;

        private List<Object> items;
        private LayoutInflater inflater;

        public LearnAdapter(Context context, SectionModel sections) {
            super();
            items = new ArrayList<>();
            for (SectionModel.Section section : sections.getSections()) {
                items.add(section.getTitle());
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

                holder.itemView.setOnClickListener(v -> {
                    String path = ResourceManager.getInstance().
                            generateAbsolutePath(ResourceManager.Resource.TYPE_HTML, item.getDetails());
                    Intent intent = ViewWebDocumentActivity.newIntentForPath(v.getContext(),
                            item.getTitle(),
                            path);
                    v.getContext().startActivity(intent);
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
            AppCompatTextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (AppCompatTextView) itemView.findViewById(R.id.learn_item_title);
            }
        }
    }
}

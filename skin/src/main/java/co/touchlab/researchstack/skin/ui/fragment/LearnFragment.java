package co.touchlab.researchstack.skin.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.backbone.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.model.SectionModel;
import co.touchlab.researchstack.skin.utils.JsonUtils;

public class LearnFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(co.touchlab.researchstack.glue.R.id.recycler_view);
        recyclerView.setAdapter(new LearnAdapter(getContext(), loadSections()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        Configuration config = getResources().getConfiguration();

        //TODO Implement compat method to get RTL on API < 17
        boolean isRTL = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private SectionModel loadSections()
    {
        int fileResId = ResourceManager.getInstance().getLearnSections();
        return JsonUtils.loadClass(getContext(), SectionModel.class, fileResId);
    }

    public static class LearnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM   = 1;

        private List<Object>   items;
        private LayoutInflater inflater;

        public LearnAdapter(Context context, SectionModel sections)
        {
            super();
            items = new ArrayList<>();
            for(SectionModel.Section section : sections.getSections())
            {
                items.add(section.getTitle());
                items.addAll(section.getItems());

            }
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if(viewType == VIEW_TYPE_HEADER)
            {
                View view = inflater.inflate(R.layout.header_learn, parent, false);
                return new HeaderViewHolder(view);
            }
            else
            {
                View view = inflater.inflate(R.layout.item_row_section, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
        {
            if(hldr instanceof ViewHolder)
            {
                ViewHolder holder = (ViewHolder) hldr;
                Context context = holder.itemView.getContext();

                //Offset for header
                SectionModel.SectionRow item = (SectionModel.SectionRow) items.get(position);

                holder.title.setText(item.getTitle());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = ViewWebDocumentActivity.newIntent(v.getContext(),
                            item.getTitle(),
                            item.getDetails());
                    v.getContext().startActivity(intent);
                });
            }
            else
            {
                HeaderViewHolder holder = (HeaderViewHolder) hldr;
                String title = (String) items.get(position);
                holder.title.setText(title);
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            Object item = items.get(position);
            return item instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount()
        {
            // Size of items + header
            return items.size();
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder
        {

            TextView title;

            public HeaderViewHolder(View itemView)
            {
                super(itemView);
                title = ((TextView) itemView);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            AppCompatTextView title;

            public ViewHolder(View itemView)
            {
                super(itemView);
                title = (AppCompatTextView) itemView.findViewById(R.id.learn_item_title);
            }
        }
    }
}

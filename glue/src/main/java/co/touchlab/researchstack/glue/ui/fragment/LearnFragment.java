package co.touchlab.researchstack.glue.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import co.touchlab.researchstack.core.model.SectionModel;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.ui.views.DividerItemDecoration;
import co.touchlab.researchstack.glue.utils.JsonUtils;
import co.touchlab.researchstack.glue.utils.ViewUtils;

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new LearnAdapter(getContext(), loadTasksAndSchedules()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        Configuration config = getResources().getConfiguration();

        //TODO Implement compat method to get RTL on API < 17
        boolean isRTL = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                                                                     DividerItemDecoration.VERTICAL_LIST,
                                                                     0, isRTL);
        recyclerView.addItemDecoration(decoration);
    }

    private List<SectionModel.SectionRow> loadTasksAndSchedules()
    {
        int fileResId = ResearchStack.getInstance()
                                     .getLearnSections();
        SectionModel schedulesAndTasksModel = JsonUtils.loadClass(getContext(), SectionModel.class,
                                                                  fileResId);
        SectionModel.Section section = schedulesAndTasksModel.getSections()
                                                             .get(0);
        return section.getItems();
    }

    public static class LearnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM   = 1;

        private List<SectionModel.SectionRow> items;
        private LayoutInflater                inflater;

        public LearnAdapter(Context context, List<SectionModel.SectionRow> items)
        {
            super();
            this.items = items;
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
                SectionModel.SectionRow item = items.get(position - 1);

                holder.title.setText(item.getTitle());

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = ViewWebDocumentActivity.newIntent(v.getContext(),
                                                                      item.getTitle(),
                                                                      item.getDetails());
                    v.getContext()
                     .startActivity(intent);
                });

                int imageResId = ResearchStack.getInstance()
                                              .getDrawableResourceId(context, item.getIconImage());
                Drawable icon = context.getResources()
                                       .getDrawable(imageResId);
                int tintColor = ViewUtils.fetchAccentColor(context);
                DrawableCompat.setTint(icon, tintColor);
                holder.icon.setImageDrawable(icon);
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount()
        {
            // Size of items + header
            return items.size() + 1;
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder
        {

            ImageView logo;

            public HeaderViewHolder(View itemView)
            {
                super(itemView);
                logo = (ImageView) itemView.findViewById(R.id.logo);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            AppCompatTextView  title;
            AppCompatImageView icon;

            public ViewHolder(View itemView)
            {
                super(itemView);
                title = (AppCompatTextView) itemView.findViewById(R.id.title);
                icon = (AppCompatImageView) itemView.findViewById(R.id.icon);
            }
        }
    }
}

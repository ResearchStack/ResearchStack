package co.touchlab.touchkit.rk.ui.fragment;

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

import java.util.List;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.SectionModel;
import co.touchlab.touchkit.rk.ui.ViewWebDocumentActivity;
import co.touchlab.touchkit.rk.ui.views.DividerItemDecoration;
import co.touchlab.touchkit.rk.utils.JsonUtils;
import co.touchlab.touchkit.rk.utils.ViewUtils;

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
        DividerItemDecoration decoration = new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL_LIST, 0, isRTL);
        recyclerView.addItemDecoration(decoration);
    }

    private List<SectionModel.SectionRow> loadTasksAndSchedules()
    {
        SectionModel schedulesAndTasksModel = JsonUtils
                .loadClassFromRawJson(getContext(), SectionModel.class, R.raw.learn);

        SectionModel.Section section = schedulesAndTasksModel.getSections().get(0);

        return section.getItems();
    }

    public static class LearnAdapter extends RecyclerView.Adapter<LearnAdapter.ViewHolder>
    {
        private List<SectionModel.SectionRow> items;
        private LayoutInflater inflater;

        public LearnAdapter(Context context, List<SectionModel.SectionRow> items)
        {
            super();
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public LearnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = inflater.inflate(R.layout.item_row_section, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LearnAdapter.ViewHolder holder, int position)
        {
            Context context = holder.itemView.getContext();

            SectionModel.SectionRow item = items.get(position);

            holder.title.setText(item.getTitle());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = ViewWebDocumentActivity
                        .newIntent(v.getContext(), item.getTitle(), item.getDetails());
                v.getContext().startActivity(intent);
            });

            int imageResId = AppDelegate.getInstance().getDrawableResourceId(context, item.getIconImage());
            Drawable icon = context.getResources().getDrawable(imageResId, null);
            int tintColor = ViewUtils.fetchAccentColor(context);
            DrawableCompat.setTint(icon, tintColor);
            holder.icon.setImageDrawable(icon);
        }

        @Override
        public int getItemCount()
        {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            AppCompatTextView title;
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

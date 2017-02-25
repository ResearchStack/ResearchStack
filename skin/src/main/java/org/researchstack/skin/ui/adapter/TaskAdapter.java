package org.researchstack.skin.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.R;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by rianhouston on 2/25/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = TaskAdapter.class.getCanonicalName();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    List<Object> tasks;
    private LayoutInflater inflater;

    PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> publishSubject = PublishSubject.create();

    public TaskAdapter(Context context) {
        super();

        tasks = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> getPublishSubject() {
        return publishSubject;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.rss_item_schedule_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.rss_item_schedule, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position) {
         Object obj = tasks.get(position);
         if(hldr instanceof ViewHolder) {
             ViewHolder holder = (ViewHolder) hldr;
             SchedulesAndTasksModel.TaskScheduleModel task = (SchedulesAndTasksModel.TaskScheduleModel)obj;

             Resources res = holder.itemView.getResources();
             int tintColor = getColorForTask(res, task.taskID);
             holder.colorBar.setBackgroundColor(tintColor);

             holder.title.setText(task.taskTitle);
             holder.subtitle.setText(task.taskCompletionTime);

             holder.itemView.setOnClickListener(v -> {
                 LogExt.d(LOG_TAG, "Item clicked: " + task.taskID + ", " + task.taskType);
                 publishSubject.onNext(task);
             });
         } else {
             HeaderViewHolder holder = (HeaderViewHolder) hldr;
             Header header = (Header)obj;
             holder.title.setText(header.title);
             holder.message.setText(header.message);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = tasks.get(position);
        return item instanceof Header ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    // Clean all elements of the recycler
    public void clear() {
        tasks.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Object> list) {
        tasks.addAll(list);
        notifyDataSetChanged();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView message;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.activity_header_title);
            message = (TextView) itemView.findViewById(R.id.activity_header_message);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorBar;
        ImageView dailyIndicator;
        TextView title;
        TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            colorBar = itemView.findViewById(R.id.color_bar);
            dailyIndicator = (ImageView) itemView.findViewById(R.id.daily_indicator);
            title = (TextView) itemView.findViewById(R.id.task_title);
            subtitle = (TextView) itemView.findViewById(R.id.task_subtitle);
        }
    }

    public static class Header {
        String title;
        String message;

        public Header(String t, String m) {
            title = t;
            message = m;
        }
    }

    // TODO: this should live somewhere else like ResourceManager?  Cause it will be app specific
    //       and the constants defined somewhere?
    private int getColorForTask(Resources resources, String taskId) {
        int colorId = 0;
        if(taskId != null) {
            if (taskId.contains("APHTimedWalking")) {
                colorId = resources.getColor(R.color.rss_activity_yellow);
            } else if (taskId.contains("APHPhonation")) {
                colorId = resources.getColor(R.color.rss_activity_blue);
            } else if (taskId.contains("APHIntervalTapping")) {
                colorId = resources.getColor(R.color.rss_activity_purple);
            } else if (taskId.contains("APHMedicationTracker")) {
              colorId = resources.getColor(R.color.rss_activity_red);
            } else if (taskId.contains("APHTremor")) {
              colorId = resources.getColor(R.color.rsb_colorPrimary);
            } else {
              colorId = resources.getColor(R.color.rss_activity_default);
            }
        } else {
            colorId = resources.getColor(R.color.rss_activity_default);
        }

        return colorId;
    }
}

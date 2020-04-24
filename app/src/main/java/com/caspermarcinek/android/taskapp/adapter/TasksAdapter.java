package com.caspermarcinek.android.taskapp.adapter;

/**
 * Created by witch on 09/28/2017.
 */
import java.util.List;

import com.caspermarcinek.android.taskapp.R;
import com.caspermarcinek.android.taskapp.model.Task;
import android.app.Activity;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TasksAdapter extends ArrayAdapter<Task>{
    private Activity context;
    private List<Task> tasks;
    public TasksAdapter(Activity context, List<Task> tasks) {
        super(context, R.layout.todo_list_item, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    private static class ViewHolder {
        TextView text_view_tasks_description;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.todo_list_item, null, true);
            viewHolder = new ViewHolder();
            viewHolder.text_view_tasks_description = rowView.findViewById(R.id.text_view_taskDescription);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        Task task = tasks.get(position);
        viewHolder.text_view_tasks_description.setText(task.getDescription());
        if(task.isCompleted()) {
            viewHolder.text_view_tasks_description
                    .setPaintFlags(viewHolder.text_view_tasks_description.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.text_view_tasks_description
                    .setPaintFlags(viewHolder.text_view_tasks_description.getPaintFlags() &
                            ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return rowView;
    }
}


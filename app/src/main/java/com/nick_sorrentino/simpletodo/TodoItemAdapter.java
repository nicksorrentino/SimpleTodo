package com.nick_sorrentino.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nick_sorrentino on 7/19/16.
 */
public class TodoItemAdapter extends ArrayAdapter<TodoItem> {
    public TodoItemAdapter(Context context, ArrayList<TodoItem> todoItems) {
        super(context, 0, todoItems);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem todoItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todoitem_listview, parent, false);
        }

        // Lookup view for data population
        TextView tvTodoTitle = (TextView) convertView.findViewById(R.id.tvTodoTitle);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvAdapterDueDate);
        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);

        // Populate the data into the template view using the data object
        tvTodoTitle.setText(todoItem.getTitle());

        tvPriority.setText(todoItem.getPriority());

        switch ( todoItem.getPriority() ) {
            case "Low":
                tvPriority.setTextColor(Color.parseColor("#336600"));
                break;
            case "Medium":
                tvPriority.setTextColor(Color.parseColor("#ff6600"));
                break;
            case "High":
                tvPriority.setTextColor(Color.parseColor("#990000"));
                break;
            default:
                tvPriority.setTextColor(Color.DKGRAY);
        }

        if ( todoItem.getDueDate() != null ) {
            Date dueDate = todoItem.getDueDate();
            long now = System.currentTimeMillis();
            String timeString = (String)DateUtils.getRelativeTimeSpanString(dueDate.getTime(), now, DateUtils.DAY_IN_MILLIS);
            tvDueDate.setText("Due " + timeString);
            tvDueDate.setVisibility(View.VISIBLE);
        }else {
            tvDueDate.setText("");
            tvDueDate.setVisibility(View.GONE);
        }


        // Return the completed view to render on screen
        return convertView;
    }
}


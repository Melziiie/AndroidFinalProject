package com.example.doit2.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.doit2.AddNewTask;
import com.example.doit2.MainActivity;
import com.example.doit2.Model.ToDoModel;
import com.example.doit2.R;
import com.example.doit2.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @Override
    public ToDoAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ToDoAdapter.ViewHolder holder, int position) {
        db.openDatabase();

        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });

        holder.dateTime.setText(R.string.no_date);

        if(item.getDate()==null){
            holder.dateTime.setText(item.getDate());
        }else{
            holder.dateHolder.setVisibility(View.INVISIBLE);
        }
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("date", item.getDate());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView dateTime;
        LinearLayout dateHolder;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            dateTime = view.findViewById(R.id.dateTimeText);
            dateHolder = view.findViewById(R.id.taskDate);
        }
    }
}

package com.example.todolistapp.Adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.Model.Task;
import com.example.todolistapp.R;
import com.example.todolistapp.database.TaskDatabaseHelper;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private ArrayList<Task> tasks;
    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        dbHelper = new TaskDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskName.setText(task.getName());
        holder.taskTime.setText(task.getTime());
        holder.taskDescription.setText(task.getDescription());
        holder.radioPending.setChecked(!task.isComplete());
        holder.radioCompleted.setChecked(task.isComplete());

        // Handle Edit Button Click
        holder.editButton.setOnClickListener(v -> showEditDialog(task));

        // Handle Delete Button Click
        holder.deleteButton.setOnClickListener(v -> {
            db.delete(TaskDatabaseHelper.TABLE_TASKS, TaskDatabaseHelper.COLUMN_ID + "=?",
                    new String[] { String.valueOf(task.getId()) });
            tasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, tasks.size());
        });

        // Handle Status Change
        holder.taskStatusGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isComplete = checkedId == R.id.radioCompleted;
            task.setComplete(isComplete);
            ContentValues values = new ContentValues();
            values.put(TaskDatabaseHelper.COLUMN_STATUS, isComplete ? 1 : 0);
            db.update(TaskDatabaseHelper.TABLE_TASKS, values, TaskDatabaseHelper.COLUMN_ID + "=?",
                    new String[] { String.valueOf(task.getId()) });
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // ViewHolder class to hold each item view
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskTime, taskDescription;
        RadioGroup taskStatusGroup;
        RadioButton radioPending, radioCompleted;
        Button editButton, deleteButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskStatusGroup = itemView.findViewById(R.id.taskStatusGroup);
            radioPending = itemView.findViewById(R.id.radioPending);
            radioCompleted = itemView.findViewById(R.id.radioCompleted);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Method to show edit dialog
    private void showEditDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        // Inflate the dialog layout
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null);
        EditText editName = view.findViewById(R.id.editTaskName);
        EditText editTime = view.findViewById(R.id.editTaskTime);
        EditText editDescription = view.findViewById(R.id.editTaskDescription);

        // Set initial values for editing
        editName.setText(task.getName());
        editTime.setText(task.getTime());
        editDescription.setText(task.getDescription());

        builder.setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update task details
                    task.setName(editName.getText().toString());
                    task.setTime(editTime.getText().toString());
                    task.setDescription(editDescription.getText().toString());

                    ContentValues values = new ContentValues();
                    values.put(TaskDatabaseHelper.COLUMN_NAME, task.getName());
                    values.put(TaskDatabaseHelper.COLUMN_TIME, task.getTime());
                    values.put(TaskDatabaseHelper.COLUMN_DESCRIPTION, task.getDescription());
                    values.put(TaskDatabaseHelper.COLUMN_STATUS, task.isComplete() ? 1 : 0);

                    db.update(TaskDatabaseHelper.TABLE_TASKS, values, TaskDatabaseHelper.COLUMN_ID + "=?",
                            new String[] { String.valueOf(task.getId()) });

                    // Notify the adapter that data has changed
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
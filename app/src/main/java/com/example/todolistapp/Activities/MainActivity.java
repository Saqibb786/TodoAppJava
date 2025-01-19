package com.example.todolistapp.Activities;

import com.example.todolistapp.R;
import com.example.todolistapp.database.TaskDatabaseHelper;
import com.example.todolistapp.Model.Task;
import com.example.todolistapp.Adapters.TaskAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText taskNameInput, taskTimeInput, taskDescriptionInput;
    private Switch markAsCompleteSwitch;
    private RecyclerView taskListView;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        taskNameInput = findViewById(R.id.taskNameInput);
        taskTimeInput = findViewById(R.id.taskTimeInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        markAsCompleteSwitch = findViewById(R.id.markAsCompleteSwitch);
        Button addButton = findViewById(R.id.addButton);
        Button showListButton = findViewById(R.id.showListButton);
        taskListView = findViewById(R.id.taskListView);

        // Set up RecyclerView
        taskListView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

        // Initialize database helper
        dbHelper = new TaskDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Load tasks from database
        loadTasksFromDatabase();

        // Add button click listener
        addButton.setOnClickListener(v -> {
            String name = taskNameInput.getText().toString();
            String time = taskTimeInput.getText().toString();
            String description = taskDescriptionInput.getText().toString();
            boolean isComplete = markAsCompleteSwitch.isChecked();

            // Validate input
            if (!name.isEmpty() && !time.isEmpty() && !description.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(TaskDatabaseHelper.COLUMN_NAME, name);
                values.put(TaskDatabaseHelper.COLUMN_TIME, time);
                values.put(TaskDatabaseHelper.COLUMN_DESCRIPTION, description);
                values.put(TaskDatabaseHelper.COLUMN_STATUS, isComplete ? 1 : 0);

                long newRowId = db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values);

                Task task = new Task((int) newRowId, name, time, description, isComplete);
                taskList.add(task);
                taskAdapter.notifyItemInserted(taskList.size() - 1);

                // Clear input fields
                taskNameInput.setText("");
                taskTimeInput.setText("");
                taskDescriptionInput.setText("");
                markAsCompleteSwitch.setChecked(false);
            } else {
                Toast.makeText(MainActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show List button click listener (optional, can be used for additional
        // functionality)
        showListButton.setOnClickListener(v -> taskAdapter.notifyDataSetChanged());
    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_TASKS, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TIME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DESCRIPTION));
            boolean isComplete = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_STATUS)) == 1;

            Task task = new Task(id, name, time, description, isComplete);
            taskList.add(task);
        }
        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }
}
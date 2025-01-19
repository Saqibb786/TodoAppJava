package com.example.todolistapp.Activities;

import com.example.todolistapp.R;
import com.example.todolistapp.database.TaskDatabaseHelper;
import com.example.todolistapp.Model.Task;
import com.example.todolistapp.Adapters.TaskAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText taskNameInput, taskTimeInput, taskDescriptionInput;
    private RecyclerView taskListView;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private boolean showAllTasks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        taskNameInput = findViewById(R.id.taskNameInput);
        taskTimeInput = findViewById(R.id.taskTimeInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        Button addButton = findViewById(R.id.addButton);
        ToggleButton showListButton = findViewById(R.id.showListButton);
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

            // Validate input
            if (!name.isEmpty() && !time.isEmpty() && !description.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(TaskDatabaseHelper.COLUMN_NAME, name);
                values.put(TaskDatabaseHelper.COLUMN_TIME, time);
                values.put(TaskDatabaseHelper.COLUMN_DESCRIPTION, description);
                values.put(TaskDatabaseHelper.COLUMN_STATUS, 0); // Default to pending

                long newRowId = db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values);

                Task task = new Task((int) newRowId, name, time, description, false);
                taskList.add(task);
                taskAdapter.notifyItemInserted(taskList.size() - 1);

                // Clear input fields
                taskNameInput.setText("");
                taskTimeInput.setText("");
                taskDescriptionInput.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show List button click listener
        showListButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showAllTasks = isChecked;
            loadTasksFromDatabase();
        });
    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        String selection = showAllTasks ? null : TaskDatabaseHelper.COLUMN_STATUS + "=?";
        String[] selectionArgs = showAllTasks ? null : new String[] { "0" };
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_TASKS, null, selection, selectionArgs, null, null, null);
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
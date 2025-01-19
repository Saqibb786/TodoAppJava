package com.example.todolistapp.Model;

public class Task {
    private int id;
    private String name, time, description;
    private boolean isComplete;

    public Task(int id, String name, String time, String description, boolean isComplete) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.description = description;
        this.isComplete = isComplete;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
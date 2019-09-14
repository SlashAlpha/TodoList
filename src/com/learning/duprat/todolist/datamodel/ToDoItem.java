package com.learning.duprat.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ToDoItem {
    private String shortDescription;
    private String details;
    private LocalDate deadLine;
    private int remaining;
    private boolean completed;

    private int progress;
    private ObservableList<ToDoTask> toDoTasks;

    private String store;


    public ToDoItem(String shortDescription, String details, LocalDate deadLine, int remaining, boolean completed, int progress, String store) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.deadLine = deadLine;
        this.remaining = remaining;
        this.completed = completed;

        this.progress = progress;


        this.store = store;
        this.toDoTasks = stringToTasks(store);

    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ObservableList<ToDoTask> getToDoTasks() {
        return toDoTasks;
    }

    public void setToDoTasks(ObservableList<ToDoTask> toDoTasks) {
        this.toDoTasks = toDoTasks;
    }


    public void addToDoTask(String sticker, String stickerInfo) {
        ToDoTask task = new ToDoTask(sticker, stickerInfo, 0, LocalDate.now(), 0, "false", 0, 0, 0);
        toDoTasks.add(task);
    }

    public String taskToString(ToDoTask s) {
        String x = "";
        x += s.getSticker() + "cucaraccahippo" + s.getInfo() + "cucaraccahippo" + Double.toString(s.getEstimatedTime()) + "cucaraccahippo"
                + localD(s.getDate()) + "cucaraccahippo" + intToStr(s.getOrder()) + "cucaraccahippo" + s.getCompleted() + "cucaraccahippo" +
                intToStr(s.getUnexpected()) + "cucaraccahippo" + Double.toString(s.getSocialFactor()) + "cucaraccahippo" +
                Double.toString(s.getDifficulty()) + "cucaraccaraccoon";
        return x;
    }

    public String tasksToString(ObservableList<ToDoTask> s) {
        String x = "";
        for (ToDoTask t : s) {
            x += taskToString(t);
        }

        return x;
    }

    public ObservableList<ToDoTask> stringToTasks(String s) {
        toDoTasks = FXCollections.observableArrayList();
        if (!s.equals("")) {

            String[] x = s.split("cucaraccaraccoon");

            for (String a : x) {
                String[] z = a.split("cucaraccahippo");
                if (z.length > 1) {
                    toDoTasks.add(new ToDoTask(z[0], z[1], Double.valueOf(z[2]), strTolocal(z[3]), strToInt(z[4]), z[5], strToInt(z[6]), Double.valueOf(z[7]), Double.valueOf(z[8])));
                }
            }
        }
        return toDoTasks;
    }

    public void addTask(ToDoTask task) {

        toDoTasks.add(task);
        System.out.println("added to item");

    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    //    @Override
//    public String toString() {
//        return shortDescription;
//    }
    public void saveTasks(ObservableList<ToDoTask> tasks) {
        String save = tasksToString(tasks);
        setStore(save);

    }

    public void loadStore() {
        setToDoTasks(stringToTasks(this.store));

    }

    public int taskIndex(ToDoTask tasksearch) {
        int count = 0;
        for (ToDoTask task : toDoTasks) {
            if (tasksearch.getSticker().equals(task.getSticker())) {
                break;
            } else {
                count++;
            }
            ;
        }

        return count;
    }

    public void removeTask(ToDoTask task) {
        toDoTasks.remove(this.taskIndex(task));
        System.out.println("Task " + task.getSticker() + " removed");
    }

    public String localD(LocalDate s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return s.format(formatter);
    }

    public String intToStr(int s) {
        return Integer.toString(s);
    }

    public Integer strToInt(String s) {
        return Integer.parseInt(s);
    }

    public LocalDate strTolocal(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate x = LocalDate.parse(s, formatter);
        return x;
    }


}

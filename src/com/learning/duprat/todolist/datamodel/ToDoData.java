package com.learning.duprat.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.format.DateTimeFormatter;

public class ToDoData {
    private static ToDoData instance = new ToDoData();
    private static String filename = "default.xml";

    private ObservableList<ToDoItem> toDoItems;
    private DateTimeFormatter formatter;

    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static ToDoData getInstance() {
        return instance;
    }

    public static String getFilename() {
        return filename;
    }

    public static void setFilename(String filename) {
        ToDoData.filename = filename;
    }

//    public void setToDoItems(List<ToDoItem> toDoItems) {
//        this.toDoItems = toDoItems;
//    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void addToDoItem(ToDoItem item) {
        toDoItems.add(item);
    }

    public void deleteTodoItem(ToDoItem item) {
        toDoItems.remove(item);
    }

    public void initiateList() {
        toDoItems = FXCollections.observableArrayList();

    }


}

package com.learning.duprat.todolist;

import com.learning.duprat.todolist.datamodel.ToDoTask;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class TaskController {
    @FXML
    TextField sticker;
    @FXML
    TextArea detailsArea;
    @FXML
    Spinner difficulty;
    @FXML
    Spinner estimated;


    public ToDoTask processedResult(int order) {
        String s = sticker.getText();
        String r = detailsArea.getText();
        ToDoTask task = new ToDoTask(s, r, ((Double) estimated.getValue()), LocalDate.now(), order, "false", 0, 0, ((Double) difficulty.getValue()));
        return task;

    }

    public ToDoTask updateTasks(ToDoTask task) {
        task.setInfo(detailsArea.getText());
        task.setSticker(sticker.getText());
        task.setDifficulty(((Double) difficulty.getValue()));
        task.setEstimatedTime(((Double) estimated.getValue()));
        return task;
    }

    public void editTask(ToDoTask task) {
        sticker.setText(task.getSticker());
        detailsArea.setText(task.getInfo());


    }
}

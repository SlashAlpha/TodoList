package com.learning.duprat.todolist;

import com.learning.duprat.todolist.datamodel.ToDoData;
import com.learning.duprat.todolist.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;

public class DialogController {
    @FXML
    BorderPane borderPane;
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private Spinner<Integer> countingDays;


    public ToDoItem processResult() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        ToDoItem newItem = new ToDoItem(shortDescription, details, deadlineValue, countingDays.getValue(), false, 0, "");
        ToDoData.getInstance().addToDoItem(newItem);
        return newItem;

    }

    public void editTask(ToDoItem item) {
        shortDescriptionField.setText(item.getShortDescription());
        detailsArea.setText(item.getDetails());
        countingDays.setEditable(true);
        countingDays.getValueFactory().setValue(item.getRemaining());
        deadlinePicker.setValue(item.getDeadLine());

    }

    public ToDoItem updateTask() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();
        ToDoItem newItem = new ToDoItem(shortDescription, details, deadlineValue, countingDays.getValue(), false, 0, "");
        return newItem;
    }

    @FXML
    public void showContactDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();


        dialog.setTitle(" Edit ToDO");
        dialog.setHeaderText("Use this dialog to update a  ToDO Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();
            return;
        }
    }

}

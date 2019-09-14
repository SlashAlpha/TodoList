package com.learning.duprat.todolist;

import com.learning.duprat.todolist.datamodel.DataDoubleStore;
import com.learning.duprat.todolist.datamodel.ToDoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("todoList.fxml"));
        primaryStage.setTitle("ToDo List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        DataDoubleStore dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
        try {
            dataDoubleStore.saveContacts();


        } catch (NullPointerException e) {
            System.out.println((e.getMessage()));
        }
    }

    @Override
    public void init() throws Exception {

        try {

            DataDoubleStore dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
            dataDoubleStore.loadContacts();
        } catch (NullPointerException e) {
        }


    }

}



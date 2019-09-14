package com.learning.duprat.todolist;

import com.learning.duprat.todolist.datamodel.DataDoubleStore;
import com.learning.duprat.todolist.datamodel.ToDoData;
import com.learning.duprat.todolist.datamodel.ToDoItem;
import com.learning.duprat.todolist.datamodel.ToDoTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    public BorderPane mainBorderPane;
    @FXML
    Slider slider = new Slider();
    @FXML
    ProgressBar pb = new ProgressBar(5);
    @FXML
    ProgressIndicator pi = new ProgressIndicator(5);
    @FXML
    Label sorting;
    int sortChoice = 1;
    private List<ToDoItem> toDoItems;
    @FXML
    private ListView<ToDoItem> todoListView;
    private List<ToDoTask> toDoTasks;
    @FXML
    private ListView<ToDoTask> todoTaskview;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextArea description;
    @FXML
    private Label deadlineLabel;
    @FXML
    private Label reminder;
    @FXML
    private HBox hBox;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private Label startDay;
    @FXML
    private ToggleButton filterToggleButton;
    @FXML
    private ToggleButton filterToggleButton2;
    @FXML
    private TextArea fileInfo;
    @FXML
    private Label completed;
    private ObservableList<String> fileNames = FXCollections.observableArrayList();
    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem> wantPriorityItems;
    private Predicate<ToDoItem> wantAllItems;
    private Predicate<ToDoItem> wantTodaysItems;

    public void initialize() {


        addCBoxItems();
        choiceBox.setItems(fileNames);
        choiceBox.setValue("default.xml");
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
//        ToDoItem item=new ToDoItem("Death Cards","Invitation to see you in the ground",
//                LocalDate.of(2019, Month.JULY, 14));
//        ToDoItem item2=new ToDoItem("Doctor Appointment","Autopsy case 5062",
//                LocalDate.of(2019, Month.JULY, 19));
//        ToDoItem item3=new ToDoItem("Undertakers","Make up and clothing,fitting the box",
//                LocalDate.of(2019, Month.JULY, 20));
//        ToDoItem item4=new ToDoItem("Digging","Digging your hole at cemetary",
//                LocalDate.of(2019, Month.JULY, 21));
//        ToDoItem item5=new ToDoItem("Celebration","Burial,Photos and Canapé",
//                LocalDate.of(2019, Month.JULY, 22));
//        toDoItems=new ArrayList<>();
//        toDoItems.add(item);
//        toDoItems.add(item2);
//        toDoItems.add(item3);
//        toDoItems.add(item4);
//        toDoItems.add(item5);
//        ToDoData.getInstance().setToDoItems(toDoItems);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if (t1 != null) {
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();

                    description.setText(item.getDetails());
                    item.setToDoTasks(item.stringToTasks(item.getStore()));
                    setPb(item);
                    todoTaskview.setItems(item.getToDoTasks());
                    todoTaskview.getSelectionModel().selectFirst();
                    todoTaskview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTask>() {
                        @Override
                        public void changed(ObservableValue<? extends ToDoTask> observableValue, ToDoTask toDoTask, ToDoTask t1) {
                            ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
                            try {
                                fileInfo.setText(task.getInfo());

                            } catch (NullPointerException e) {
                            }

                        }
                    });

                    int i = LocalDate.now().getDayOfYear();
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("EEEE d MMMM YYYY");
                    deadlineLabel.setText(df.format(item.getDeadLine()));
                    if (!item.isCompleted()) {
                        completed.setText(String.valueOf(item.isCompleted()));

                        int z = item.getDeadLine().getDayOfYear() - i;
                        if (z > 0) {
                            i = z;
                        }
                        if (item.getDeadLine().getYear() == LocalDate.now().getYear()) {
                            reminder.setText(Integer.toString(i));
                            if (i - item.getRemaining() > 0) {
                                startDay.setText(" begin in " + Integer.toString(i - item.getRemaining()) + " days");
                            } else {
                                startDay.setText("Started for " + (-(i - item.getRemaining())) + " days");
                            }
                        } else {
                            double s = (LocalDate.now().getYear() - item.getDeadLine().getYear()) * 365.25;
                            reminder.setText(Double.toString((s + item.getDeadLine().getDayOfYear())));
                            startDay.setText("Over");
                        }

                    } else {
                        startDay.setText("");
                        reminder.setText("");
                        completed.setText(String.valueOf(item.isCompleted()));
                    }


                }
            }
        });

        wantAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return item.getDeadLine().equals(LocalDate.now());
            }
        };
        wantPriorityItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {

                return item.getDeadLine().getDayOfYear() - LocalDate.now().getDayOfYear() - item.getRemaining() <= 0 && !item.isCompleted();
            }
        };

        filteredList = new FilteredList<ToDoItem>(ToDoData.getInstance().getToDoItems(), wantAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
                return o1.getDeadLine().compareTo(o2.getDeadLine());
            }
        });


//        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoTaskview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean b) {
                        super.updateItem(item, b);
                        if (b) {
                            setText(null);
                        } else {
                            setPb(item);
                            setText(item.getShortDescription());

                            if (item.isCompleted()) {
                                setTextFill(Color.BLUE);
                                setText(item.getShortDescription() + " : completed");
                            } else if (item.getDeadLine().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (item.getDeadLine().getDayOfYear() - LocalDate.now().getDayOfYear() <= item.getRemaining()) {
                                setTextFill(Color.ORANGE);
                            } else {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
        todoTaskview.refresh();
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        todoTaskview.setItems(item.getToDoTasks());
        setPb(item);
        todoTaskview.setCellFactory(new Callback<ListView<ToDoTask>, ListCell<ToDoTask>>() {
            @Override
            public ListCell<ToDoTask> call(ListView<ToDoTask> toDoTaskListView) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                todoTaskview.setItems(item.getToDoTasks());

                ListCell<ToDoTask> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoTask task, boolean c) {

                        super.updateItem(task, c);
                        if (c) {
                            setText(null);
                        } else {
                            setText(task.getOrder() + ") " + task.getSticker() + "     estimation : " + doubleToTime(task.getEstimatedTime()));
                            if (isCompleteBool(task)) {
                                setTextFill(Color.BLUE);
                                setText(task.getSticker() + " Completed");
                            } else if (task.getDifficulty() > 6.99) {
                                setTextFill(Color.RED);
                                setText(task.getOrder() + ") " + "Hard : " + task.getSticker() + "       estimation :" + doubleToTime(task.getEstimatedTime()));
                            } else if (task.getDifficulty() > 3.99 && task.getDifficulty() < 6.99) {
                                setTextFill(Color.DARKORANGE);

                                setText(task.getOrder() + ") " + "Medium : " + task.getSticker() + "  estimation : " + doubleToTime(task.getEstimatedTime()));
                            } else if (task.getDifficulty() < 3.99) {
                                setTextFill(Color.GREEN);

                                setText(task.getOrder() + ") " + "Easy : " + task.getSticker() + "  estimation : " + doubleToTime(task.getEstimatedTime()));
                            }


                            saveData();


                        }
                    }
                };
                todoTaskview.refresh();
                todoListView.refresh();
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }

            ;
        });

        fileInfo.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
                if (!item.getToDoTasks().isEmpty()) {
                    item.getToDoTasks().get(item.taskIndex(task)).setInfo(t1);
                    item.saveTasks(item.getToDoTasks());
                    todoTaskview.setItems(item.getToDoTasks());
                }

            }
        });
        description.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                item.setDetails(t1);

            }
        });


        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                DataDoubleStore dataDoubleStore = new DataDoubleStore(t1);
                ToDoData.setFilename(t1);
                dataDoubleStore.loadContacts();
                description.clear();
                fileInfo.clear();
                todoListView.setItems(ToDoData.getInstance().getToDoItems());
                todoListView.getSelectionModel().selectFirst();

            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(" Add New ToDO");
        dialog.setHeaderText("Use this dialog to create a new ToDO Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResult();

            todoListView.getSelectionModel().select(newItem);
            fileInfo.clear();
            description.clear();
            setPb(newItem);
        }
    }

    @FXML
    public void handleClickListView() {
        ToDoItem item = selectedItem();
//         StringBuilder sb = new StringBuilder(item.getDetails());
//          sb.append("\n\n\n\n");


        description.setText(item.getDetails());
        deadlineLabel.setText(item.getDeadLine().toString());
    }

//    @FXML
//    public void handleFilterButton() {
//        ToDoItem selectedItem = selectedItem();
//        if (filterToggleButton.isSelected()) {
//            filteredList.setPredicate(wantTodaysItems);
//            if (filteredList.isEmpty()) {
//                description.clear();
//                deadlineLabel.setText("");
//                reminder.setText("");
//                startDay.setText("");
//            } else if (filteredList.contains(selectedItem)) {
//                todoListView.getSelectionModel().select(selectedItem);
//            } else {
//                todoListView.getSelectionModel().selectFirst();
//            }
//        } else {
//            filteredList.setPredicate(wantAllItems);
//            todoListView.getSelectionModel().select(selectedItem);
//        }
//
//    }

//    public void handleFilterButton2() {
//        ToDoItem selectedItem = selectedItem();
//        if (filterToggleButton2.isSelected()) {
//            filteredList.setPredicate(wantPriorityItems);
//            if (filteredList.isEmpty()) {
//                description.clear();
//                deadlineLabel.setText("");
//                reminder.setText("");
//                startDay.setText("");
//            } else if (filteredList.contains(selectedItem)) {
//                todoListView.getSelectionModel().select(selectedItem);
//            } else {
//                todoListView.getSelectionModel().selectFirst();
//            }
//
//        } else {
//            filteredList.setPredicate(wantAllItems);
//            todoListView.getSelectionModel().select(selectedItem);
//        }
//    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = selectedItem();
        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }

    }

    public void deleteItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDO Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure,data will be lost ,press OK to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get()) == ButtonType.OK) {
            ToDoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

    public void completeMission() {

        ToDoItem item = selectedItem();
        String s = "";
        s = (item.isCompleted()) ? "unfinished" : "completed";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm" + s + " Task");
        alert.setHeaderText("Is the mission " + item.getShortDescription() + " " + s + " ?");
        alert.setContentText("Task will be registered as " + s + ", press OK to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        item.setCompleted((((result.isPresent()) && (result.get()) == ButtonType.OK)) ? ((item.isCompleted()) ? false : true) : item.isCompleted());
        ;
        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        if (item.isCompleted()) {
            for (ToDoTask task : item.getToDoTasks()) {

                todoTaskview.getSelectionModel().select(task);
                if (!isCompleteBool(task))
                    isComplete();
            }
        }
        todoTaskview.refresh();
        completed.setText(Boolean.toString(item.isCompleted()));
        todoListView.getSelectionModel().selectFirst();
        todoListView.getSelectionModel().select(item);


    }

    public ToDoItem selectedItem() {
        return todoListView.getSelectionModel().getSelectedItem();
    }

    public void showEditItemDialog() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(" Edit ToDO");
        dialog.setHeaderText("Use this dialog to update a  ToDO Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();
            return;
        }
        DialogController dialogController = fxmlLoader.getController();
        dialogController.editTask(item);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ToDoItem itemNew = dialogController.updateTask();
            ToDoData.getInstance().deleteTodoItem(item);
            itemNew.setCompleted(false);
            ToDoData.getInstance().addToDoItem(itemNew);

            todoListView.getSelectionModel().select(itemNew);

        }
    }

    public void deleteMission() {
        ToDoData.getInstance().deleteTodoItem(todoListView.getSelectionModel().getSelectedItem());

    }

    @FXML
    public void showContactDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(" Edit ToDO");
        dialog.setHeaderText("Use this dialog to update a  ToDO Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();

        }
    }

    @FXML
    public String loadTasksList() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load application file");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")

        );
//        DirectoryChooser chooser=new DirectoryChooser();
        File file = chooser.showOpenDialog(mainBorderPane.getScene().getWindow());
//        File file=chooser.showSaveDialog(gridPane.getScene().getWindow());
        if (file != null) {
            String str = file.getName();
            ToDoData.setFilename(str);
            System.out.println(str);
            DataDoubleStore dataDoubleStore = new DataDoubleStore(str);

            dataDoubleStore.loadContacts();


            todoListView.setItems(ToDoData.getInstance().getToDoItems());
            todoListView.getSelectionModel().selectFirst();

            return str;
        } else {
            System.out.println("Chooser was cancel");
            return null;
        }
    }

    @FXML
    public void saveMissionList() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save application file");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")

        );
        File file = chooser.showSaveDialog(mainBorderPane.getScene().getWindow());

        if (file != null) {
            String str = file.getName();
            System.out.println("Saving " + str);
            DataDoubleStore dataDoubleStore = new DataDoubleStore(str);
            todoListView.setItems(ToDoData.getInstance().getToDoItems());
            dataDoubleStore.saveContacts();

            addCBoxItems();
            choiceBox.setItems(fileNames);


        } else {
            System.out.println("Chooser was cancel");

        }


    }

    public void saveCurrentList() {
        if (ToDoData.getFilename().equals("default.xml")) {
            saveMissionList();
        } else {
            DataDoubleStore dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
            todoListView.setItems(ToDoData.getInstance().getToDoItems());
            System.out.println("saving.." + ToDoData.getFilename());
            dataDoubleStore.saveContacts();
            dataDoubleStore.saveContacts();
        }
    }

    @FXML
    public void newMissionList() {
        if (!ToDoData.getFilename().equals("default.xml")) {
            DataDoubleStore dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
            dataDoubleStore.saveContacts();
            ToDoData.setFilename("default.xml");

            dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
            dataDoubleStore.loadContacts();
            todoListView.setItems(ToDoData.getInstance().getToDoItems());
            ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
            choiceBox.setValue("default.xml");
            todoTaskview.setItems(item.getToDoTasks());

        } else {
            saveMissionList();
        }
        description.clear();
        fileInfo.clear();
    }

    public void addCBoxItems() {
        fileNames.clear();

        File folder = new File("C:\\Users\\dupra\\OneDrive\\Java Projects\\ToDoList\\");
        File[] listOfFiles = folder.listFiles();
        for (File s : listOfFiles) {
            if (s.getName().contains(".xml") || s.getName().contains(".XML")) {
                fileNames.add(s.getName());
            }
        }
    }

    @FXML
    public void deleteList() {

        String s = ToDoData.getFilename();
        DataDoubleStore dataDoubleStore = new DataDoubleStore("default.xml");
        ToDoData.setFilename("default.xml");


        File file = new File("C:\\Users\\dupra\\OneDrive\\Java Projects\\ToDoList\\" + s);
        file.delete();

        addCBoxItems();

        choiceBox.setItems(fileNames);
        choiceBox.setValue("default.xml");
    }

    public int numberOfItem() {
        int count = 0;
        for (ToDoItem s : ToDoData.getInstance().getToDoItems()) {
            count++;
        }
        return count;
    }

    public int numberAccomplished() {
        int count = 0;
        for (ToDoItem s : ToDoData.getInstance().getToDoItems()) {
            if (s.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    @FXML
    public void addNewTask() {
        Dialog<ButtonType> dialog1 = new Dialog<>();
        dialog1.initOwner(mainBorderPane.getScene().getWindow());
        dialog1.setTitle(" Create Task");
        dialog1.setHeaderText("Create a new Task");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoTaskDialog.fxml"));
        try {
            dialog1.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();

        }
        dialog1.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog1.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog1.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            TaskController taskController = fxmlLoader.getController();

            ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
            ToDoTask task = taskController.processedResult(generateOrder(item));
            item.addTask(task);
            todoListView.refresh();

            item.saveTasks(item.getToDoTasks());
            todoTaskview.getSelectionModel().selectFirst();
            saveData();

            todoTaskview.refresh();
            setPb(item);
            todoTaskview.getSelectionModel().select(task);
            item.setCompleted(false);
        }
    }

    public int generateOrder(ToDoItem item) {
        int count = 1;
        for (ToDoTask task : item.getToDoTasks()) {
            count += 1;
        }
        return count;
    }

    @FXML
    public void orderUp() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
        if (task.getCompleted().equals("false")) {
            if (task.getOrder() != 1) {
                task.setOrder(task.getOrder() - 1);
            }

            for (ToDoTask t : item.getToDoTasks()) {
                if (t.getOrder() == task.getOrder() && !t.equals(task)) {
                    t.setOrder(t.getOrder() + 1);
                }

            }
            int count = 0;

            tasksortEngine(1);

            item.saveTasks(item.getToDoTasks());

            todoTaskview.refresh();
        }


    }

    public int findComplete() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();

        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();


        int comple = 0;
        for (ToDoTask t : item.getToDoTasks()) {
            if (t.getCompleted().equals("true")) {
                comple += 1;
            }
        }


        return comple;
    }

    @FXML
    public void orderDown() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
        if (task.getCompleted().equals("false"))
            if (task.getOrder() != (generateOrder(item) - 1)) {
                task.setOrder(task.getOrder() + 1);
            }
        for (ToDoTask t : item.getToDoTasks()) {
            if (t.getOrder() == task.getOrder() && !t.equals(task)) {
                t.setOrder(t.getOrder() - 1);
            }

        }
        tasksortEngine(1);
        item.saveTasks(item.getToDoTasks());
        todoTaskview.refresh();
    }

    public void tasksortEngine(int order) {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (order == 1) {
            item.getToDoTasks().sort(Comparator.comparing(ToDoTask::getOrder));
            sorting.setText("Order");
        } else if (order == 2) {
            item.getToDoTasks().sort(Comparator.comparing(ToDoTask::getCompleted));
            sorting.setText("Completion");
        } else if (order == 3) {
            item.getToDoTasks().sort(Comparator.comparing(ToDoTask::getDifficulty));
            sorting.setText("Difficulty");
        } else if (order == 4) {
            item.getToDoTasks().sort(Comparator.comparing(ToDoTask::getEstimatedTime));
            sorting.setText("Estimated time");
        }

    }

    @FXML
    public void sortButton() {
        if (sortChoice < 5) {
            sortChoice += 1;
        } else {
            sortChoice = 1;
        }
        tasksortEngine(sortChoice);
    }

    @FXML
    public void updateTask() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(" Edit Task");
        dialog.setHeaderText("Use this dialog to update your task");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoTaskDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog window");
            e.printStackTrace();
            return;
        }
        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();

        TaskController dialogController = fxmlLoader.getController();
        dialogController.editTask(task);
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            item.getToDoTasks().remove(item.taskIndex(task));
            task = dialogController.updateTasks(task);
            task.setCompleted("false");
            item.setCompleted(false);
            item.getToDoTasks().add(task);
            todoTaskview.setItems(item.getToDoTasks());
            item.saveTasks(item.getToDoTasks());
            saveData();
            todoTaskview.getSelectionModel().select(task);

        }

    }

    public void saveData() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        item.saveTasks(item.getToDoTasks());
        DataDoubleStore dataDoubleStore = new DataDoubleStore(ToDoData.getFilename());
        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        System.out.println("saving.." + ToDoData.getFilename());
        dataDoubleStore.saveContacts();
        setPb(item);


    }

    @FXML
    public void deleteTask() {
        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        item.removeTask(task);
        if (!fileInfo.equals("")) {
            fileInfo.clear();
        }
        saveData();
    }

    public void isComplete() {

        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();

        ToDoTask task = todoTaskview.getSelectionModel().getSelectedItem();
        if (task.getCompleted().equals("true")) {
            task.setCompleted("false");


            task.setOrder(generateOrder(item) - findComplete() - 1);
            tasksortEngine(1);

        } else if (task.getCompleted().equals("false")) {
            task.setCompleted("true");

            for (ToDoTask t : item.getToDoTasks()) {
                if (t.getOrder() > task.getOrder()) {
                    t.setOrder(t.getOrder() - 1);
                }

            }
            for (ToDoTask t : item.getToDoTasks()) {
                if (t.getCompleted().equals("true")) {
                    t.setOrder(generateOrder(item));
                }
            }
            tasksortEngine(2);
        }
        saveData();

        todoTaskview.refresh();
        if (setPb(item) == 1 && !item.isCompleted()) {
            completeMission();
        }
    }

    public boolean isCompleteBool(ToDoTask task) {
        if (task.getCompleted().equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public String doubleToTime(Double s) {
        int hours = s.intValue();
        Double minutes = (s - hours) * 60;
        int minute = minutes.intValue();

        if (hours == 0) {
            return minute + "min";
        } else if (minute == 0) {
            return hours + " H";
        }
        return hours + " H " + minute + " min";
    }

    public double setPb(ToDoItem item) {
        double s = 0;
        double t = 0;
        for (ToDoTask task : item.getToDoTasks()) {
            s += 1;
            if (task.getCompleted().equals("true")) {
                t += 1;
            }
        }
        t = t / s;
        pb.setProgress(t);
        return t;
    }


}

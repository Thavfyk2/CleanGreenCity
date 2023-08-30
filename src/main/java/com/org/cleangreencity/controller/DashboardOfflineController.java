package com.org.cleangreencity.controller;

import com.org.cleangreencity.utility.DragAndDropItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.org.cleangreencity.CGCApplication.rootStage;

public class DashboardOfflineController {

    @FXML
    public Label incorrectTitleText;
    @FXML
    public TextField updateTaskTitle;
    @FXML
    public TextField updateAssignee;
    @FXML
    public TextArea updateTaskDesc;
    @FXML
    public Button createTaskButton;
    @FXML
    public AnchorPane rootPane;

    @FXML
    private TextField taskTitle;
    @FXML
    private TextField assignee;
    @FXML
    private TextArea taskDesc;
    @FXML
    private ListView<String> backlogList;
    @FXML
    private ListView<String> inProgressList;
    @FXML
    private ListView<String> toTestList;
    @FXML
    private ListView<String> doneList;
    @FXML
    private ListView<String> deleteList;
    @FXML
    public Label welcomeLabel;
    @FXML
    public Pane newTaskPane;
    ObservableList<String> backlogItems = null;
    ObservableList<String> inProgressItems = null;
    ObservableList<String> toTestItems = null;
    ObservableList<String> doneItems = null;
    ObservableList<String> deleteItems = null;
    public static boolean onLineMode;
    public int lastCardId = 0;

    public void initialize() {
        dragAndDropTasks();
        onLineMode = false;
    }

    public void dragAndDropTasks() {
        backlogItems = FXCollections.observableArrayList();
        inProgressItems = FXCollections.observableArrayList();
        toTestItems = FXCollections.observableArrayList();
        doneItems = FXCollections.observableArrayList();
        deleteItems = FXCollections.observableArrayList();
        backlogList.setItems(backlogItems);
        inProgressList.setItems(inProgressItems);
        toTestList.setItems(toTestItems);
        doneList.setItems(doneItems);
        deleteList.setItems(deleteItems);
        new DragAndDropItems(inProgressList, inProgressItems, inProgressList, inProgressItems);
        new DragAndDropItems(toTestList, toTestItems, toTestList, toTestItems);
        new DragAndDropItems(doneList, doneItems, doneList, doneItems);
        new DragAndDropItems(backlogList, backlogItems, backlogList, backlogItems);
        new DragAndDropItems(deleteList, deleteItems, deleteList, deleteItems);
    }

    public void validateNewTaskOnAction() {
        if (taskTitle.getText().isBlank()) {
            return;
        }
        lastCardId++;
        backlogItems.add("ID-" + lastCardId +
                "\n" + taskTitle.getText() +
                "\n" + assignee.getText() +
                "\n" + taskDesc.getText());
        backlogList.setItems(backlogItems);
        taskTitle.clear();
        assignee.clear();
        taskDesc.clear();
    }

    public void disconnectOnAction(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ConnexionPageView.fxml")));
        rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        rootStage.setResizable(false);
        Scene scene = new Scene(root);
        rootStage.setScene(scene);
        rootStage.show();
    }
}
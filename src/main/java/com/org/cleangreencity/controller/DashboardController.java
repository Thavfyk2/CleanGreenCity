package com.org.cleangreencity.controller;

import com.org.cleangreencity.model.CardModel;
import com.org.cleangreencity.model.DashboardModel;
import com.org.cleangreencity.model.UserModel;
import com.org.cleangreencity.utility.DragAndDropItems;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.org.cleangreencity.CGCApplication.rootStage;
import static com.org.cleangreencity.controller.ConnexionPageController.currentUser;
import static com.org.cleangreencity.model.CardModel.*;
import static com.org.cleangreencity.model.DashboardModel.getCurrentDashboardNameID;
import static com.org.cleangreencity.model.UserModel.*;

public class DashboardController extends ExportController {

    public static DashboardModel currentDashboard = null;
    public CardModel selectedCard = new CardModel();
    @FXML
    public MenuBar menu;
    @FXML
    public Label incorrectTitleText, welcomeLabel;
    @FXML
    public TextField updateTaskTitle, updateAssignee, search;
    @FXML
    public MenuItem saveButton;
    @FXML
    public Button createTaskButton;
    @FXML
    public Pane teamListView, newTaskPane, teamListPane;
    @FXML
    public ComboBox<String> listOfProject;
    @FXML
    public Menu exportMenu;
    @FXML
    public ImageView deleteProject, addTeamMember;
    @FXML
    public TextField cmdTextField;
    ObservableList<String> backlogItems = null, inProgressItems = null, toTestItems = null, doneItems = null, deleteItems = null, userItems = null;
    @FXML
    private AnchorPane editTask, addProject, rootPane, cmdPane;
    @FXML
    private TextField teamMember, taskTitle, assignee, projectNameTextField;
    @FXML
    private TextArea taskDesc, updateTaskDesc, projectDescTextField;
    @FXML
    private ListView<String> usersList, backlogList, inProgressList, toTestList, doneList, deleteList;
    @FXML
    private Label backlogLabel, inProgressLabel, toTestLabel, doneLabel, incorrectTitleText2, cmdLabel;
    private boolean isDarkMode = false;

    public void initialize() {
        DashboardOfflineController.onLineMode = true;
        currentUser.setUser_id(getUserID(currentUser.getUsername()));
        welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        dragAndDropTasks();
        initializeLists();
        getProjectList();

        teamMember.setOnKeyPressed(event2 -> {
            if (event2.getCode() == KeyCode.ENTER) {
                addTeamMember();
            }
        });

        editTask.setOnKeyPressed(event2 -> {
            if (event2.getCode() == KeyCode.ESCAPE) {
                closeUpdateTaskOnAction();
            }
        });

        cmdTextField.setOnKeyPressed(event2 -> {
            if (event2.getCode() == KeyCode.ESCAPE) {
                closeCmdOnAction();
            }
        });
    }

    private void initializeLists() {

        backlogList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                inProgressList.getSelectionModel().clearSelection();
                toTestList.getSelectionModel().clearSelection();
                doneList.getSelectionModel().clearSelection();
            }
        });

        inProgressList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                backlogList.getSelectionModel().clearSelection();
                toTestList.getSelectionModel().clearSelection();
                doneList.getSelectionModel().clearSelection();
            }
        });

        toTestList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                backlogList.getSelectionModel().clearSelection();
                inProgressList.getSelectionModel().clearSelection();
                doneList.getSelectionModel().clearSelection();
            }
        });

        doneList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                inProgressList.getSelectionModel().clearSelection();
                toTestList.getSelectionModel().clearSelection();
                backlogList.getSelectionModel().clearSelection();
            }
        });

    }

    public List<CardModel> getTasksFromDashboard(int dashboardId, String searchTaskName) {

        List<CardModel> cardList = new ArrayList<>();
        String query;
        try {

            Connection connectDB = DatabaseConnection.getConnection();
            Statement statement = connectDB.createStatement();

            if (searchTaskName == null) query = "SELECT * FROM t_card WHERE dashboard_id = " + dashboardId;
            else
                query = "SELECT * FROM t_card WHERE name LIKE '%" + searchTaskName + "%' AND dashboard_id = " + dashboardId;

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                CardModel card = new CardModel();
                card.setCardId(resultSet.getInt("card_id"));
                card.setName(resultSet.getString("name"));
                card.setDescription(resultSet.getString("description"));
                card.setPersonInCharge(resultSet.getInt("user_id"));
                card.setPosition(resultSet.getInt("position"));
                cardList.add(card);
            }

            resultSet.close();
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cardList;
    }

    @FXML
    private void setTasksToDashboard() {
        backlogItems.clear();
        inProgressItems.clear();
        toTestItems.clear();
        doneItems.clear();
        List<CardModel> cardsOfSelectedProject;

        int countBacklog = 0;
        int countInProgress = 0;
        int countToTest = 0;
        int countDone = 0;

        if (search.getText() != null)
            cardsOfSelectedProject = getTasksFromDashboard(currentDashboard.getDashboardId(), search.getText());
        else {
            cardsOfSelectedProject = getTasksFromDashboard(currentDashboard.getDashboardId(), null);

        }

        for (CardModel card : cardsOfSelectedProject) {
            currentDashboard.setTasks(card);
            switch (card.getPosition()) {
                case 0 -> {
                    backlogItems.add(card.toString());
                    countBacklog++;
                }
                case 1 -> {
                    inProgressItems.add(card.toString());
                    countInProgress++;
                }
                case 2 -> {
                    toTestItems.add(card.toString());
                    countToTest++;
                }
                case 3 -> {
                    doneItems.add(card.toString());
                    countDone++;
                }
                default -> {
                }
            }
        }

        backlogLabel.setText("BACKLOG " + countBacklog);
        inProgressLabel.setText("IN PROGRESS " + countInProgress);
        toTestLabel.setText("TO TEST " + countToTest);
        doneLabel.setText("DONE " + countDone);

        backlogList.setItems(backlogItems);
        inProgressList.setItems(inProgressItems);
        toTestList.setItems(toTestItems);
        doneList.setItems(doneItems);
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


    public void getProjectList() {

        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String query = "SELECT title " + "FROM t_dashboard t,r_user_dashboard r " + "WHERE r.dashboard_id = t.dashboard_id " + "AND r.user_id = ?";

            PreparedStatement statement = connectDB.prepareStatement(query);
            statement.setInt(1, currentUser.getUser_id());
            ResultSet resultSet = statement.executeQuery();

            listOfProject.getItems().clear();
            while (resultSet.next()) {
                String value = resultSet.getString("title");
                listOfProject.getItems().add(value);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project list", e);
        }

        listOfProject.setOnAction(event -> {

            currentDashboard = new DashboardModel();
            currentDashboard.setTitle(listOfProject.getSelectionModel().getSelectedItem());
            currentDashboard.setDashboardId(getCurrentDashboardNameID(currentDashboard.getTitle()));
            setTasksToDashboard();
            updateTaskProcess();

            newTaskPane.setDisable(false);
            teamListPane.setDisable(false);
            addTeamMember.setOpacity(1);
            deleteProject.setVisible(true);
            try {
                getTeamList(currentDashboard.getDashboardId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void getTeamList(int dashboardId) throws SQLException {

        userItems = FXCollections.observableArrayList();

        String query = "SELECT * " + "FROM t_user t, r_user_dashboard r " + "WHERE r.user_id = t.user_id " + "AND r.dashboard_id = ?";

        try (Connection connectDB = DatabaseConnection.getConnection(); PreparedStatement statement = connectDB.prepareStatement(query)) {

            statement.setInt(1, dashboardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UserModel user = new UserModel();

                user.setUser_id(Integer.parseInt(resultSet.getString("user_id")));
                user.setUsername(resultSet.getString("username"));

                if (!currentDashboard.memberExist(user)) {
                    currentDashboard.setTeamList(user);
                }

                userItems.add(user.getUsername());
            }

            usersList.setItems(userItems);
        }
    }

    public void createNewDashboardOnAction() {
        addProject.setVisible(true);
        rootPane.setDisable(true);
        GaussianBlur blur = new GaussianBlur(10);
        rootPane.setEffect(blur);
    }

    public void DeleteDashboardOnAction() {

        Connection connection = DatabaseConnection.getConnection();
        int obtainProjectId = getCurrentDashboardNameID(currentDashboard.getTitle());

        String DeleteDashboardFromT_dashboard = "DELETE  from t_dashboard where dashboard_id= ? ";
        String DeleteDashboardFromT_card = "DELETE  from t_card where dashboard_id= ? ";
        String DeleteDashboardFromR_user_dashboard = "DELETE  from r_user_dashboard where dashboard_id= ? ";

        try {

            PreparedStatement statementDeleteProject = connection.prepareStatement(DeleteDashboardFromR_user_dashboard);
            PreparedStatement statementDeleteProject2 = connection.prepareStatement(DeleteDashboardFromT_card);
            PreparedStatement statementDeleteProject3 = connection.prepareStatement(DeleteDashboardFromT_dashboard);
            statementDeleteProject.setInt(1, obtainProjectId);
            statementDeleteProject2.setInt(1, obtainProjectId);
            statementDeleteProject3.setInt(1, obtainProjectId);

            int preparedStatement = statementDeleteProject.executeUpdate();
            if (preparedStatement > 0) {
                statementDeleteProject2.executeUpdate();
                preparedStatement = statementDeleteProject3.executeUpdate();
                if (preparedStatement > 0) {
                    System.out.println("Dashboard deleted successfully.");
                    getProjectList();
                    newTaskPane.setDisable(true);
                    teamListPane.setDisable(true);
                    addTeamMember.setOpacity(0.5);
                } else {
                    System.out.println("No dashboard deleted. Maybe the dashboard with ID: " + obtainProjectId + " doesn't exist.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTeamMember() {
        teamMember.setVisible(true);
        try {
            Connection connection = DatabaseConnection.getConnection();

            if (!userExists(connection, teamMember.getText())) {
                System.out.println("User doesn't exist, choose another one");
            } else {

                String addTeamMember = "INSERT INTO r_user_dashboard (user_id, dashboard_id) VALUES (?, ?)";
                try (PreparedStatement statementAddCard = connection.prepareStatement(addTeamMember)) {
                    statementAddCard.setInt(1, getUserID(teamMember.getText()));
                    statementAddCard.setInt(2, currentDashboard.getDashboardId());

                    int rowsAffected = statementAddCard.executeUpdate();
                    if (rowsAffected > 0) {
                        getTeamList(currentDashboard.getDashboardId());
                        teamMember.clear();
                        teamMember.setVisible(false);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskProcess() {

        StringProperty selectedItem = new SimpleStringProperty();

        backlogList.setOnMouseClicked((MouseEvent event1) -> {
            if (event1.getClickCount() == 2) { // Check if it's a double click
                selectedItem.set(backlogList.getSelectionModel().getSelectedItem());
                try {
                    updateTaskPreparation(selectedItem);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        inProgressList.setOnMouseClicked((MouseEvent event1) -> {
            if (event1.getClickCount() == 2) { // Check if it's a double click
                selectedItem.set(inProgressList.getSelectionModel().getSelectedItem());
                try {
                    updateTaskPreparation(selectedItem);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        toTestList.setOnMouseClicked((MouseEvent event1) -> {
            if (event1.getClickCount() == 2) { // Check if it's a double click
                selectedItem.set(toTestList.getSelectionModel().getSelectedItem());
                try {
                    updateTaskPreparation(selectedItem);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        doneList.setOnMouseClicked((MouseEvent event1) -> {
            if (event1.getClickCount() == 2) {
                selectedItem.set(doneList.getSelectionModel().getSelectedItem());
                try {
                    updateTaskPreparation(selectedItem);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void validateNewTaskOnAction() {
        if (taskTitle.getText().isBlank()) {
            return;
        }

        int lastCardId = getLastCardId();

        CardModel newTask = new CardModel(lastCardId, currentDashboard.getDashboardId(), taskTitle.getText(), taskDesc.getText(), getUserID(assignee.getText()), 0);

        try {
            Connection connectDB = DatabaseConnection.getConnection();

            if (!userIsMemberInCurrentDashboard(connectDB, assignee.getText())) {
                System.out.println("User doesn't exist, choose another one");
                return;
            }

            if (cardExists(connectDB, newTask.getName())) {
                System.out.println("Task already exists, choose another one");
                return;
            }

            if (addCard(connectDB, newTask)) {
                backlogItems.add(newTask.toString());
                backlogList.setItems(backlogItems);
                taskTitle.clear();
                assignee.clear();
                taskDesc.clear();
            } else {
                System.out.println("Failed to add card");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskPreparation(StringProperty selectedItem) throws IOException, SQLException {
        editTask.setVisible(true);
        rootPane.setDisable(true);
        GaussianBlur blur = new GaussianBlur(10);
        rootPane.setEffect(blur);

        String selected_item = selectedItem.toString();
        String selectedItemId = selected_item.substring(selected_item.indexOf("-") + 1, selected_item.indexOf("\n"));
        Connection connectDB = DatabaseConnection.getConnection();
        Statement statement = connectDB.createStatement();

        String query = "SELECT * FROM t_card WHERE card_id = " + selectedItemId;
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {

            selectedCard.setCardId(resultSet.getInt("card_id"));
            selectedCard.setName(resultSet.getString("name"));
            selectedCard.setDescription(resultSet.getString("description"));
            selectedCard.setPersonInCharge(resultSet.getInt("user_id"));
            selectedCard.setPosition(resultSet.getInt("position"));
        }

        updateTaskTitle.setText(selectedCard.getName());
        updateAssignee.setText(getUserUserName(selectedCard.getPersonInCharge()));
        updateTaskDesc.setText(selectedCard.getDescription());

        statement.close();
        connectDB.close();
    }

    public void updateTaskOnAction() throws SQLException {
        Connection connectDB = DatabaseConnection.getConnection();

        if (!userIsMemberInCurrentDashboard(connectDB, updateAssignee.getText())) {
            incorrectTitleText.setText("User doesn't exist, choose another one");
            return;
        }

        if (cardExists(connectDB, updateTaskTitle.getText()) && (!updateTaskTitle.getText().equals(selectedCard.getName()))) {
            incorrectTitleText.setText("Task already exists, choose another one");
            return;
        }

        String updateQuery = "UPDATE t_card t SET t.name = ?, t.description = ?, t.user_id = ? WHERE t.card_id = ?";
        PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
        preparedStatement.setString(1, updateTaskTitle.getText());
        preparedStatement.setString(2, updateTaskDesc.getText());
        preparedStatement.setInt(3, getUserID(updateAssignee.getText()));
        preparedStatement.setInt(4, selectedCard.getCardId());

        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Update successful.");
            closeUpdateTaskOnAction();
            setTasksToDashboard();
        } else {
            System.out.println("No rows updated.");
        }
        preparedStatement.close();
        connectDB.close();
    }

    public void closeUpdateTaskOnAction() {
        editTask.setVisible(false);
        rootPane.setDisable(false);
        GaussianBlur blur = new GaussianBlur(0);
        rootPane.setEffect(blur);
    }

    private void changeAllLabelAndButtonFontColor(String colorCode, Pane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Label || node instanceof Button) {
                node.setStyle("-fx-text-fill: " + colorCode + ";");
            } else if (node instanceof Pane) {
                changeAllLabelAndButtonFontColor(colorCode, (Pane) node);
            }
        }
    }

    private void changeAllLabelFontColor(String colorCode, Pane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Label) {
                node.setStyle("-fx-text-fill: " + colorCode + ";");
            } else if (node instanceof Pane) {
                changeAllLabelFontColor(colorCode, (Pane) node);
            }
        }
    }

    private void changeAllPaneBackgroundColor(String colorCode, Pane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Pane) {
                node.setStyle("-fx-background-color: " + colorCode + ";");
            }
        }
    }

    private void changeButtonBackgroundColor(String colorCode, Pane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                node.setStyle("-fx-background-color: " + colorCode + ";");
            } else if (node instanceof Pane) {
                changeButtonBackgroundColor(colorCode, (Pane) node);
            }
        }
    }

    private void changeAllButtonFontColor(String colorCode, Pane container) {
        for (Node node : container.getChildren()) {
            if (node instanceof Button) {
                node.setStyle("-fx-text-fill: " + colorCode + ";");
            } else if (node instanceof Pane) {
                changeAllButtonFontColor(colorCode, (Pane) node);
            }
        }
    }

    @FXML
    private void darkModeOnAction(MouseEvent event) {
        if (isDarkMode) {
            rootPane.setStyle("-fx-background-color: #FFFFFF;");
            changeAllPaneBackgroundColor("#FFFFFF", rootPane);
            changeAllLabelFontColor("#000000", rootPane);
        } else {
            rootPane.setStyle("-fx-background-color: #000000;");
            changeAllPaneBackgroundColor("#424242", rootPane);
            changeAllLabelFontColor("#FFFFFF", rootPane);
        }
        isDarkMode = !isDarkMode;
    }

    @FXML
    private void cmdOnAction(ActionEvent event) {
        String command = cmdTextField.getText().toUpperCase().trim();
        cmdLabel.setText(cmdLabel.getText() + "\n" + command);
        if (cmdTextField.getLayoutY() < 200) {
            cmdTextField.setLayoutY(cmdTextField.getLayoutY() + 20);
        }
        cmdTextField.setStyle("-fx-text-fill: #FFFFFF;-fx-background-color: #5D5D5D;");

        if (command.startsWith("SET BACKGROUND COLOR ")) {
            String colorCode = command.substring("SET BACKGROUND COLOR ".length());
            rootPane.setStyle("-fx-background-color: " + colorCode + ";");

        } else if (command.startsWith("SET ALL LABEL AND BUTTON FONT COLOR ")) {
            String colorCode = command.substring("SET ALL LABEL AND BUTTON FONT COLOR ".length());
            changeAllLabelAndButtonFontColor(colorCode, rootPane);
        } else if (command.startsWith("SET ALL PANE COLOR ")) {
            String colorCode = command.substring("SET ALL PANE COLOR ".length());
            changeAllPaneBackgroundColor(colorCode, rootPane);
        } else if (command.startsWith("SET ALL BUTTON BACKGROUND COLOR ")) {
            String colorCode = command.substring("SET ALL BUTTON BACKGROUND COLOR ".length());
            changeButtonBackgroundColor(colorCode, rootPane);
        } else if (command.startsWith("SET ALL LABEL FONT COLOR ")) {
            String colorCode = command.substring("SET ALL LABEL FONT COLOR ".length());
            changeAllLabelFontColor(colorCode, rootPane);
        } else if (command.startsWith("SET ALL BUTTON FONT COLOR ")) {
            String colorCode = command.substring("SET ALL BUTTON FONT COLOR ".length());
            changeAllButtonFontColor(colorCode, rootPane);
        } else {
            cmdLabel.setText(cmdLabel.getText() + "\n" + "ERROR : Invalid Command");
            if (cmdTextField.getLayoutY() < 200) {
                cmdTextField.setLayoutY(cmdTextField.getLayoutY() + 20);
            }
        }
    }

    public void chooseJarFile() {
        FileDialog fileDialog = new FileDialog((java.awt.Frame) null, "Select a .jar File");
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setFile("*.jar");
        fileDialog.setVisible(true);
        String jarFilePath = fileDialog.getDirectory() + fileDialog.getFile();
        String menuName = jarFilePath.substring(jarFilePath.lastIndexOf("\\") + 1, jarFilePath.lastIndexOf("."));
        MenuItem exportOtherType = new MenuItem(menuName);
        exportMenu.getItems().add(exportOtherType);
        exportOtherType.setOnAction(event -> {
            exportToAnotherFormat(jarFilePath);
            System.out.println("Export Other Type selected!");
        });
    }

    @FXML
    private void disconnectOnAction(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(DashboardController.class.getResource("/fxml/ConnexionPageView.fxml")));
        rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        rootStage.setResizable(false);
        Scene scene = new Scene(root);
        rootStage.setScene(scene);
        rootStage.show();
    }

    @FXML
    private void fullScreenOnAction(MouseEvent event) {
        rootStage.setFullScreen(!rootStage.isFullScreen());
    }

    private void closeCmdOnAction() {
        cmdPane.setVisible(false);
        rootPane.setDisable(false);
        GaussianBlur blur = new GaussianBlur(0);
        rootPane.setEffect(blur);
    }

    @FXML
    private void openCmdOnAction(MouseEvent event) {
        cmdPane.setVisible(true);
        rootPane.setDisable(true);
        GaussianBlur blur = new GaussianBlur(10);
        rootPane.setEffect(blur);
    }

    @FXML
    private void closeNewDashboardTaskOnAction() {
        addProject.setVisible(false);
        rootPane.setDisable(false);
        GaussianBlur blur = new GaussianBlur(0);
        rootPane.setEffect(blur);
    }

    @FXML
    private void validateNewDashboardOnAction() {
        if (projectNameTextField.getText().isBlank()) {
            incorrectTitleText2.setText("Some information is missing");
        } else {
            try (Connection connectDB = DatabaseConnection.getConnection()) {
                String verifyNameExistsQuery = "SELECT count(1) FROM t_dashboard WHERE title = ?";
                String addProjectQuery = "INSERT INTO t_dashboard(title, description) VALUES (?, ?)";
                String addProjectUserQuery = "INSERT INTO r_user_dashboard(user_id, dashboard_id) VALUES (?, ?)";

                try (PreparedStatement verifyNameExistsStatement = connectDB.prepareStatement(verifyNameExistsQuery); PreparedStatement addProjectStatement = connectDB.prepareStatement(addProjectQuery)) {
                    verifyNameExistsStatement.setString(1, projectNameTextField.getText());
                    ResultSet queryResultSignInUsernameCheck = verifyNameExistsStatement.executeQuery();

                    if (queryResultSignInUsernameCheck.next() && queryResultSignInUsernameCheck.getInt(1) == 1) {
                        incorrectTitleText2.setText("Project name already exists. Please choose another one.");
                    } else {
                        queryResultSignInUsernameCheck.close();
                        addProjectStatement.setString(1, projectNameTextField.getText());
                        addProjectStatement.setString(2, projectDescTextField.getText());

                        int rowsAffected = addProjectStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            String extractProjectIdQuery = "SELECT dashboard_id FROM t_dashboard WHERE title = ?";
                            PreparedStatement extractProjectIdQueryStatement = connectDB.prepareStatement(extractProjectIdQuery);
                            {
                                extractProjectIdQueryStatement.setString(1, projectNameTextField.getText());
                                ResultSet extractProjectIdQueryCheck = extractProjectIdQueryStatement.executeQuery();
                                if (extractProjectIdQueryCheck.next()) {
                                    int projectId = extractProjectIdQueryCheck.getInt(1);
                                    extractProjectIdQueryCheck.close();
                                    try (PreparedStatement addProjectUserStatement = connectDB.prepareStatement(addProjectUserQuery)) {
                                        addProjectUserStatement.setInt(1, currentUser.getUser_id());
                                        addProjectUserStatement.setInt(2, projectId);
                                        rowsAffected = addProjectUserStatement.executeUpdate();
                                        if (rowsAffected > 0) {
                                            incorrectTitleText2.setText("Project added successfully");
                                            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                                            delay.setOnFinished(event -> {
                                                projectNameTextField.clear();
                                                projectDescTextField.clear();
                                                incorrectTitleText2.setText("");
                                                closeNewDashboardTaskOnAction();
                                                getProjectList();
                                            });
                                            delay.play();
                                        } else {
                                            incorrectTitleText2.setText("Failed to add project");
                                        }
                                    }
                                } else {
                                    incorrectTitleText2.setText("Failed to retrieve user or project data");
                                }
                            }
                        } else {
                            incorrectTitleText.setText("Failed to add project");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error during project validation.", e);
            }
        }
    }

    private void handleCreateTaskButton(ActionEvent event) {
    }
}



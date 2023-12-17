package com.org.cleangreencity.controller;

import com.org.cleangreencity.CGCApplication;
import com.org.cleangreencity.model.UserModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.util.Objects;

public class ConnexionPageController {
    public static UserModel currentUser = new UserModel();
    public static Stage rootStage;
    public Scene scene;

    @FXML
    public Button forgotPassword;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label incorrectEmailOrPasswordText;

    public static void switchToNewDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(ConnexionPageController.class.getResource("/fxml/DashboardView.fxml"));
        Parent root = loader.load();

        rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        rootStage.setResizable(false);

        Scene scene = new Scene(root);
        rootStage.setScene(scene);
        rootStage.centerOnScreen();
        rootStage.show();
    }

    @FXML
    private void onOfflineButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/OfflineDashboardView.fxml")));
        CGCApplication.rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CGCApplication.rootStage.setResizable(false);
        scene = new Scene(root);
        CGCApplication.rootStage.setScene(scene);
        CGCApplication.rootStage.show();
    }

    public void connectButton(ActionEvent event) throws SQLException, IOException {
        if (usernameTextField.getText().isBlank() || passwordTextField.getText().isBlank()) {
            incorrectEmailOrPasswordText.setText("Some information are missing");
            incorrectEmailOrPasswordText.setVisible(true);
        } else {
            currentUser.setUsername(usernameTextField.getText());

            try (Connection connectDB = DatabaseConnection.getConnection()) {
                String verifyLoginUsername = "SELECT count(1) FROM t_user WHERE username = ? AND password = ?";
                PreparedStatement statementLoginUsername = connectDB.prepareStatement(verifyLoginUsername);
                statementLoginUsername.setString(1, usernameTextField.getText());
                statementLoginUsername.setString(2, passwordTextField.getText());
                ResultSet queryResultLoginUsername = statementLoginUsername.executeQuery();

                if (queryResultLoginUsername.next() && queryResultLoginUsername.getInt(1) == 1) {
                    String username = usernameTextField.getText();
                    UserModel.getInstance().setUsername(username);

                    String getUserId = "SELECT user_id FROM t_user WHERE username = ?";
                    PreparedStatement statementGetUserID = connectDB.prepareStatement(getUserId);
                    statementGetUserID.setString(1, username); // Set the parameter value

                    ResultSet queryGetId = statementGetUserID.executeQuery();

                    if (queryGetId.next()) {
                        int userId = queryGetId.getInt("user_id");
                        UserModel.getInstance().setUser_id(userId);
                    }

                    queryGetId.close();
                    statementGetUserID.close();
                    switchToNewDashboard(event);
                } else {
                    incorrectEmailOrPasswordText.setText("Invalid username or password");
                }
                statementLoginUsername.close();
                queryResultLoginUsername.close();
            } catch (SQLException e) {
                throw new SQLException("Error during login validation.", e);
            }
        }
    }

    @FXML
    private void switchToInscription(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/InscriptionPageView.fxml")));
        rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        rootStage.setResizable(false);
        scene = new Scene(root);
        rootStage.setScene(scene);
        rootStage.show();
    }

    public void onForgotPassword(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ChangePasswordView.fxml")));
        rootStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        rootStage.setResizable(false);
        scene = new Scene(root);
        rootStage.setScene(scene);
        rootStage.show();
    }
}
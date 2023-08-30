package com.org.cleangreencity.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


public class InscriptionController {

    public Stage stage;
    public Scene scene;

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label incorrectEmailOrPasswordText;

    public void validateInscriptionOnAction(ActionEvent event) throws SQLException {
        if (usernameTextField.getText().isBlank() || passwordTextField.getText().isBlank() || emailTextField.getText().isBlank()) {
            incorrectEmailOrPasswordText.setText("Some informations are missing");
            incorrectEmailOrPasswordText.setVisible(true);
        } else {
            try (Connection connectDB = DatabaseConnection.getConnection()) {
                String verifyUsernameExists = "SELECT count(1) FROM t_user WHERE username = ?";
                String verifyEmailExists = "SELECT count(1) FROM t_user WHERE email = ?";
                String addUser = "INSERT INTO t_user(username, password, email)\n" +
                        "VALUES (?, ?, ?);";
                PreparedStatement statementSignInUsername = connectDB.prepareStatement(verifyUsernameExists);
                statementSignInUsername.setString(1, usernameTextField.getText());

                PreparedStatement statementSignInEmail = connectDB.prepareStatement(verifyEmailExists);
                statementSignInEmail.setString(1, emailTextField.getText());

                PreparedStatement statementAddUser = connectDB.prepareStatement(addUser);
                statementAddUser.setString(1, usernameTextField.getText());
                statementAddUser.setString(2, passwordTextField.getText());
                statementAddUser.setString(3, emailTextField.getText());

                ResultSet queryResultSignInUsernameCheck = statementSignInUsername.executeQuery();
                ResultSet queryResultLoginEmailCheck = statementSignInEmail.executeQuery();

                if (queryResultSignInUsernameCheck.next() && queryResultSignInUsernameCheck.getInt(1) == 1) {
                    incorrectEmailOrPasswordText.setText("Username exists already, choose another one");
                    incorrectEmailOrPasswordText.setVisible(true);
                } else if (!emailTextField.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    incorrectEmailOrPasswordText.setText("Invalid email");

                    incorrectEmailOrPasswordText.setVisible(true);
                } else if (queryResultLoginEmailCheck.next() && queryResultLoginEmailCheck.getInt(1) == 1) {
                    incorrectEmailOrPasswordText.setText("You already have an account, contact the administrator to get or change your password.");

                    incorrectEmailOrPasswordText.setVisible(true);
                } else {
                    int rowsAffected = statementAddUser.executeUpdate();

                    if (rowsAffected > 0) {
                        incorrectEmailOrPasswordText.setText("User added successfully");
                        incorrectEmailOrPasswordText.setVisible(true);

                        Platform.runLater(() -> {

                            try {
                                Thread.sleep(3000);
                                switchToConnexion(event);
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }


                        });
                    } else {
                        incorrectEmailOrPasswordText.setText("Failed to add user");
                        incorrectEmailOrPasswordText.setVisible(true);
                    }

                    statementAddUser.close();
                }
                statementSignInUsername.close();
                queryResultSignInUsernameCheck.close();

                statementSignInEmail.close();
                queryResultLoginEmailCheck.close();
            } catch (SQLException e) {
                throw new SQLException("Error during Signin validation.", e);
            }
        }
    }

    @FXML
    private void switchToConnexion(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ConnexionPageView.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

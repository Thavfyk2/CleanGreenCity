package com.org.cleangreencity.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class ChangePasswordController {

    public Stage stage;
    public Scene scene;
    @FXML
    public Label errorMessageLabel;
    @FXML
    public Button changePassword;
    @FXML
    public TextField usernameTextField1;
    @FXML
    public TextField emailTextField1;
    @FXML
    public PasswordField passwordTextField1;
    @FXML
    public PasswordField passwordTextField11;

    public void changePasswordOnAction(ActionEvent event) throws SQLException {

        if (usernameTextField1.getText().isBlank() || passwordTextField1.getText().isBlank() || emailTextField1.getText().isBlank() || passwordTextField11.getText().isBlank()) {
            errorMessageLabel.setText("Some informations are missing");
            errorMessageLabel.setVisible(true);
        } else {

            try (Connection connectDB = DatabaseConnection.getConnection()) {
                String checkUsernameEmail = "SELECT COUNT(1) FROM t_user WHERE username = ? AND email = ?;";
                String updatePassword = "UPDATE t_user SET password = ? WHERE username = ?";
                PreparedStatement statementCheckUsernameEmail = connectDB.prepareStatement(checkUsernameEmail);
                statementCheckUsernameEmail.setString(1, usernameTextField1.getText());
                statementCheckUsernameEmail.setString(2, emailTextField1.getText());

                PreparedStatement statementUpdatePassword = connectDB.prepareStatement(updatePassword);
                statementUpdatePassword.setString(1, passwordTextField1.getText());
                statementUpdatePassword.setString(2, usernameTextField1.getText());

                ResultSet queryResultCheckUsernameEmail = statementCheckUsernameEmail.executeQuery();

                if (queryResultCheckUsernameEmail.next() && queryResultCheckUsernameEmail.getInt(1) != 1) {
                    errorMessageLabel.setText("Username/Email incorrect");
                    errorMessageLabel.setVisible(true);
                } else if (!Objects.equals(passwordTextField1.getText(), passwordTextField11.getText())) {
                    errorMessageLabel.setText("Passwords doesn't match");
                    errorMessageLabel.setVisible(true);
                } else {

                    int rowsAffected = statementUpdatePassword.executeUpdate();

                    if (rowsAffected > 0) {
                        errorMessageLabel.setText("Password successfully changed");
                        errorMessageLabel.setVisible(true);

                        Platform.runLater(() -> {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                switchToConnexionn(event);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        errorMessageLabel.setText("Failed to change password");
                        errorMessageLabel.setVisible(true);
                    }
                }
            } catch (SQLException e) {
                throw new SQLException("Error during Sign in validation.", e);
            }
        }
    }

    public void switchToConnexionn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ConnexionPageView.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

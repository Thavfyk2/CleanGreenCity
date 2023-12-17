package com.org.cleangreencity;

import com.org.cleangreencity.controller.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CGCApplication extends Application {
    public static Stage rootStage;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CGCApplication.class.getResource("/fxml/ConnexionPageView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("CleanGreenCity Trellu");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/Trello-Logo.png")));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
        rootStage=stage;
    }

    public static void main(String[] args) {
        launch();
    }
}
package com.profservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Альтернативный способ загрузки с явным URL
            System.out.println("JavaFX version: " + System.getProperty("javafx.version"));
            URL fxmlUrl = getClass().getResource("/com/profservice/view/auth/login.fxml");
            System.out.println("Actual FXML path: " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            primaryStage.setTitle("HRM System - Авторизация");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("===== FXML LOAD ERROR =====");
            System.err.println("Failed to load FXML file");
            System.err.println("Path attempted: /com/profservice/view/auth/login.fxml");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
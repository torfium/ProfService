package com.profservice.controller;

import com.profservice.model.User;
import com.profservice.service.AuthService;
import com.profservice.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

//        if (username.isEmpty() || password.isEmpty()) {
//            AlertUtil.showError("Ошибка входа", "Пожалуйста, введите имя пользователя и пароль");
//            return;
//        }

        try {
            User user = AuthService.authenticate(username, password);
            if (user != null) {
                openMainWindow(user);
                closeLoginWindow();
            } else {
                AlertUtil.showError("Ошибка входа", "Неверное имя пользователя или пароль");
            }
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось подключиться к базе данных");
            e.printStackTrace();
        } catch (IOException e) {
            AlertUtil.showError("Ошибка приложения", "Не удалось загрузить главное окно");
            e.printStackTrace();
        }
    }

    private void openMainWindow(User user) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/main/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setCurrentUser(user);

            Stage stage = new Stage();
            stage.setTitle("HRM System - " + user.getFullName());
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            throw new IOException("Failed to load main.fxml", e);
        }
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
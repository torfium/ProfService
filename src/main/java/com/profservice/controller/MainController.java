package com.profservice.controller;

import com.profservice.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML private BorderPane mainPane;
    @FXML private TabPane tabPane;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        try {
            // Добавляем вкладки в зависимости от роли пользователя
            if (currentUser.isAdmin() || currentUser.isManager()) {
                addTab("Сотрудники", "/com/profservice/view/employee/employee_list.fxml");
                addTab("Должности", "/com/profservice/view/position/position_list.fxml");
                addTab("Отделы", "/com/profservice/view/department/department_list.fxml");
                addTab("Прием на работу", "/com/profservice/view/hiring/hiring_list.fxml");
                addTab("Отпуска", "/com/profservice/view/vacation/vacation_list.fxml");
                addTab("Больничные", "/com/profservice/view/sickleave/sickleave_list.fxml");
                addTab("Перемещения", "/com/profservice/view/movement/movement_list.fxml");
            } else {
                addTab("Мои данные", "/com/profservice/view/employee/employee_list.fxml");
            }

            if (currentUser.isAdmin()) {
                addTab("Отчеты", "/com/profservice/view/report/employee_report.fxml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTab(String title, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent content = loader.load();

        // Передаем текущего пользователя в контроллер
        Object controller = loader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).setCurrentUser(currentUser);
        }

        Tab tab = new Tab(title);
        tab.setContent(content);
        tabPane.getTabs().add(tab);
    }
}
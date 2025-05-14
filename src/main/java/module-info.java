module com.profservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Открываем все необходимые пакеты
    opens com.profservice to javafx.fxml;
    opens com.profservice.controller to javafx.fxml;
    opens com.profservice.view.auth to javafx.fxml;
    opens com.profservice.model to javafx.fxml;
    opens com.profservice.service to javafx.fxml;

    // Экспортируем пакеты, которые должны быть доступны
    exports com.profservice;
    exports com.profservice.controller;
    exports com.profservice.model;
    exports com.profservice.service;
}
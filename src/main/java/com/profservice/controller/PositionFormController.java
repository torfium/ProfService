package com.profservice.controller;

import com.profservice.model.Position;
import com.profservice.service.PositionService;
import com.profservice.util.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class PositionFormController {
    @FXML private TextField nameField;
    @FXML private TextField salaryField;
    @FXML private TextField bonusField;
    @FXML private TextArea descriptionArea;
    @FXML private Button saveButton;

    private Position position;
    private ObservableList<Position> positionList;

    public void setPosition(Position position) {
        this.position = position;
        if (position != null) {
            fillForm();
        }
    }

    public void setPositionList(ObservableList<Position> positionList) {
        this.positionList = positionList;
    }

    private void fillForm() {
        nameField.setText(position.getPositionName());
        salaryField.setText(String.valueOf(position.getBaseSalary()));
        bonusField.setText(String.valueOf(position.getBonusPercentage()));
        descriptionArea.setText(position.getDescription());
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Position updatedPosition = collectFormData();
            if (position == null) {
                PositionService.addPosition(updatedPosition);
                positionList.add(updatedPosition);
            } else {
                PositionService.updatePosition(updatedPosition);
                positionList.set(positionList.indexOf(position), updatedPosition);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные должности");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || salaryField.getText().isEmpty()) {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните обязательные поля (Название и Зарплата)");
            return false;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText());
            if (salary <= 0) {
                AlertUtil.showWarning("Предупреждение", "Зарплата должна быть положительным числом");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Предупреждение", "Некорректное значение зарплаты");
            return false;
        }

        if (!bonusField.getText().isEmpty()) {
            try {
                double bonus = Double.parseDouble(bonusField.getText());
                if (bonus < 0 || bonus > 100) {
                    AlertUtil.showWarning("Предупреждение", "Бонус должен быть от 0 до 100%");
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showWarning("Предупреждение", "Некорректное значение бонуса");
                return false;
            }
        }

        return true;
    }

    private Position collectFormData() {
        Position pos = position != null ? position : new Position();
        pos.setPositionName(nameField.getText());
        pos.setBaseSalary(Double.parseDouble(salaryField.getText()));
        pos.setBonusPercentage(bonusField.getText().isEmpty() ? 0 : Double.parseDouble(bonusField.getText()));
        pos.setDescription(descriptionArea.getText().isEmpty() ? null : descriptionArea.getText());
        return pos;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
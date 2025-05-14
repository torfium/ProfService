package com.profservice.controller;

import com.profservice.model.Movement;
import com.profservice.service.DepartmentService;
import com.profservice.service.EmployeeService;
import com.profservice.service.MovementService;
import com.profservice.service.PositionService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class MovementFormController {
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> previousDepartmentComboBox;
    @FXML private ComboBox<String> newDepartmentComboBox;
    @FXML private ComboBox<String> previousPositionComboBox;
    @FXML private ComboBox<String> newPositionComboBox;
    @FXML private TextField salaryChangeField;
    @FXML private TextField reasonField;
    @FXML private ComboBox<String> responsibleComboBox;
    @FXML private Button saveButton;

    private Movement movement;
    private ObservableList<Movement> movementList;
    private ObservableList<String> employeeNames;
    private ObservableList<String> departmentNames;
    private ObservableList<String> positionNames;
    private ObservableList<String> responsibleNames;

    public void setMovement(Movement movement) {
        this.movement = movement;
        if (movement != null) {
            fillForm();
        }
    }

    public void setMovementList(ObservableList<Movement> movementList) {
        this.movementList = movementList;
    }

    public void setEmployeeNames(ObservableList<String> employeeNames) {
        this.employeeNames = employeeNames;
        employeeComboBox.setItems(employeeNames);
    }

    public void setDepartmentNames(ObservableList<String> departmentNames) {
        this.departmentNames = departmentNames;
        previousDepartmentComboBox.setItems(departmentNames);
        newDepartmentComboBox.setItems(departmentNames);
    }

    public void setPositionNames(ObservableList<String> positionNames) {
        this.positionNames = positionNames;
        previousPositionComboBox.setItems(positionNames);
        newPositionComboBox.setItems(positionNames);
    }

    public void setResponsibleNames(ObservableList<String> responsibleNames) {
        this.responsibleNames = responsibleNames;
        responsibleComboBox.setItems(responsibleNames);
    }

    @FXML
    private void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Прием", "Увольнение", "Перевод", "Повышение", "Понижение"
        ));
    }

    private void fillForm() {
        employeeComboBox.setValue(movement.getEmployeeName());
        typeComboBox.setValue(movement.getMovementType());
        datePicker.setValue(movement.getMovementDate());
        if (movement.getPreviousDepartmentName() != null) {
            previousDepartmentComboBox.setValue(movement.getPreviousDepartmentName());
        }
        if (movement.getNewDepartmentName() != null) {
            newDepartmentComboBox.setValue(movement.getNewDepartmentName());
        }
        if (movement.getPreviousPositionName() != null) {
            previousPositionComboBox.setValue(movement.getPreviousPositionName());
        }
        if (movement.getNewPositionName() != null) {
            newPositionComboBox.setValue(movement.getNewPositionName());
        }
        if (movement.getSalaryChange() != null) {
            salaryChangeField.setText(String.valueOf(movement.getSalaryChange()));
        }
        reasonField.setText(movement.getReason());
        responsibleComboBox.setValue(movement.getResponsibleName());
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Movement updatedMovement = collectFormData();
            if (movement == null) {
                MovementService.addMovement(updatedMovement);
                movementList.add(updatedMovement);
            } else {
                MovementService.updateMovement(updatedMovement);
                movementList.set(movementList.indexOf(movement), updatedMovement);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные о перемещении");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (employeeComboBox.getValue() == null || typeComboBox.getValue() == null ||
                datePicker.getValue() == null || responsibleComboBox.getValue() == null) {

            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните все обязательные поля");
            return false;
        }

        if (!salaryChangeField.getText().isEmpty()) {
            try {
                Double.parseDouble(salaryChangeField.getText());
            } catch (NumberFormatException e) {
                AlertUtil.showWarning("Предупреждение", "Некорректное значение изменения зарплаты");
                return false;
            }
        }

        return true;
    }

    private Movement collectFormData() throws SQLException {
        Movement mov = movement != null ? movement : new Movement();
        mov.setEmployeeId(EmployeeService.getEmployeeIdByName(employeeComboBox.getValue()));
        mov.setEmployeeName(employeeComboBox.getValue());
        mov.setMovementType(typeComboBox.getValue());
        mov.setMovementDate(datePicker.getValue());

        if (previousDepartmentComboBox.getValue() != null) {
            mov.setPreviousDepartmentId(DepartmentService.getDepartmentIdByName(previousDepartmentComboBox.getValue()));
            mov.setPreviousDepartmentName(previousDepartmentComboBox.getValue());
        }

        if (newDepartmentComboBox.getValue() != null) {
            mov.setNewDepartmentId(DepartmentService.getDepartmentIdByName(newDepartmentComboBox.getValue()));
            mov.setNewDepartmentName(newDepartmentComboBox.getValue());
        }

        if (previousPositionComboBox.getValue() != null) {
            mov.setPreviousPositionId(PositionService.getPositionIdByName(previousPositionComboBox.getValue()));
            mov.setPreviousPositionName(previousPositionComboBox.getValue());
        }

        if (newPositionComboBox.getValue() != null) {
            mov.setNewPositionId(PositionService.getPositionIdByName(newPositionComboBox.getValue()));
            mov.setNewPositionName(newPositionComboBox.getValue());
        }

        mov.setSalaryChange(salaryChangeField.getText().isEmpty() ? null :
                Double.parseDouble(salaryChangeField.getText()));
        mov.setReason(reasonField.getText());
        mov.setResponsibleId(EmployeeService.getEmployeeIdByName(responsibleComboBox.getValue()));
        mov.setResponsibleName(responsibleComboBox.getValue());

        return mov;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
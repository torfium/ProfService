package com.profservice.controller;

import com.profservice.model.Hiring;
import com.profservice.service.EmployeeService;
import com.profservice.service.HiringService;
import com.profservice.util.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class HiringFormController {
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private DatePicker hireDatePicker;
    @FXML private TextField contractNumberField;
    @FXML private TextField salaryField;
    @FXML private TextField probationField;
    @FXML private ComboBox<String> hrManagerComboBox;
    @FXML private Button saveButton;

    private Hiring hiring;
    private ObservableList<Hiring> hiringList;
    private ObservableList<String> employeeNames;
    private ObservableList<String> hrManagerNames;

    public void setHiring(Hiring hiring) {
        this.hiring = hiring;
        if (hiring != null) {
            fillForm();
        }
    }

    public void setHiringList(ObservableList<Hiring> hiringList) {
        this.hiringList = hiringList;
    }

    public void setEmployeeNames(ObservableList<String> employeeNames) {
        this.employeeNames = employeeNames;
        employeeComboBox.setItems(employeeNames);
    }

    public void setHrManagerNames(ObservableList<String> hrManagerNames) {
        this.hrManagerNames = hrManagerNames;
        hrManagerComboBox.setItems(hrManagerNames);
    }

    private void fillForm() {
        employeeComboBox.setValue(hiring.getEmployeeName());
        hireDatePicker.setValue(hiring.getHireDate());
        contractNumberField.setText(hiring.getContractNumber());
        salaryField.setText(String.valueOf(hiring.getSalary()));
        probationField.setText(hiring.getProbationPeriod() != null ?
                String.valueOf(hiring.getProbationPeriod()) : "");
        hrManagerComboBox.setValue(hiring.getHrManagerName());
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Hiring updatedHiring = collectFormData();
            if (hiring == null) {
                HiringService.addHiring(updatedHiring);
                hiringList.add(updatedHiring);
            } else {
                HiringService.updateHiring(updatedHiring);
                hiringList.set(hiringList.indexOf(hiring), updatedHiring);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные о приеме на работу");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (employeeComboBox.getValue() == null || hireDatePicker.getValue() == null ||
                contractNumberField.getText().isEmpty() || salaryField.getText().isEmpty() ||
                hrManagerComboBox.getValue() == null) {

            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните все обязательные поля");
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

        if (!probationField.getText().isEmpty()) {
            try {
                int probation = Integer.parseInt(probationField.getText());
                if (probation < 0) {
                    AlertUtil.showWarning("Предупреждение", "Испытательный срок не может быть отрицательным");
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showWarning("Предупреждение", "Некорректное значение испытательного срока");
                return false;
            }
        }

        return true;
    }

    private Hiring collectFormData() throws SQLException {
        Hiring hire = hiring != null ? hiring : new Hiring();
        hire.setEmployeeId(EmployeeService.getEmployeeIdByName(employeeComboBox.getValue()));
        hire.setEmployeeName(employeeComboBox.getValue());
        hire.setHireDate(hireDatePicker.getValue());
        hire.setContractNumber(contractNumberField.getText());
        hire.setSalary(Double.parseDouble(salaryField.getText()));
        hire.setProbationPeriod(probationField.getText().isEmpty() ? null :
                Integer.parseInt(probationField.getText()));
        hire.setHrManagerId(EmployeeService.getEmployeeIdByName(hrManagerComboBox.getValue()));
        hire.setHrManagerName(hrManagerComboBox.getValue());
        return hire;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
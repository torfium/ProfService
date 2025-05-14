package com.profservice.controller;

import com.profservice.model.SickLeave;
import com.profservice.service.EmployeeService;
import com.profservice.service.SickLeaveService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class SickLeaveFormController {
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField sickLeaveNumberField;
    @FXML private TextField diagnosisField;
    @FXML private TextField paymentPercentageField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button saveButton;

    private SickLeave sickLeave;
    private ObservableList<SickLeave> sickLeaveList;
    private ObservableList<String> employeeNames;

    public void setSickLeave(SickLeave sickLeave) {
        this.sickLeave = sickLeave;
        if (sickLeave != null) {
            fillForm();
        }
    }

    public void setSickLeaveList(ObservableList<SickLeave> sickLeaveList) {
        this.sickLeaveList = sickLeaveList;
    }

    public void setEmployeeNames(ObservableList<String> employeeNames) {
        this.employeeNames = employeeNames;
        employeeComboBox.setItems(employeeNames);
    }

    @FXML
    private void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Открыт", "Закрыт", "Продлен"
        ));
    }

    private void fillForm() {
        employeeComboBox.setValue(sickLeave.getEmployeeName());
        startDatePicker.setValue(sickLeave.getStartDate());
        endDatePicker.setValue(sickLeave.getEndDate());
        sickLeaveNumberField.setText(sickLeave.getSickLeaveNumber());
        diagnosisField.setText(sickLeave.getDiagnosis());
        paymentPercentageField.setText(String.valueOf(sickLeave.getPaymentPercentage()));
        statusComboBox.setValue(sickLeave.getStatus());
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            SickLeave updatedSickLeave = collectFormData();
            if (sickLeave == null) {
                SickLeaveService.addSickLeave(updatedSickLeave);
                sickLeaveList.add(updatedSickLeave);
            } else {
                SickLeaveService.updateSickLeave(updatedSickLeave);
                sickLeaveList.set(sickLeaveList.indexOf(sickLeave), updatedSickLeave);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные о больничном");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (employeeComboBox.getValue() == null || startDatePicker.getValue() == null ||
                endDatePicker.getValue() == null || sickLeaveNumberField.getText().isEmpty() ||
                paymentPercentageField.getText().isEmpty() || statusComboBox.getValue() == null) {

            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните все обязательные поля");
            return false;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            AlertUtil.showWarning("Предупреждение", "Дата окончания не может быть раньше даты начала");
            return false;
        }

        try {
            int percentage = Integer.parseInt(paymentPercentageField.getText());
            if (percentage < 0 || percentage > 100) {
                AlertUtil.showWarning("Предупреждение", "Процент оплаты должен быть от 0 до 100");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Предупреждение", "Некорректное значение процента оплаты");
            return false;
        }

        return true;
    }

    private SickLeave collectFormData() throws SQLException {
        SickLeave sl = sickLeave != null ? sickLeave : new SickLeave();
        sl.setEmployeeId(EmployeeService.getEmployeeIdByName(employeeComboBox.getValue()));
        sl.setEmployeeName(employeeComboBox.getValue());
        sl.setStartDate(startDatePicker.getValue());
        sl.setEndDate(endDatePicker.getValue());
        sl.setSickLeaveNumber(sickLeaveNumberField.getText());
        sl.setDiagnosis(diagnosisField.getText());
        sl.setPaymentPercentage(Integer.parseInt(paymentPercentageField.getText()));
        sl.setStatus(statusComboBox.getValue());
        return sl;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
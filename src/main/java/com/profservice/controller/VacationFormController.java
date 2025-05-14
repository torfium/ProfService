package com.profservice.controller;

import com.profservice.model.Vacation;
import com.profservice.service.EmployeeService;
import com.profservice.service.VacationService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class VacationFormController {
    @FXML private ComboBox<String> employeeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField daysCountField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> approverComboBox;
    @FXML private Button saveButton;

    private Vacation vacation;
    private ObservableList<Vacation> vacationList;
    private ObservableList<String> employeeNames;
    private ObservableList<String> approverNames;

    public void setVacation(Vacation vacation) {
        this.vacation = vacation;
        if (vacation != null) {
            fillForm();
        }
    }

    public void setVacationList(ObservableList<Vacation> vacationList) {
        this.vacationList = vacationList;
    }

    public void setEmployeeNames(ObservableList<String> employeeNames) {
        this.employeeNames = employeeNames;
        employeeComboBox.setItems(employeeNames);
    }

    public void setApproverNames(ObservableList<String> approverNames) {
        this.approverNames = approverNames;
        approverComboBox.setItems(approverNames);
    }

    @FXML
    private void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Очередной", "Дополнительный", "Без сохранения ЗП", "Учебный"
        ));
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Запланирован", "Использован", "Отменен"
        ));
    }

    private void fillForm() {
        employeeComboBox.setValue(vacation.getEmployeeName());
        startDatePicker.setValue(vacation.getStartDate());
        endDatePicker.setValue(vacation.getEndDate());
        typeComboBox.setValue(vacation.getVacationType());
        daysCountField.setText(String.valueOf(vacation.getDaysCount()));
        statusComboBox.setValue(vacation.getStatus());
        if (vacation.getApprovedByName() != null) {
            approverComboBox.setValue(vacation.getApprovedByName());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Vacation updatedVacation = collectFormData();
            if (vacation == null) {
                VacationService.addVacation(updatedVacation);
                vacationList.add(updatedVacation);
            } else {
                VacationService.updateVacation(updatedVacation);
                vacationList.set(vacationList.indexOf(vacation), updatedVacation);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные об отпуске");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (employeeComboBox.getValue() == null || startDatePicker.getValue() == null ||
                endDatePicker.getValue() == null || typeComboBox.getValue() == null ||
                daysCountField.getText().isEmpty() || statusComboBox.getValue() == null) {

            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните все обязательные поля");
            return false;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            AlertUtil.showWarning("Предупреждение", "Дата окончания не может быть раньше даты начала");
            return false;
        }

        try {
            int days = Integer.parseInt(daysCountField.getText());
            if (days <= 0) {
                AlertUtil.showWarning("Предупреждение", "Количество дней должно быть положительным числом");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Предупреждение", "Некорректное значение количества дней");
            return false;
        }

        return true;
    }

    private Vacation collectFormData() throws SQLException {
        Vacation vac = vacation != null ? vacation : new Vacation();
        vac.setEmployeeId(EmployeeService.getEmployeeIdByName(employeeComboBox.getValue()));
        vac.setEmployeeName(employeeComboBox.getValue());
        vac.setStartDate(startDatePicker.getValue());
        vac.setEndDate(endDatePicker.getValue());
        vac.setVacationType(typeComboBox.getValue());
        vac.setDaysCount(Integer.parseInt(daysCountField.getText()));
        vac.setStatus(statusComboBox.getValue());
        if (approverComboBox.getValue() != null) {
            vac.setApprovedBy(EmployeeService.getEmployeeIdByName(approverComboBox.getValue()));
            vac.setApprovedByName(approverComboBox.getValue());
        }
        return vac;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
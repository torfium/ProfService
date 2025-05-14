package com.profservice.controller;

import com.profservice.model.Employee;
import com.profservice.service.DepartmentService;
import com.profservice.service.EmployeeService;
import com.profservice.service.PositionService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EmployeeFormController {
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField passportSeriesField;
    @FXML private TextField passportNumberField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker hireDatePicker;
    @FXML private DatePicker dismissalDatePicker;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button saveButton;

    private Employee employee;
    private ObservableList<Employee> employeeList;

    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            fillForm();
        }
    }

    public void setEmployeeList(ObservableList<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @FXML
    private void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList("М", "Ж"));
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Работает", "Уволен", "Отпуск", "Больничный"
        ));

        try {
            // Преобразуем List в ObservableList
            ObservableList<String> positionNames = FXCollections.observableArrayList(
                    PositionService.getAllPositionNames()
            );
            positionComboBox.setItems(positionNames);

            ObservableList<String> departmentNames = FXCollections.observableArrayList(
                    DepartmentService.getAllDepartmentNames()
            );
            departmentComboBox.setItems(departmentNames);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка загрузки данных", "Не удалось загрузить список должностей или отделов");
            e.printStackTrace();
        }
    }

    private void fillForm() {
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        middleNameField.setText(employee.getMiddleName());
        birthDatePicker.setValue(employee.getBirthDate());
        genderComboBox.setValue(employee.getGender());
        passportSeriesField.setText(employee.getPassportSeries());
        passportNumberField.setText(employee.getPassportNumber());
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhone());
        emailField.setText(employee.getEmail());
        hireDatePicker.setValue(employee.getHireDate());
        dismissalDatePicker.setValue(employee.getDismissalDate());
        positionComboBox.setValue(employee.getPositionName());
        departmentComboBox.setValue(employee.getDepartmentName());
        statusComboBox.setValue(employee.getStatus());
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Employee updatedEmployee = collectFormData();
            if (employee == null) {
                EmployeeService.addEmployee(updatedEmployee);
                employeeList.add(updatedEmployee);
            } else {
                EmployeeService.updateEmployee(updatedEmployee);
                employeeList.set(employeeList.indexOf(employee), updatedEmployee);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные сотрудника");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                birthDatePicker.getValue() == null || genderComboBox.getValue() == null ||
                passportSeriesField.getText().isEmpty() || passportNumberField.getText().isEmpty() ||
                addressField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                hireDatePicker.getValue() == null || statusComboBox.getValue() == null) {

            AlertUtil.showWarning("Предупреждение", "Пожалуйста, заполните все обязательные поля");
            return false;
        }

        if (hireDatePicker.getValue().isBefore(birthDatePicker.getValue().plusYears(14))) {
            AlertUtil.showWarning("Предупреждение", "Дата приема на работу не может быть раньше даты рождения + 14 лет");
            return false;
        }

        if (dismissalDatePicker.getValue() != null &&
                dismissalDatePicker.getValue().isBefore(hireDatePicker.getValue())) {

            AlertUtil.showWarning("Предупреждение", "Дата увольнения не может быть раньше даты приема");
            return false;
        }

        return true;
    }

    private Employee collectFormData() throws SQLException {
        Employee emp = employee != null ? employee : new Employee();
        emp.setLastName(lastNameField.getText());
        emp.setFirstName(firstNameField.getText());
        emp.setMiddleName(middleNameField.getText().isEmpty() ? null : middleNameField.getText());
        emp.setBirthDate(birthDatePicker.getValue());
        emp.setGender(genderComboBox.getValue());
        emp.setPassportSeries(passportSeriesField.getText());
        emp.setPassportNumber(passportNumberField.getText());
        emp.setAddress(addressField.getText());
        emp.setPhone(phoneField.getText());
        emp.setEmail(emailField.getText().isEmpty() ? null : emailField.getText());
        emp.setHireDate(hireDatePicker.getValue());
        emp.setDismissalDate(dismissalDatePicker.getValue());
        emp.setStatus(statusComboBox.getValue());

        if (positionComboBox.getValue() != null) {
            emp.setPositionId(PositionService.getPositionIdByName(positionComboBox.getValue()));
            emp.setPositionName(positionComboBox.getValue());
        }

        if (departmentComboBox.getValue() != null) {
            emp.setDepartmentId(DepartmentService.getDepartmentIdByName(departmentComboBox.getValue()));
            emp.setDepartmentName(departmentComboBox.getValue());
        }

        return emp;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
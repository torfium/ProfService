package com.profservice.controller;

import com.profservice.model.Department;
import com.profservice.service.DepartmentService;
import com.profservice.service.EmployeeService;
import com.profservice.util.AlertUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DepartmentFormController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> managerComboBox;
    @FXML private TextField locationField;
    @FXML private Button saveButton;

    private Department department;
    private ObservableList<Department> departmentList;
    private ObservableList<String> employeeNames;

    public void setDepartment(Department department) {
        this.department = department;
        if (department != null) {
            fillForm();
        }
    }

    public void setDepartmentList(ObservableList<Department> departmentList) {
        this.departmentList = departmentList;
    }

    public void setEmployeeNames(ObservableList<String> employeeNames) {
        this.employeeNames = employeeNames;
        managerComboBox.setItems(employeeNames);
    }

    private void fillForm() {
        nameField.setText(department.getDepartmentName());
        locationField.setText(department.getLocation());
        if (department.getManagerName() != null) {
            managerComboBox.setValue(department.getManagerName());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Department updatedDepartment = collectFormData();
            if (department == null) {
                DepartmentService.addDepartment(updatedDepartment);
                departmentList.add(updatedDepartment);
            } else {
                DepartmentService.updateDepartment(updatedDepartment);
                departmentList.set(departmentList.indexOf(department), updatedDepartment);
            }

            closeForm();
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка сохранения", "Не удалось сохранить данные отдела");
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty()) {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, укажите название отдела");
            return false;
        }
        return true;
    }

    private Department collectFormData() throws SQLException {
        Department dept = department != null ? department : new Department();
        dept.setDepartmentName(nameField.getText());
        dept.setLocation(locationField.getText().isEmpty() ? null : locationField.getText());

        if (managerComboBox.getValue() != null && !managerComboBox.getValue().isEmpty()) {
            dept.setManagerId(EmployeeService.getEmployeeIdByName(managerComboBox.getValue()));
            dept.setManagerName(managerComboBox.getValue());
        } else {
            dept.setManagerId(null);
            dept.setManagerName(null);
        }

        return dept;
    }

    private void closeForm() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
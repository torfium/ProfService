package com.profservice.controller;

import com.profservice.model.Employee;
import com.profservice.service.EmployeeService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class EmployeeController extends BaseController {
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> lastNameColumn;
    @FXML private TableColumn<Employee, String> firstNameColumn;
    @FXML private TableColumn<Employee, String> middleNameColumn;
    @FXML private TableColumn<Employee, String> genderColumn;
    @FXML private TableColumn<Employee, String> passportSeriesColumn;
    @FXML private TableColumn<Employee, String> passportNumberColumn;
    @FXML private TableColumn<Employee, String> addressColumn;
    @FXML private TableColumn<Employee, String> phoneColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private TableColumn<Employee, LocalDate> hireDateColumn;
    @FXML private TableColumn<Employee, LocalDate> dismissalDateColumn;
    @FXML private TableColumn<Employee, LocalDate> birthDateColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, String> departmentColumn;
    @FXML private TableColumn<Employee, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeComboBox;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            if (currentUser.isAdmin() || currentUser.isManager()) {
                employeeList.setAll(EmployeeService.getAllEmployees());
            } else {
                employeeList.setAll(EmployeeService.getEmployeeById(currentUser.getEmployeeId()));
            }

            idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
            birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
            genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
            passportSeriesColumn.setCellValueFactory(new PropertyValueFactory<>("passportSeries"));
            passportNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
            dismissalDateColumn.setCellValueFactory(new PropertyValueFactory<>("dismissalDate"));
            positionColumn.setCellValueFactory(new PropertyValueFactory<>("positionName"));
            departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            employeeTable.setItems(employeeList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные сотрудников");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEmployee() {
        showEmployeeForm(null);
    }

    @FXML
    private void handleEditEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEmployeeForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите сотрудника для редактирования");
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление сотрудника");
            alert.setContentText("Вы уверены, что хотите удалить сотрудника " + selected.getFullName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    EmployeeService.deleteEmployee(selected.getEmployeeId());
                    employeeList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить сотрудника");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите сотрудника для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            employeeTable.setItems(employeeList);
            return;
        }

        ObservableList<Employee> filteredList = FXCollections.observableArrayList();
        for (Employee employee : employeeList) {
            if (employee.getLastName().toLowerCase().contains(searchText) ||
                    employee.getFirstName().toLowerCase().contains(searchText) ||
                    employee.getGender().toLowerCase().contains(searchText) ||
                    employee.getPassportSeries().toLowerCase().contains(searchText) ||
                    employee.getPassportNumber().toLowerCase().contains(searchText) ||
                    employee.getAddress().toLowerCase().contains(searchText) ||
                    employee.getPhone().toLowerCase().contains(searchText) ||
                    (employee.getEmail() != null && employee.getEmail().toLowerCase().contains(searchText)) ||
                    (employee.getPositionName() != null && employee.getPositionName().toLowerCase().contains(searchText)) ||
                    (employee.getDepartmentName() != null && employee.getDepartmentName().toLowerCase().contains(searchText)) ||
                    employee.getStatus().toLowerCase().contains(searchText)) {
                filteredList.add(employee);
            }
        }
        employeeTable.setItems(filteredList);
    }

    private void showEmployeeForm(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/employee/employee_form.fxml"));
            Parent root = loader.load();

            EmployeeFormController controller = loader.getController();
            controller.setEmployee(employee);
            controller.setEmployeeList(employeeList);

            Stage stage = new Stage();
            stage.setTitle(employee == null ? "Добавление сотрудника" : "Редактирование сотрудника");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
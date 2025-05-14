package com.profservice.controller;

import com.profservice.model.Department;
import com.profservice.service.DepartmentService;
import com.profservice.service.EmployeeService;
import com.profservice.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class DepartmentController extends BaseController {
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Integer> idColumn;
    @FXML private TableColumn<Department, String> nameColumn;
    @FXML private TableColumn<Department, String> managerColumn;
    @FXML private TableColumn<Department, String> locationColumn;
    @FXML private TextField searchField;

    private ObservableList<Department> departmentList = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            departmentList.setAll(DepartmentService.getAllDepartments());
            employeeNames.setAll(EmployeeService.getAllEmployeeNames());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
            managerColumn.setCellValueFactory(new PropertyValueFactory<>("managerName"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

            departmentTable.setItems(departmentList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные отделов");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDepartment() {
        showDepartmentForm(null);
    }

    @FXML
    private void handleEditDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDepartmentForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите отдел для редактирования");
        }
    }

    @FXML
    private void handleDeleteDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление отдела",
                    "Вы уверены, что хотите удалить отдел " + selected.getDepartmentName() + "?")) {

                try {
                    DepartmentService.deleteDepartment(selected.getDepartmentId());
                    departmentList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить отдел");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите отдел для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            departmentTable.setItems(departmentList);
            return;
        }

        ObservableList<Department> filteredList = FXCollections.observableArrayList();
        for (Department department : departmentList) {
            if (department.getDepartmentName().toLowerCase().contains(searchText) ||
                    (department.getLocation() != null && department.getLocation().toLowerCase().contains(searchText)) ||
                    (department.getManagerName() != null && department.getManagerName().toLowerCase().contains(searchText))) {
                filteredList.add(department);
            }
        }
        departmentTable.setItems(filteredList);
    }

    private void showDepartmentForm(Department department) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/department/department_form.fxml"));
            Parent root = loader.load();

            DepartmentFormController controller = loader.getController();
            controller.setDepartment(department);
            controller.setDepartmentList(departmentList);
            controller.setEmployeeNames(employeeNames);

            Stage stage = new Stage();
            stage.setTitle(department == null ? "Добавление отдела" : "Редактирование отдела");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
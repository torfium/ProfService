package com.profservice.controller;

import com.profservice.model.Movement;
import com.profservice.model.User;
import com.profservice.service.DepartmentService;
import com.profservice.service.EmployeeService;
import com.profservice.service.MovementService;
import com.profservice.service.PositionService;
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

public class MovementController extends BaseController {
    @FXML private TableView<Movement> movementTable;
    @FXML private TableColumn<Movement, Integer> idColumn;
    @FXML private TableColumn<Movement, String> employeeColumn;
    @FXML private TableColumn<Movement, String> typeColumn;
    @FXML private TableColumn<Movement, LocalDate> dateColumn;
    @FXML private TableColumn<Movement, String> newDepartmentColumn;
    @FXML private TableColumn<Movement, String> newPositionColumn;
    @FXML private TextField searchField;

    private ObservableList<Movement> movementList = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();
    private ObservableList<String> departmentNames = FXCollections.observableArrayList();
    private ObservableList<String> positionNames = FXCollections.observableArrayList();
    private ObservableList<String> responsibleNames = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            movementList.setAll(MovementService.getAllMovements());
            employeeNames.setAll(EmployeeService.getAllEmployeeNames());
            departmentNames.setAll(DepartmentService.getAllDepartmentNames());
            positionNames.setAll(PositionService.getAllPositionNames());
            responsibleNames.setAll(EmployeeService.getAllEmployeeNames());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("movementId"));
            employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("movementType"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("movementDate"));
            newDepartmentColumn.setCellValueFactory(new PropertyValueFactory<>("newDepartmentName"));
            newPositionColumn.setCellValueFactory(new PropertyValueFactory<>("newPositionName"));

            movementTable.setItems(movementList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные о перемещениях");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddMovement() {
        showMovementForm(null);
    }

    @FXML
    private void handleEditMovement() {
        Movement selected = movementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showMovementForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите перемещение для редактирования");
        }
    }

    @FXML
    private void handleDeleteMovement() {
        Movement selected = movementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление перемещения",
                    "Вы уверены, что хотите удалить запись о перемещении сотрудника " + selected.getEmployeeName() + "?")) {

                try {
                    MovementService.deleteMovement(selected.getMovementId());
                    movementList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить запись о перемещении");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите перемещение для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            movementTable.setItems(movementList);
            return;
        }

        ObservableList<Movement> filteredList = FXCollections.observableArrayList();
        for (Movement movement : movementList) {
            if (movement.getEmployeeName().toLowerCase().contains(searchText) ||
                    movement.getMovementType().toLowerCase().contains(searchText) ||
                    (movement.getNewDepartmentName() != null && movement.getNewDepartmentName().toLowerCase().contains(searchText)) ||
                    (movement.getNewPositionName() != null && movement.getNewPositionName().toLowerCase().contains(searchText))) {
                filteredList.add(movement);
            }
        }
        movementTable.setItems(filteredList);
    }

    private void showMovementForm(Movement movement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/movement/movement_form.fxml"));
            Parent root = loader.load();

            MovementFormController controller = loader.getController();
            controller.setMovement(movement);
            controller.setMovementList(movementList);
            controller.setEmployeeNames(employeeNames);
            controller.setDepartmentNames(departmentNames);
            controller.setPositionNames(positionNames);
            controller.setResponsibleNames(responsibleNames);

            Stage stage = new Stage();
            stage.setTitle(movement == null ? "Добавление перемещения" : "Редактирование перемещения");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
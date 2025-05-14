package com.profservice.controller;

import com.profservice.model.SickLeave;
import com.profservice.model.User;
import com.profservice.service.EmployeeService;
import com.profservice.service.SickLeaveService;
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

public class SickLeaveController extends BaseController {
    @FXML private TableView<SickLeave> sickLeaveTable;
    @FXML private TableColumn<SickLeave, Integer> idColumn;
    @FXML private TableColumn<SickLeave, String> employeeColumn;
    @FXML private TableColumn<SickLeave, LocalDate> startDateColumn;
    @FXML private TableColumn<SickLeave, LocalDate> endDateColumn;
    @FXML private TableColumn<SickLeave, String> diagnosisColumn;
    @FXML private TableColumn<SickLeave, Integer> paymentPercentageColumn;
    @FXML private TableColumn<SickLeave, String> statusColumn;
    @FXML private TextField searchField;

    private ObservableList<SickLeave> sickLeaveList = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            sickLeaveList.setAll(SickLeaveService.getAllSickLeaves());
            employeeNames.setAll(EmployeeService.getAllEmployeeNames());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("sickLeaveId"));
            employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
            diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("paymentPercentage"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            sickLeaveTable.setItems(sickLeaveList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные о больничных");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddSickLeave() {
        showSickLeaveForm(null);
    }

    @FXML
    private void handleEditSickLeave() {
        SickLeave selected = sickLeaveTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showSickLeaveForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите больничный для редактирования");
        }
    }

    @FXML
    private void handleDeleteSickLeave() {
        SickLeave selected = sickLeaveTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление больничного",
                    "Вы уверены, что хотите удалить больничный сотрудника " + selected.getEmployeeName() + "?")) {

                try {
                    SickLeaveService.deleteSickLeave(selected.getSickLeaveId());
                    sickLeaveList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить запись о больничном");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите больничный для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            sickLeaveTable.setItems(sickLeaveList);
            return;
        }

        ObservableList<SickLeave> filteredList = FXCollections.observableArrayList();
        for (SickLeave sickLeave : sickLeaveList) {
            if (sickLeave.getEmployeeName().toLowerCase().contains(searchText) ||
                    (sickLeave.getDiagnosis() != null && sickLeave.getDiagnosis().toLowerCase().contains(searchText)) ||
                    sickLeave.getStatus().toLowerCase().contains(searchText)) {
                filteredList.add(sickLeave);
            }
        }
        sickLeaveTable.setItems(filteredList);
    }

    private void showSickLeaveForm(SickLeave sickLeave) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/sickleave/sickleave_form.fxml"));
            Parent root = loader.load();

            SickLeaveFormController controller = loader.getController();
            controller.setSickLeave(sickLeave);
            controller.setSickLeaveList(sickLeaveList);
            controller.setEmployeeNames(employeeNames);

            Stage stage = new Stage();
            stage.setTitle(sickLeave == null ? "Добавление больничного" : "Редактирование больничного");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
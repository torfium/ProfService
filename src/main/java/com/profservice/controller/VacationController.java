package com.profservice.controller;

import com.profservice.model.Vacation;
import com.profservice.model.User;
import com.profservice.service.EmployeeService;
import com.profservice.service.VacationService;
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

public class VacationController extends BaseController {
    @FXML private TableView<Vacation> vacationTable;
    @FXML private TableColumn<Vacation, Integer> idColumn;
    @FXML private TableColumn<Vacation, String> employeeColumn;
    @FXML private TableColumn<Vacation, LocalDate> startDateColumn;
    @FXML private TableColumn<Vacation, LocalDate> endDateColumn;
    @FXML private TableColumn<Vacation, String> typeColumn;
    @FXML private TableColumn<Vacation, String> statusColumn;
    @FXML private TextField searchField;

    private ObservableList<Vacation> vacationList = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();
    private ObservableList<String> approverNames = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            vacationList.setAll(VacationService.getAllVacations());
            employeeNames.setAll(EmployeeService.getAllEmployeeNames());
            approverNames.setAll(EmployeeService.getAllEmployeeNames());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("vacationId"));
            employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("vacationType"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            vacationTable.setItems(vacationList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные об отпусках");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddVacation() {
        showVacationForm(null);
    }

    @FXML
    private void handleEditVacation() {
        Vacation selected = vacationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showVacationForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите отпуск для редактирования");
        }
    }

    @FXML
    private void handleDeleteVacation() {
        Vacation selected = vacationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление отпуска",
                    "Вы уверены, что хотите удалить отпуск сотрудника " + selected.getEmployeeName() + "?")) {

                try {
                    VacationService.deleteVacation(selected.getVacationId());
                    vacationList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить запись об отпуске");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите отпуск для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            vacationTable.setItems(vacationList);
            return;
        }

        ObservableList<Vacation> filteredList = FXCollections.observableArrayList();
        for (Vacation vacation : vacationList) {
            if (vacation.getEmployeeName().toLowerCase().contains(searchText) ||
                    vacation.getVacationType().toLowerCase().contains(searchText) ||
                    vacation.getStatus().toLowerCase().contains(searchText)) {
                filteredList.add(vacation);
            }
        }
        vacationTable.setItems(filteredList);
    }

    private void showVacationForm(Vacation vacation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/vacation/vacation_form.fxml"));
            Parent root = loader.load();

            VacationFormController controller = loader.getController();
            controller.setVacation(vacation);
            controller.setVacationList(vacationList);
            controller.setEmployeeNames(employeeNames);
            controller.setApproverNames(approverNames);

            Stage stage = new Stage();
            stage.setTitle(vacation == null ? "Добавление отпуска" : "Редактирование отпуска");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
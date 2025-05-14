package com.profservice.controller;

import com.profservice.model.Hiring;
import com.profservice.model.User;
import com.profservice.service.EmployeeService;
import com.profservice.service.HiringService;
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

public class HiringController extends BaseController {
    @FXML private TableView<Hiring> hiringTable;
    @FXML private TableColumn<Hiring, Integer> idColumn;
    @FXML private TableColumn<Hiring, String> employeeColumn;
    @FXML private TableColumn<Hiring, LocalDate> hireDateColumn;
    @FXML private TableColumn<Hiring, String> contractColumn;
    @FXML private TableColumn<Hiring, Double> salaryColumn;
    @FXML private TableColumn<Hiring, String> hrManagerColumn;
    @FXML private TextField searchField;

    private ObservableList<Hiring> hiringList = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();
    private ObservableList<String> hrManagerNames = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            hiringList.setAll(HiringService.getAllHirings());
            employeeNames.setAll(EmployeeService.getAllEmployeeNames());
            hrManagerNames.setAll(EmployeeService.getAllEmployeeNames());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("hiringId"));
            employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
            hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
            contractColumn.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
            salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
            hrManagerColumn.setCellValueFactory(new PropertyValueFactory<>("hrManagerName"));

            hiringTable.setItems(hiringList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные о приемах на работу");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddHiring() {
        showHiringForm(null);
    }

    @FXML
    private void handleEditHiring() {
        Hiring selected = hiringTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showHiringForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите запись о приеме для редактирования");
        }
    }

    @FXML
    private void handleDeleteHiring() {
        Hiring selected = hiringTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление записи о приеме",
                    "Вы уверены, что хотите удалить запись о приеме " + selected.getEmployeeName() + "?")) {

                try {
                    HiringService.deleteHiring(selected.getHiringId());
                    hiringList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить запись о приеме");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите запись о приеме для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            hiringTable.setItems(hiringList);
            return;
        }

        ObservableList<Hiring> filteredList = FXCollections.observableArrayList();
        for (Hiring hiring : hiringList) {
            if (hiring.getEmployeeName().toLowerCase().contains(searchText) ||
                    hiring.getContractNumber().toLowerCase().contains(searchText) ||
                    hiring.getHrManagerName().toLowerCase().contains(searchText)) {
                filteredList.add(hiring);
            }
        }
        hiringTable.setItems(filteredList);
    }

    private void showHiringForm(Hiring hiring) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/hiring/hiring_form.fxml"));
            Parent root = loader.load();

            HiringFormController controller = loader.getController();
            controller.setHiring(hiring);
            controller.setHiringList(hiringList);
            controller.setEmployeeNames(employeeNames);
            controller.setHrManagerNames(hrManagerNames);

            Stage stage = new Stage();
            stage.setTitle(hiring == null ? "Добавление приема на работу" : "Редактирование приема на работу");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
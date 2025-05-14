package com.profservice.controller;

import com.profservice.model.Position;
import com.profservice.service.PositionService;
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

public class PositionController extends BaseController {
    @FXML private TableView<Position> positionTable;
    @FXML private TableColumn<Position, Integer> idColumn;
    @FXML private TableColumn<Position, String> nameColumn;
    @FXML private TableColumn<Position, Double> salaryColumn;
    @FXML private TableColumn<Position, Double> bonusColumn;
    @FXML private TableColumn<Position, String> descriptionColumn;
    @FXML private TextField searchField;

    private ObservableList<Position> positionList = FXCollections.observableArrayList();

    @Override
    protected void initializeData() {
        try {
            positionList.setAll(PositionService.getAllPositions());

            idColumn.setCellValueFactory(new PropertyValueFactory<>("positionId"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("positionName"));
            salaryColumn.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
            bonusColumn.setCellValueFactory(new PropertyValueFactory<>("bonusPercentage"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

            positionTable.setItems(positionList);
        } catch (SQLException e) {
            AlertUtil.showError("Ошибка базы данных", "Не удалось загрузить данные должностей");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPosition() {
        showPositionForm(null);
    }

    @FXML
    private void handleEditPosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showPositionForm(selected);
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите должность для редактирования");
        }
    }

    @FXML
    private void handleDeletePosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (AlertUtil.showConfirmation("Подтверждение удаления",
                    "Удаление должности",
                    "Вы уверены, что хотите удалить должность " + selected.getPositionName() + "?")) {

                try {
                    PositionService.deletePosition(selected.getPositionId());
                    positionList.remove(selected);
                } catch (SQLException e) {
                    AlertUtil.showError("Ошибка удаления", "Не удалось удалить должность");
                    e.printStackTrace();
                }
            }
        } else {
            AlertUtil.showWarning("Предупреждение", "Пожалуйста, выберите должность для удаления");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            positionTable.setItems(positionList);
            return;
        }

        ObservableList<Position> filteredList = FXCollections.observableArrayList();
        for (Position position : positionList) {
            if (position.getPositionName().toLowerCase().contains(searchText) ||
                    (position.getDescription() != null && position.getDescription().toLowerCase().contains(searchText))) {
                filteredList.add(position);
            }
        }
        positionTable.setItems(filteredList);
    }

    private void showPositionForm(Position position) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/profservice/view/position/position_form.fxml"));
            Parent root = loader.load();

            PositionFormController controller = loader.getController();
            controller.setPosition(position);
            controller.setPositionList(positionList);

            Stage stage = new Stage();
            stage.setTitle(position == null ? "Добавление должности" : "Редактирование должности");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
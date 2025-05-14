package com.profservice.util;

import com.profservice.model.Employee;
import com.profservice.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    public static BarChart<String, Number> createDepartmentEmployeeCountChart() throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Количество сотрудников по отделам");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Отделы");

        Map<String, Integer> departmentCount = new HashMap<>();
        List<Employee> employees = EmployeeService.getAllEmployees();

        for (Employee emp : employees) {
            String dept = emp.getDepartmentName() != null ? emp.getDepartmentName() : "Не указан";
            departmentCount.put(dept, departmentCount.getOrDefault(dept, 0) + 1);
        }

        departmentCount.forEach((dept, count) ->
                series.getData().add(new XYChart.Data<>(dept, count)));

        barChart.getData().add(series);
        return barChart;
    }

    public static PieChart createPositionDistributionChart() throws SQLException {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Распределение по должностям");

        Map<String, Integer> positionCount = new HashMap<>();
        List<Employee> employees = EmployeeService.getAllEmployees();

        for (Employee emp : employees) {
            String position = emp.getPositionName() != null ? emp.getPositionName() : "Не указана";
            positionCount.put(position, positionCount.getOrDefault(position, 0) + 1);
        }

        positionCount.forEach((position, count) ->
                pieChart.getData().add(new PieChart.Data(position, count)));

        return pieChart;
    }

    public static TableView<Employee> createEmployeeStatusTable() throws SQLException {
        TableView<Employee> table = new TableView<>();
        ObservableList<Employee> employees = FXCollections.observableArrayList(EmployeeService.getAllEmployees());
        table.setItems(employees);
        return table;
    }
}
package com.profservice.controller;

import com.profservice.util.ReportGenerator;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class EmployeeReportController extends BaseController {
    @FXML private VBox reportContainer;

    @Override
    protected void initializeData() {
        try {
            BarChart<String, Number> barChart = ReportGenerator.createDepartmentEmployeeCountChart();
            PieChart pieChart = ReportGenerator.createPositionDistributionChart();

            reportContainer.getChildren().addAll(barChart, pieChart);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
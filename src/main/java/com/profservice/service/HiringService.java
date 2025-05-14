package com.profservice.service;

import com.profservice.model.Hiring;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HiringService {
    public static List<Hiring> getAllHirings() throws SQLException {
        List<Hiring> hirings = new ArrayList<>();
        String sql = "SELECT h.*, e1.last_name || ' ' || e1.first_name as employee_name, " +
                "e2.last_name || ' ' || e2.first_name as hr_manager_name " +
                "FROM hiring h " +
                "JOIN employees e1 ON h.employee_id = e1.employee_id " +
                "JOIN employees e2 ON h.hr_manager_id = e2.employee_id " +
                "ORDER BY h.hire_date DESC";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hirings.add(mapHiring(rs));
            }
        }
        return hirings;
    }

    public static void addHiring(Hiring hiring) throws SQLException {
        String sql = "INSERT INTO hiring (employee_id, hire_date, contract_number, salary, " +
                "probation_period, hr_manager_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, hiring.getEmployeeId());
            stmt.setObject(2, hiring.getHireDate());
            stmt.setString(3, hiring.getContractNumber());
            stmt.setDouble(4, hiring.getSalary());
            stmt.setObject(5, hiring.getProbationPeriod(), Types.INTEGER);
            stmt.setInt(6, hiring.getHrManagerId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    hiring.setHiringId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateHiring(Hiring hiring) throws SQLException {
        String sql = "UPDATE hiring SET employee_id = ?, hire_date = ?, contract_number = ?, " +
                "salary = ?, probation_period = ?, hr_manager_id = ? WHERE hiring_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hiring.getEmployeeId());
            stmt.setObject(2, hiring.getHireDate());
            stmt.setString(3, hiring.getContractNumber());
            stmt.setDouble(4, hiring.getSalary());
            stmt.setObject(5, hiring.getProbationPeriod(), Types.INTEGER);
            stmt.setInt(6, hiring.getHrManagerId());
            stmt.setInt(7, hiring.getHiringId());
            stmt.executeUpdate();
        }
    }

    public static void deleteHiring(int hiringId) throws SQLException {
        String sql = "DELETE FROM hiring WHERE hiring_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hiringId);
            stmt.executeUpdate();
        }
    }

    private static Hiring mapHiring(ResultSet rs) throws SQLException {
        Hiring hiring = new Hiring();
        hiring.setHiringId(rs.getInt("hiring_id"));
        hiring.setEmployeeId(rs.getInt("employee_id"));
        hiring.setHireDate(rs.getObject("hire_date", LocalDate.class));
        hiring.setContractNumber(rs.getString("contract_number"));
        hiring.setSalary(rs.getDouble("salary"));
        hiring.setProbationPeriod(rs.getInt("probation_period"));
        hiring.setHrManagerId(rs.getInt("hr_manager_id"));
        hiring.setEmployeeName(rs.getString("employee_name"));
        hiring.setHrManagerName(rs.getString("hr_manager_name"));
        return hiring;
    }
}
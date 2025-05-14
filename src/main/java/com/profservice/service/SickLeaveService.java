package com.profservice.service;

import com.profservice.model.SickLeave;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SickLeaveService {
    public static List<SickLeave> getAllSickLeaves() throws SQLException {
        List<SickLeave> sickLeaves = new ArrayList<>();
        String sql = "SELECT s.*, e.last_name || ' ' || e.first_name as employee_name " +
                "FROM sick_leaves s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "ORDER BY s.start_date DESC";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sickLeaves.add(mapSickLeave(rs));
            }
        }
        return sickLeaves;
    }

    public static void addSickLeave(SickLeave sickLeave) throws SQLException {
        String sql = "INSERT INTO sick_leaves (employee_id, start_date, end_date, " +
                "sick_leave_number, diagnosis, payment_percentage, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setSickLeaveParameters(stmt, sickLeave);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    sickLeave.setSickLeaveId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateSickLeave(SickLeave sickLeave) throws SQLException {
        String sql = "UPDATE sick_leaves SET employee_id = ?, start_date = ?, end_date = ?, " +
                "sick_leave_number = ?, diagnosis = ?, payment_percentage = ?, status = ? " +
                "WHERE sick_leave_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setSickLeaveParameters(stmt, sickLeave);
            stmt.setInt(8, sickLeave.getSickLeaveId());
            stmt.executeUpdate();
        }
    }

    public static void deleteSickLeave(int sickLeaveId) throws SQLException {
        String sql = "DELETE FROM sick_leaves WHERE sick_leave_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sickLeaveId);
            stmt.executeUpdate();
        }
    }

    private static SickLeave mapSickLeave(ResultSet rs) throws SQLException {
        SickLeave sickLeave = new SickLeave();
        sickLeave.setSickLeaveId(rs.getInt("sick_leave_id"));
        sickLeave.setEmployeeId(rs.getInt("employee_id"));
        sickLeave.setStartDate(rs.getObject("start_date", LocalDate.class));
        sickLeave.setEndDate(rs.getObject("end_date", LocalDate.class));
        sickLeave.setSickLeaveNumber(rs.getString("sick_leave_number"));
        sickLeave.setDiagnosis(rs.getString("diagnosis"));
        sickLeave.setPaymentPercentage(rs.getInt("payment_percentage"));
        sickLeave.setStatus(rs.getString("status"));
        sickLeave.setEmployeeName(rs.getString("employee_name"));
        return sickLeave;
    }

    private static void setSickLeaveParameters(PreparedStatement stmt, SickLeave sickLeave) throws SQLException {
        stmt.setInt(1, sickLeave.getEmployeeId());
        stmt.setObject(2, sickLeave.getStartDate());
        stmt.setObject(3, sickLeave.getEndDate());
        stmt.setString(4, sickLeave.getSickLeaveNumber());
        stmt.setString(5, sickLeave.getDiagnosis());
        stmt.setInt(6, sickLeave.getPaymentPercentage());
        stmt.setString(7, sickLeave.getStatus());
    }
}
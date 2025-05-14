package com.profservice.service;

import com.profservice.model.Vacation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VacationService {
    public static List<Vacation> getAllVacations() throws SQLException {
        List<Vacation> vacations = new ArrayList<>();
        String sql = "SELECT v.*, e1.last_name || ' ' || e1.first_name as employee_name, " +
                "e2.last_name || ' ' || e2.first_name as approved_by_name " +
                "FROM vacations v " +
                "JOIN employees e1 ON v.employee_id = e1.employee_id " +
                "LEFT JOIN employees e2 ON v.approved_by = e2.employee_id " +
                "ORDER BY v.start_date DESC";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vacations.add(mapVacation(rs));
            }
        }
        return vacations;
    }

    public static void addVacation(Vacation vacation) throws SQLException {
        String sql = "INSERT INTO vacations (employee_id, start_date, end_date, vacation_type, " +
                "days_count, status, approval_date, approved_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setVacationParameters(stmt, vacation);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    vacation.setVacationId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateVacation(Vacation vacation) throws SQLException {
        String sql = "UPDATE vacations SET employee_id = ?, start_date = ?, end_date = ?, " +
                "vacation_type = ?, days_count = ?, status = ?, approval_date = ?, " +
                "approved_by = ? WHERE vacation_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setVacationParameters(stmt, vacation);
            stmt.setInt(9, vacation.getVacationId());
            stmt.executeUpdate();
        }
    }

    public static void deleteVacation(int vacationId) throws SQLException {
        String sql = "DELETE FROM vacations WHERE vacation_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vacationId);
            stmt.executeUpdate();
        }
    }

    private static Vacation mapVacation(ResultSet rs) throws SQLException {
        Vacation vacation = new Vacation();
        vacation.setVacationId(rs.getInt("vacation_id"));
        vacation.setEmployeeId(rs.getInt("employee_id"));
        vacation.setStartDate(rs.getObject("start_date", LocalDate.class));
        vacation.setEndDate(rs.getObject("end_date", LocalDate.class));
        vacation.setVacationType(rs.getString("vacation_type"));
        vacation.setDaysCount(rs.getInt("days_count"));
        vacation.setStatus(rs.getString("status"));
        vacation.setApprovalDate(rs.getObject("approval_date", LocalDate.class));
        vacation.setApprovedBy(rs.getInt("approved_by"));
        vacation.setEmployeeName(rs.getString("employee_name"));
        vacation.setApprovedByName(rs.getString("approved_by_name"));
        return vacation;
    }

    private static void setVacationParameters(PreparedStatement stmt, Vacation vacation) throws SQLException {
        stmt.setInt(1, vacation.getEmployeeId());
        stmt.setObject(2, vacation.getStartDate());
        stmt.setObject(3, vacation.getEndDate());
        stmt.setString(4, vacation.getVacationType());
        stmt.setInt(5, vacation.getDaysCount());
        stmt.setString(6, vacation.getStatus());
        stmt.setObject(7, vacation.getApprovalDate());
        stmt.setObject(8, vacation.getApprovedBy(), Types.INTEGER);
    }
}
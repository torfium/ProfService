package com.profservice.service;

import com.profservice.model.Movement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovementService {
    public static List<Movement> getAllMovements() throws SQLException {
        List<Movement> movements = new ArrayList<>();
        String sql = "SELECT m.*, e.last_name || ' ' || e.first_name as employee_name, " +
                "d1.department_name as previous_department_name, " +
                "d2.department_name as new_department_name, " +
                "p1.position_name as previous_position_name, " +
                "p2.position_name as new_position_name, " +
                "er.last_name || ' ' || er.first_name as responsible_name " +
                "FROM staff_movements m " +
                "JOIN employees e ON m.employee_id = e.employee_id " +
                "LEFT JOIN departments d1 ON m.previous_department_id = d1.department_id " +
                "LEFT JOIN departments d2 ON m.new_department_id = d2.department_id " +
                "LEFT JOIN positions p1 ON m.previous_position_id = p1.position_id " +
                "LEFT JOIN positions p2 ON m.new_position_id = p2.position_id " +
                "JOIN employees er ON m.responsible_id = er.employee_id " +
                "ORDER BY m.movement_date DESC";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movements.add(mapMovement(rs));
            }
        }
        return movements;
    }

    public static void addMovement(Movement movement) throws SQLException {
        String sql = "INSERT INTO staff_movements (employee_id, movement_type, movement_date, " +
                "previous_department_id, new_department_id, previous_position_id, " +
                "new_position_id, salary_change, reason, document_reference, responsible_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setMovementParameters(stmt, movement);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movement.setMovementId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateMovement(Movement movement) throws SQLException {
        String sql = "UPDATE staff_movements SET employee_id = ?, movement_type = ?, movement_date = ?, " +
                "previous_department_id = ?, new_department_id = ?, previous_position_id = ?, " +
                "new_position_id = ?, salary_change = ?, reason = ?, document_reference = ?, " +
                "responsible_id = ? WHERE movement_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setMovementParameters(stmt, movement);
            stmt.setInt(12, movement.getMovementId());
            stmt.executeUpdate();
        }
    }

    public static void deleteMovement(int movementId) throws SQLException {
        String sql = "DELETE FROM staff_movements WHERE movement_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movementId);
            stmt.executeUpdate();
        }
    }

    private static Movement mapMovement(ResultSet rs) throws SQLException {
        Movement movement = new Movement();
        movement.setMovementId(rs.getInt("movement_id"));
        movement.setEmployeeId(rs.getInt("employee_id"));
        movement.setMovementType(rs.getString("movement_type"));
        movement.setMovementDate(rs.getObject("movement_date", LocalDate.class));
        movement.setPreviousDepartmentId(rs.getInt("previous_department_id"));
        movement.setNewDepartmentId(rs.getInt("new_department_id"));
        movement.setPreviousPositionId(rs.getInt("previous_position_id"));
        movement.setNewPositionId(rs.getInt("new_position_id"));
        movement.setSalaryChange(rs.getDouble("salary_change"));
        movement.setReason(rs.getString("reason"));
        movement.setDocumentReference(rs.getString("document_reference"));
        movement.setResponsibleId(rs.getInt("responsible_id"));
        movement.setEmployeeName(rs.getString("employee_name"));
        movement.setPreviousDepartmentName(rs.getString("previous_department_name"));
        movement.setNewDepartmentName(rs.getString("new_department_name"));
        movement.setPreviousPositionName(rs.getString("previous_position_name"));
        movement.setNewPositionName(rs.getString("new_position_name"));
        movement.setResponsibleName(rs.getString("responsible_name"));
        return movement;
    }

    private static void setMovementParameters(PreparedStatement stmt, Movement movement) throws SQLException {
        stmt.setInt(1, movement.getEmployeeId());
        stmt.setString(2, movement.getMovementType());
        stmt.setObject(3, movement.getMovementDate());
        stmt.setObject(4, movement.getPreviousDepartmentId(), Types.INTEGER);
        stmt.setObject(5, movement.getNewDepartmentId(), Types.INTEGER);
        stmt.setObject(6, movement.getPreviousPositionId(), Types.INTEGER);
        stmt.setObject(7, movement.getNewPositionId(), Types.INTEGER);
        stmt.setObject(8, movement.getSalaryChange(), Types.DOUBLE);
        stmt.setString(9, movement.getReason());
        stmt.setString(10, movement.getDocumentReference());
        stmt.setInt(11, movement.getResponsibleId());
    }
}
package com.profservice.service;

import com.profservice.model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionService {
    public static List<Position> getAllPositions() throws SQLException {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions ORDER BY position_name";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                positions.add(mapPosition(rs));
            }
        }
        return positions;
    }

    public static ObservableList<String> getAllPositionNames() throws SQLException {
        ObservableList<String> names = FXCollections.observableArrayList();
        String sql = "SELECT position_name FROM positions ORDER BY position_name";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("position_name"));
            }
        }
        return names;
    }

    public static int getPositionIdByName(String name) throws SQLException {
        String sql = "SELECT position_id FROM positions WHERE position_name = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("position_id");
            }
            return -1;
        }
    }

    public static void addPosition(Position position) throws SQLException {
        String sql = "INSERT INTO positions (position_name, base_salary, bonus_percentage, description) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setPositionParameters(stmt, position);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    position.setPositionId(rs.getInt(1));
                }
            }
        }
    }

    public static void updatePosition(Position position) throws SQLException {
        String sql = "UPDATE positions SET position_name = ?, base_salary = ?, " +
                "bonus_percentage = ?, description = ? WHERE position_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setPositionParameters(stmt, position);
            stmt.setInt(5, position.getPositionId());
            stmt.executeUpdate();
        }
    }

    public static void deletePosition(int positionId) throws SQLException {
        String sql = "DELETE FROM positions WHERE position_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, positionId);
            stmt.executeUpdate();
        }
    }

    private static Position mapPosition(ResultSet rs) throws SQLException {
        Position position = new Position();
        position.setPositionId(rs.getInt("position_id"));
        position.setPositionName(rs.getString("position_name"));
        position.setBaseSalary(rs.getDouble("base_salary"));
        position.setBonusPercentage(rs.getDouble("bonus_percentage"));
        position.setDescription(rs.getString("description"));
        return position;
    }

    private static void setPositionParameters(PreparedStatement stmt, Position position) throws SQLException {
        stmt.setString(1, position.getPositionName());
        stmt.setDouble(2, position.getBaseSalary());
        stmt.setDouble(3, position.getBonusPercentage());
        stmt.setString(4, position.getDescription());
    }
}
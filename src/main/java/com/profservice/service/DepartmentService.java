package com.profservice.service;

import com.profservice.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {
    public static List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT d.*, e.last_name || ' ' || e.first_name as manager_name " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.manager_id = e.employee_id " +
                "ORDER BY department_name";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                departments.add(mapDepartment(rs));
            }
        }
        return departments;
    }

    public static List<String> getAllDepartmentNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT department_name FROM departments ORDER BY department_name";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("department_name"));
            }
        }
        return names;
    }

    public static int getDepartmentIdByName(String name) throws SQLException {
        String sql = "SELECT department_id FROM departments WHERE department_name = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("department_id");
            }
            return -1;
        }
    }

    public static void addDepartment(Department department) throws SQLException {
        String sql = "INSERT INTO departments (department_name, manager_id, location) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setDepartmentParameters(stmt, department);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    department.setDepartmentId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateDepartment(Department department) throws SQLException {
        String sql = "UPDATE departments SET department_name = ?, manager_id = ?, location = ? " +
                "WHERE department_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setDepartmentParameters(stmt, department);
            stmt.setInt(4, department.getDepartmentId());
            stmt.executeUpdate();
        }
    }

    public static void deleteDepartment(int departmentId) throws SQLException {
        String sql = "DELETE FROM departments WHERE department_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);
            stmt.executeUpdate();
        }
    }

    private static Department mapDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        department.setDepartmentName(rs.getString("department_name"));
        department.setManagerId(rs.getInt("manager_id"));
        department.setLocation(rs.getString("location"));
        department.setManagerName(rs.getString("manager_name"));
        return department;
    }

    private static void setDepartmentParameters(PreparedStatement stmt, Department department) throws SQLException {
        stmt.setString(1, department.getDepartmentName());
        if (department.getManagerId() != null && department.getManagerId() > 0) {
            stmt.setInt(2, department.getManagerId());
        } else {
            stmt.setNull(2, Types.INTEGER);
        }
        stmt.setString(3, department.getLocation());
    }
}
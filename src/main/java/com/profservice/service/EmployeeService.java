package com.profservice.service;

import com.profservice.model.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    public static List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.position_name, d.department_name " +
                "FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.position_id " +
                "LEFT JOIN departments d ON e.department_id = d.department_id";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }
        return employees;
    }

    public static List<String> getAllEmployeeNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT last_name || ' ' || first_name || 'middle_name' as full_name FROM employees ORDER BY last_name, first_name";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("full_name"));
            }
        }
        return names;
    }

    public static int getEmployeeIdByName(String name) throws SQLException {
        String[] parts = name.split(" ", 2);
        if (parts.length != 2) return -1;

        String sql = "SELECT employee_id FROM employees WHERE (last_name = ? AND first_name = ?) AND middle_name = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parts[0]);
            stmt.setString(2, parts[1]);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("employee_id");
            }
            return -1;
        }
    }

    public static List<Employee> getEmployeeById(int employeeId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.*, p.position_name, d.department_name " +
                "FROM employees e " +
                "LEFT JOIN positions p ON e.position_id = p.position_id " +
                "LEFT JOIN departments d ON e.department_id = d.department_id " +
                "WHERE e.employee_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }
        return employees;
    }

    public static void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (last_name, first_name, middle_name, birth_date, gender, " +
                "passport_series, passport_number, address, phone, email, hire_date, dismissal_date, " +
                "position_id, department_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setEmployeeParameters(stmt, employee);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setEmployeeId(rs.getInt(1));
                }
            }
        }
    }

    public static void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET last_name = ?, first_name = ?, middle_name = ?, " +
                "birth_date = ?, gender = ?, passport_series = ?, passport_number = ?, " +
                "address = ?, phone = ?, email = ?, hire_date = ?, dismissal_date = ?, " +
                "position_id = ?, department_id = ?, status = ? " +
                "WHERE employee_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setEmployeeParameters(stmt, employee);
            stmt.setInt(16, employee.getEmployeeId());
            stmt.executeUpdate();
        }
    }

    public static void deleteEmployee(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
        }
    }

    private static Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setLastName(rs.getString("last_name"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setMiddleName(rs.getString("middle_name"));
        employee.setBirthDate(rs.getObject("birth_date", LocalDate.class));
        employee.setGender(rs.getString("gender"));
        employee.setPassportSeries(rs.getString("passport_series"));
        employee.setPassportNumber(rs.getString("passport_number"));
        employee.setAddress(rs.getString("address"));
        employee.setPhone(rs.getString("phone"));
        employee.setEmail(rs.getString("email"));
        employee.setHireDate(rs.getObject("hire_date", LocalDate.class));
        employee.setDismissalDate(rs.getObject("dismissal_date", LocalDate.class));
        employee.setPositionId(rs.getInt("position_id"));
        employee.setDepartmentId(rs.getInt("department_id"));
        employee.setStatus(rs.getString("status"));
        employee.setPositionName(rs.getString("position_name"));
        employee.setDepartmentName(rs.getString("department_name"));
        return employee;
    }

    private static void setEmployeeParameters(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getLastName());
        stmt.setString(2, employee.getFirstName());
        stmt.setString(3, employee.getMiddleName());
        stmt.setObject(4, employee.getBirthDate());
        stmt.setString(5, employee.getGender());
        stmt.setString(6, employee.getPassportSeries());
        stmt.setString(7, employee.getPassportNumber());
        stmt.setString(8, employee.getAddress());
        stmt.setString(9, employee.getPhone());
        stmt.setString(10, employee.getEmail());
        stmt.setObject(11, employee.getHireDate());
        stmt.setObject(12, employee.getDismissalDate());
        stmt.setObject(13, employee.getPositionId() > 0 ? employee.getPositionId() : null);
        stmt.setObject(14, employee.getDepartmentId() > 0 ? employee.getDepartmentId() : null);
        stmt.setString(15, employee.getStatus());
    }
}
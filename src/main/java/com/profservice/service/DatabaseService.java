package com.profservice.service;

import java.sql.*;

public class DatabaseService {
    private static final String DB_NAME = "ProfServicePersonnelRecords.db";
    //private static final String DB_URL = "jdbc:sqlite:./data/" + DB_NAME; // Или полный путь к файлу
    private static final String DB_URL = "jdbc:sqlite:C:/Users/Computer/IdeaProjects/testProfService/1/ProfService/src/main/resources/com/profservice/data/ProfServicePersonnelRecords.db";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL);

            }
            catch (SQLException e) {
                throw new SQLException("Ошибка подключения к базе данных: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
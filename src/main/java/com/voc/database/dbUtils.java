package com.voc.database;

import java.sql.*;

public class dbUtils {
    
    private static Connection globalConnection = null;

    private Connection createDatabase(String url, String dbName, String user, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(sql);
            System.out.println("Database created successfully.");
            statement.close();
            connection.close();

            connection = DriverManager.getConnection(url + dbName, user, password);
            System.out.println("Connected to the database: " + dbName);            
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }

        if (connection != null) {
            createTables(connection);
        } else {
            System.out.println("Failed to establish a connection to the database.");
        }

        return connection;
    }

    private void createTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "session VARCHAR(255), " +
                    "username VARCHAR(255) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "finished_words TEXT, " +
                    "settings TEXT, " +
                    "levels TEXT, " +
                    "exp INT DEFAULT 0, " +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            statement.executeUpdate(sql);
            System.out.println("Users table created successfully.");
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
        }
    }

    public void checkDatabase() {
        String url = "jdbc:mysql://localhost:3306/"; // start with the base URL for MySQL
        String dbName = "vocard";
        String user = "test";
        String password = "test";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url + dbName, user, password);
            System.out.println("Database " + dbName + " exists.");
        } catch (SQLException e) {
            System.out.println("Database " + dbName + " does not exist. Creating database...");
            connection = createDatabase(url, dbName, user, password);
        }

        if (connection != null) {
            globalConnection = connection; // Store the connection in a global variable
            System.out.println("Database connection established successfully.");
        } else {
            System.out.println("Failed to establish a database connection.");
        }
    }

    public Connection getConnection() {
        if (globalConnection == null) {
            checkDatabase();
        }
        return globalConnection;
    }
}

package com.voc.database;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class dbUtils {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "vocard";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "CrimsonXD!1";

    private static Connection globalConnection = null;

    static {
        System.out.println("Loading default dataset at class load...");

        try (InputStream input = dbUtils.class.getClassLoader().getResourceAsStream("config/database.json")) {
            if (input == null) {
                throw new RuntimeException("database.json not found or haven't config. Please check the config folder in resources file.");
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String,String> data = mapper.readValue(input, HashMap.class);
            
            DB_URL = data.get("DB_URL");
            DB_NAME = data.get("DB_NAME");
            DB_USER = data.get("DB_USER");
            DB_PASSWORD = data.get("DB_PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Connection createDatabase(String url, String dbName, String user, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
            
            System.out.println("Database created successfully.");
            connection = DriverManager.getConnection(url + dbName, user, password);
        } catch (SQLException e) {System.out.println("Error creating database: " + e.getMessage());}

        if (connection != null) {createTables(connection);}
        
        else {System.out.println("Failed to establish a connection to the database.");}

        return connection;
    }

    private static void createTables(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "token_id VARCHAR(255), " +
                    "username VARCHAR(255) NOT NULL UNIQUE, " +
                    "display_name VARCHAR(255)," +
                    "password VARCHAR(255) NOT NULL, " +
                    "finished_words TEXT, " +
                    "settings TEXT, " +
                    "levels TEXT, " +
                    "exp INT DEFAULT 0, " +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "password_reset_count INT DEFAULT 0" +
                    ")";
            statement.executeUpdate(sql);
            System.out.println("Users table created successfully.");
            statement.close();
        } catch (SQLException e) {System.out.println("Error creating users table: " + e.getMessage());}
    }

    public static void checkDatabase() {
        String url = DB_URL;
        String dbName = DB_NAME;
        String user = DB_USER;
        String password = DB_PASSWORD;
        Connection connection = null;

        try {connection = DriverManager.getConnection(url + dbName, user, password);} 
        catch (SQLException e) {
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

    public static Connection getConnection() {
        if (globalConnection == null) {checkDatabase();}
        return globalConnection;
    }
}

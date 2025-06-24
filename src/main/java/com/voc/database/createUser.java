package com.voc.database;

import java.sql.*;

public class createUser {

    public static void addUser(Connection db, String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User created successfully: " + username);
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }
}

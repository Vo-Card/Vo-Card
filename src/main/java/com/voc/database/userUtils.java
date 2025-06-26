package com.voc.database;

import java.sql.*;
import com.voc.security.tokenUtils;

public class userUtils {

    public static void createUser(Connection db, String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            createNewSession(db, username, password);
            
            System.out.println("User created successfully: " + username);
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public static boolean userExists(Connection db, String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {return rs.getInt(1) > 0;}
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public static String getUserSession(Connection db, String username) {
        String sql = "SELECT session FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("session");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user session: " + e.getMessage());
        }
        return null;
    }

    public static boolean validateUser(Connection db, String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
        }
        return false;
    }

    private static void createNewSession(Connection db, String username, String password) {
        String sql = "UPDATE users SET session = ? WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, tokenUtils.generateToken(username, getUserId(db, username), password));
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error clearing user session: " + e.getMessage());
        }
    }

    public static String getUserPassword(Connection db, String username) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user password: " + e.getMessage());
        }
        return null;
    }

    public static void updateUserPassword(Connection db, String username, String newPassword, String oldPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.setString(3, oldPassword);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully for user: " + username);
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.out.println("Error updating user password: " + e.getMessage());
        }
    }

    public static int getUserId(Connection db, String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user ID: " + e.getMessage());
        }
        return -1; // Return -1 if user not found
    }

    public static void createSession(Connection db, String username) {
        String sql = "UPDATE users SET session = ? WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, tokenUtils.generateToken(username, getUserId(db, username), getUserPassword(db, username)));
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Session updated successfully for user: " + username);
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.out.println("Error updating user session: " + e.getMessage());
        }
    }

    public static boolean validateSession(Connection db, String token) {
    String sql = "SELECT session FROM users WHERE username = ? AND id = ?";
    try (PreparedStatement pstmt = db.prepareStatement(sql)) {
        pstmt.setString(1, tokenUtils.getUsernameFromToken(token));
        pstmt.setInt(2, tokenUtils.getUserIdFromToken(token));
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String username = rs.getString("username");
            String password = getUserPassword(db, username);
            return tokenUtils.validateToken(token, password);
        }
    } catch (SQLException e) {
        System.out.println("Error validating session: " + e.getMessage());
    }
    String username = tokenUtils.getUsernameFromToken(token);
    if (username != null && userExists(db, username)) {
        String password = getUserPassword(db, username);
        return tokenUtils.validateToken(token, password);
    } else {
        System.out.println("Invalid session or user not found.");
    }
        return false; // Return false if session is invalid or user not found
    }
}

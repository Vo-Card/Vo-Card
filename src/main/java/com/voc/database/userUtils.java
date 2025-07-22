package com.voc.database;

import java.sql.*;
import com.voc.security.tokenUtils;

public class userUtils {

    public static void createUser(Connection db, String display_name,String username, String password) {
        String sql = "INSERT INTO users (display_name, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, display_name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();

            createNewToken(db, username, password);

        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public static boolean userExists(Connection db, String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public static String getUserTokenID(Connection db, String username) {
        String sql = "SELECT token_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("token_id");
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

    private static void createNewToken(Connection db, String username, String password) {
        String sql = "UPDATE users SET token_id = ? WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, tokenUtils.generateToken(username, getUserId(db, username), password));
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error clearing user token_id: " + e.getMessage());
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

    public static String searchByToken(Connection db, String token, String search) {
        String sql = "SELECT " + search + " FROM users WHERE token_id = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(search);
            }
        } catch (SQLException e) {
            System.out.println("Error searching user by token: " + e.getMessage());
        }
        return null; // Return null if user not found
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

    public static void createTokenID(Connection db, String username) {
        String sql = "UPDATE users SET token_id = ? WHERE username = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1,
                    tokenUtils.generateToken(username, getUserId(db, username), getUserPassword(db, username)));
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Token updated successfully for user: " + username);
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.out.println("Error updating user session: " + e.getMessage());
        }
    }

    public static boolean validateTokenID(Connection db, String token) {
        String sql = "SELECT username, token_id FROM users WHERE username = ? AND id = ?";
        try (PreparedStatement pstmt = db.prepareStatement(sql)) {

            String username = tokenUtils.getUsernameFromToken(token);
            int userId = tokenUtils.getUserIdFromToken(token);

            pstmt.setString(1, username);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String usr = rs.getString("username");
                String password = getUserPassword(db, usr);
                return tokenUtils.validateToken(token, password);
            }
        } catch (SQLException e) {
            System.out.println("Error validating token: " + e.getMessage());
        }
        return false;
    }
}
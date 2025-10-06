package com.voc.database;

import com.voc.security.Permission;

import java.util.List;

import com.voc.security.AuthManager;
import com.voc.security.PasswordUtils;
import com.voc.security.SessionManager;
import com.voc.utils.Convertors;
import com.voc.utils.Row;

public class UserManager {
    public static Boolean updateUserInfo(Long userId, String newUsername, String newDisplayName,
            String newPassword, String currentPassword, String sessionId) {

        // Incase of no changes
        if (newUsername == null && newDisplayName == null && newPassword == null)
            return false;

        // Get user current data
        Row userData = DatabaseUtils.sqlSingleRowStatement("SELECT * FROM usertb WHERE user_id_PK = ?", userId);
        if (userData == null)
            return false;

        String hashedPassword = (String) userData.get("password");
        Boolean isCorrectPassword = PasswordUtils.verifyPassword(currentPassword, hashedPassword);

        if (isCorrectPassword) {
            if (newUsername != null && !newUsername.equals(userData.get("username"))) {
                // Check if username is taken
                if (AuthManager.isUserExist(newUsername) || Permission.isUserRoot(userId)) {
                    return false;
                }
                String sql = "UPDATE usertb SET username = ? WHERE user_id_PK = ?";
                DatabaseUtils.sqlPrepareStatement(sql, newUsername, userId);
            }

            if (newDisplayName != null && !newDisplayName.equals(userData.get("display_name"))) {
                String sql = "UPDATE usertb SET display_name = ? WHERE user_id_PK = ?";
                DatabaseUtils.sqlPrepareStatement(sql, newDisplayName, userId);
            }

            if (newPassword != null) {
                String newHashedPassword = PasswordUtils.generateSecretKey(newPassword);
                String sql = "UPDATE usertb SET password = ? WHERE user_id_PK = ?";
                DatabaseUtils.sqlPrepareStatement(sql, newHashedPassword, userId);
                // Clear all sessions
                SessionManager.deleteAllSessionsForUser(userId, sessionId);
            }
            return true;
        }

        return false;
    }

    public static Boolean removeUser(Long userId, String currentPassword, boolean bypass) {
        // Get user current data
        Row userData = DatabaseUtils.sqlSingleRowStatement("SELECT * FROM usertb WHERE user_id_PK = ?", userId);
        if (userData == null)
            return false;

        String hashedPassword = (String) userData.get("password");
        Boolean isCorrectPassword = PasswordUtils.verifyPassword(currentPassword, hashedPassword);

        if (isCorrectPassword || bypass) {
            String sql = "DELETE FROM usertb WHERE user_id_PK = ?";
            DatabaseUtils.sqlPrepareStatement(sql, userId);

            return true;
        }

        return false;
    }

    public static List<Row> getAllUserByPage(Long page) {
        if (page == null || page < 1) {
            page = 1L; // default to first page
        }

        int pageSize = 20;
        long offset = (page - 1) * pageSize;

        String sql = """
                SELECT user_id_PK,
                        username
                FROM usertb
                ORDER BY user_id_PK
                LIMIT ?
                OFFSET ?
                """;
        List<Row> userTable = DatabaseUtils.sqlPrepareStatement(sql, pageSize, offset).getData();
        Convertors.convertIdsToString(userTable, "user_id_PK");
        return userTable;
    }

    public static List<Row> getAllRoleByPage(Long page){
        if (page == null || page < 1) {
            page = 1L;
        }

        int pageSize = 20;
        long offset = (page - 1) * pageSize;

        String sql = """
               SELECT
                    p.permission_id_PK,
                    p.permission_name,
                    p.permission_level,
                    p.permission_description,
                    COUNT(u.user_id_PK) AS user_count
                FROM
                    permissiontb p
                LEFT JOIN
                    usertb u ON p.permission_id_PK = u.permission_level_FK
                GROUP BY
                    p.permission_id_PK, p.permission_name
                ORDER BY
                    p.permission_id_PK
                LIMIT ?
                OFFSET ?;
                """;

        List<Row> roleTable = DatabaseUtils.sqlPrepareStatement(sql, pageSize, offset).getData();
        Convertors.convertIdsToString(roleTable, "permission_id_PK");
        return roleTable;
    }
}

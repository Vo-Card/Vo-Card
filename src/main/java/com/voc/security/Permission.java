package com.voc.security;

import static com.voc.utils.AnsiColor.TAG_ERROR;

import java.util.List;

import com.voc.database.DatabaseUtils;
import com.voc.database.SQLResult;
import com.voc.server.Snowflake;
import com.voc.utils.Convertors;
import com.voc.utils.Row;

public class Permission {

    // Normal Permission
    public class Values {
        public static final long ROOT_USER = 1L << 63;

        public static final long DELETE_USER = 1L << 0;
        public static final long UPDATE_USER = 1L << 1;

        public static final long FORCE_DELETE_ITEM = 1L << 2;
        public static final long FORCE_UPDATE_ITEM = 1L << 3;
        public static final long FORCE_CREATE_ITEM = 1L << 4;

        public static final long MODERATE_EXPLORER = 1L << 5;

        public static final long VIEW_AUDIT_LOG = 1L << 6;

        public static final long CHANGE_USERNAME = 1L << 7;
        public static final long VIEW_ALL_USER = 1L << 8;
    }

    public static Long getPermissionViaUserId(Long userId) {
        String sql = """
                SELECT p.permission_level
                FROM permissiontb p
                JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                WHERE u.user_id_PK = ?
                                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, userId);

        return (res != null) ? ((Number) res.get("permission_level")).longValue() : null;
    }

    public static Long getRootUserPermissionId() {
        String sql = """
                SELECT p.permission_id_PK
                FROM permissiontb p
                JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                WHERE u.username = ?
                                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, DatabaseUtils.getRootUsername());

        return (res != null) ? ((Number) res.get("permission_id_PK")).longValue() : null;
    }

    public static boolean checkUserPermission(Long userId, Long checkPermission) {
        String sql = """
                SELECT p.permission_level
                    FROM permissiontb p
                    JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                    WHERE u.user_id_PK = ?
                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, userId);
        Long userPermission = res != null ? ((Number) res.get("permission_level")).longValue() : 0;

        if ((userPermission & checkPermission) != 0 || (userPermission & Values.ROOT_USER) != 0) {
            return true;
        }
        return false;
    }

    public static Long createRole(Long userId, Long permissionBitmask, String roleName, String roleDesc) {

        Long permId = Snowflake.nextId();
        String sql = """
                INSERT INTO permissiontb (permission_id_PK, permission_name, permission_level, permission_description)
                VALUES (?, ?, ?, ?)
                """;

        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, permId, roleName, permissionBitmask, roleDesc);

        if (!res.isSuccess()) {
            System.err.println(TAG_ERROR + res.getErrorMessage());
        }

        return permId;
    }

    public static Boolean removeRole(Long userId, Long selectedRole) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return null;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return null;
        }

        String sql = """
                DELETE FROM permissiontb
                WHERE permission_id_PK = ?
                """;

        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, selectedRole);

        if (!res.isSuccess()) {
            System.err.println(TAG_ERROR + res.getErrorMessage());
            return false;
        }
        return true;
    }

    public static void updateRole(Long permissionId, String newName, Long newLevel, String newDescription,
            Long userId) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return;
        }

        if ((newLevel & Values.ROOT_USER) == 1) {
            System.err.println(TAG_ERROR + "Cannot create new root role");
            return;
        }

        String sql = """
                UPDATE permissiontb
                SET permission_name = COALESCE(?, permission_name),
                    permission_level = COALESCE(?, permission_level),
                    permission_description = COALESCE(?, permission_description)
                WHERE permission_id_PK = ?
                """;

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, newName, newLevel, newDescription, permissionId);

        if (!result.isSuccess()) {
            System.err.println(TAG_ERROR + result.getErrorMessage());
        }
    }

    public static void updateUserRole(Long target, Long permissionId, Long userId) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return;
        }

        String sql = """
                UPDATE usertb
                SET permission_level_FK = ?
                WHERE user_id_PK = ?
                """;

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, permissionId, target);

        if (!result.isSuccess()) {
            System.err.println(TAG_ERROR + result.getErrorMessage());
        }
    }

    public static void removeUserRole(Long target, Long userId) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return;
        }

        String sql = """
                UPDATE usertb
                SET permission_level_FK = ?
                WHERE user_id_PK = ?
                """;

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, null, target);

        if (!result.isSuccess()) {
            System.err.println(TAG_ERROR + result.getErrorMessage());
        }
    }

    public static Row getCurrentRowInfo(Long target) {
        String sql = """
                SELECT *
                FROM permissiontb
                WHERE permission_id_PK = ?
                """;
        Row result = DatabaseUtils.sqlSingleRowStatement(sql, target);
        if (result != null) {
            Convertors.convertIdsToString(result, "permission_id_PK");
            return result;
        }
        return null;
    }

    // Hard code
    public static void moderationAutoLog(Long userId, String usedFunc, String message) {
        String sql = """
                INSERT INTO
                moderation_action (action_id_PK, action_type, action_message, user_id)
                VALUES (?,?,?,?)
                """;

        Long permId = Snowflake.nextId();
        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, permId, usedFunc, message, userId);
        if (!result.isSuccess()) {
            System.err.println(TAG_ERROR + result.getErrorMessage());
        }
    }

    public static List<Row> getModerationLog(Long userId) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return null;
        }


        if (((permissionLevel & Values.VIEW_AUDIT_LOG) != 0) || ((permissionLevel & Values.ROOT_USER) != 0)) {

            String sql = """
                        SELECT  m.action_type,
                                m.action_message,
                                m.action_time_stamp,
                                u.username
                        FROM moderation_action m
                        JOIN usertb u ON m.user_id = u.user_id_PK
                        ORDER BY m.action_time_stamp

                    """;
            List<Row> result = DatabaseUtils.sqlPrepareStatement(sql).getData();
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}

package com.voc.security;

import static com.voc.utils.AnsiColor.TAG_ERROR;

import com.voc.database.DatabaseUtils;
import com.voc.database.SQLResult;
import com.voc.server.Snowflake;
import com.voc.utils.Row;

public class Permission {

    // Normal Permission
    public class Values {
        public static final long ROOT_USER = 1 << 63;

        public static final long DELETE_USER = 1 << 0;
        public static final long UPDATE_USER = 1 << 1;

        public static final long FORCE_DELETE_ITEM = 1 << 2;
        public static final long FORCE_UPDATE_ITEM = 1 << 3;
        public static final long FORCE_CREATE_ITEM = 1 << 4;

        public static final long MODERATE_EXPLORER = 1 << 5;

        public static final long VIEW_AUDIT_LOG = 1 << 6;

        public static final long CHANGE_USERNAME = 1 << 7;
        public static final long VIEW_ALL_USER = 1 << 8;
    }

    private static Long getPermissionViaUserId(Long userId) {
        String sql = """
                SELECT p.permission_level
                FROM permissiontb p
                JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                WHERE u.user_id_PK = ?
                                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, userId);

        return (res != null) ? ((Number) res.get("permission_level")).longValue() : null;
    }

    private static Long getRootUserPermissionId(Long userId) {
        String sql = """
                SELECT p.permission_id_PK
                FROM permissiontb p
                JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                WHERE u.user_id_PK = ? AND u.username = ?
                                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, userId, DatabaseUtils.getRootUsername());

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
        Long userPermission = ((Number) res.get("permission_level")).longValue();

        if ((userPermission & checkPermission) != 0 || (userPermission & Values.ROOT_USER) != 0) {
            return true;
        }
        return false;
    }

    private static boolean isRootUserPresent() {
        String sql = """
                    SELECT p.permission_level
                    FROM permissiontb p
                    JOIN usertb u ON u.permission_level_FK = p.permission_id_PK
                    WHERE u.username = ?
                """;

        Row res = DatabaseUtils.sqlSingleRowStatement(sql, DatabaseUtils.getRootUsername());
        if (res == null)
            return false;

        Long permissionLevel = ((Number) res.get("permission_level")).longValue();
        return (permissionLevel & Values.ROOT_USER) != 0;
    }

    public static Boolean isUserRoot(Long userId) {
        Long permissionLevel = getPermissionViaUserId(userId);
        if (permissionLevel == null)
            return false;

        return (permissionLevel & Values.ROOT_USER) != 0;
    }

    public static Long createRole(Long userId, Long permissionBitmask, String roleName, String roleDesc) {
        if (isRootUserPresent()) {
            Long permissionLevel = getPermissionViaUserId(userId);

            if (permissionLevel == null) {
                System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
                return null;
            }

            if ((permissionLevel & Values.ROOT_USER) == 0) {
                System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
                return null;
            }

            if ((permissionBitmask & Values.ROOT_USER) != 0 && isRootUserPresent()) {
                System.err.println(TAG_ERROR + "You cannot have more than 1 ROOT USER");
                return null;
            }
        }

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

    public static void removeRole(Long userId, Long selectedRole) {
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
                DELETE FROM permissiontb
                WHERE permission_id_PK = ?
                """;

        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, selectedRole);

        if (!res.isSuccess()) {
            System.err.println(TAG_ERROR + res.getErrorMessage());
        }
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

}

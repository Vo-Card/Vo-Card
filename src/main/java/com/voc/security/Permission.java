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

    public static void createRole(Long userId, Long permissionBitmask, String roleName, String roleDesc) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return;
        }

        if ((permissionBitmask & Values.ROOT_USER) != 0 && isRootUserPresent()) {
            System.err.println(TAG_ERROR + "You cannot have more than 1 ROOT USER");
            return;
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
    }

    public static void removeRole(Long userId, Long permissionBitmask, String roleName, String roleDesc) {
        Long permissionLevel = getPermissionViaUserId(userId);

        if (permissionLevel == null) {
            System.err.println(TAG_ERROR + "User not found or no permissions assigned.");
            return;
        }

        if ((permissionLevel & Values.ROOT_USER) == 0) {
            System.err.println(TAG_ERROR + "Access denied: only root user can perform this action.");
            return;
        }

        if ((permissionBitmask & Values.ROOT_USER) != 0 && isRootUserPresent()) {
            System.err.println(TAG_ERROR + "You cannot have more than 1 ROOT USER");
            return;
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
    }

}

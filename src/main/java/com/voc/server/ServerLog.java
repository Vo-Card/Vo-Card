package com.voc.server;

import static com.voc.utils.AnsiColor.TAG_ERROR;

import com.voc.database.DatabaseUtils;
import com.voc.database.SQLResult;

public class ServerLog {

    public class actionType {

        // Action - User
        public static final int createAccount = 1001;
        public static final int createDeck = 1002;
        public static final int createCard = 1003;
        public static final int createLevel = 1004;

        public static final int updateDeck = 1005;
        public static final int updateCard = 1006;
        public static final int updateLevel = 1007;

        public static final int cloneDeck = 1008;
        public static final int forkDeck = 1009;
        public static final int deleteAccount = 1010;

        public static final int updateUsername = 1011;
        public static final int updateDisplay = 1012;
        public static final int updatePassword = 1013;

        // Action - Moderation
        public static final int forceDeleteUser = 2001;
        public static final int forceDeleteDeck = 2002;
        public static final int forceDeleteCard = 2003;
        public static final int forceDeleteLevel = 2004;

        public static final int forceUpdateUsername = 2005;
        public static final int forceUpdateDeck = 2006;
        public static final int forceUpdateCard = 2007;
        public static final int forceUpdateLevel = 2008;

        public static final int forceCreateUsername = 2009;
        public static final int forceCreateDeck = 2010;
        public static final int forceCreateCard = 2011;
        public static final int forceCreateLevel = 2012;

        // ROOT - action
        public static final int createRole = 5000;
        public static final int editRole = 5001;
        public static final int assignRole = 5002;
        public static final int removeRole = 5003;
        public static final int deleteRole = 5004;

    }

    public static void autoCreateLog(Long userId, String actionType, String actionMessage) {

        Long actionId = Snowflake.nextId();
        String sql = """
                INSERT INTO moderation_action(action_id_PK, action_type, action_message, user_id_FK)
                VALUE(?, ?, ?, ?)
                """;
        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, actionId, actionType, actionMessage);
        if (!result.isSuccess()) {
            System.err.println(TAG_ERROR + result.getErrorMessage());
        }
    }

}

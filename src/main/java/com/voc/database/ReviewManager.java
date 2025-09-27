package com.voc.database;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.voc.server.Snowflake;
import com.voc.utils.Row;

public class ReviewManager {
    public static Boolean updateReviewedCard(Long userId, Long deckId, Long cardId) {
        String sql = """
                    INSERT INTO learned_cardtb (user_id_FK, deck_id_FK, card_id_FK, lastest_review)
                    VALUES (?, ?, ?, NOW())
                    ON DUPLICATE KEY UPDATE lastest_review = NOW();
                """;
        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, userId, deckId, cardId);

        return res.getAffectedRow() > 0;
    }

    public static Boolean addCompletionSession(Long userId, Integer totalCards, Integer totalCorrect,
            Integer totalFailed) {
        Long resultId = Snowflake.nextId();
        String sql = """
                    INSERT INTO learned_resulttb (result_id_PK, user_id_FK, passed, failed, total_cards)
                    VALUES (?, ?, ?, ?, ?);
                """;
        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, resultId, userId, totalCorrect, totalFailed, totalCards);

        return res.getAffectedRow() > 0;
    }

    public static List<Row> getThisWeekStats(Long userId) {
        String sql = """
                        SELECT
                            DAYOFWEEK(completed_date) AS day_of_week,
                            DATE(completed_date) AS review_date,
                            SUM(total_cards) AS total_reviews,
                            SUM(passed) AS total_correct,
                            SUM(failed) AS total_failed
                        FROM learned_resulttb
                        WHERE user_id_FK = ?
                          AND YEARWEEK(completed_date, 1) = YEARWEEK(CURDATE(), 1)
                        GROUP BY day_of_week, review_date
                        ORDER BY review_date ASC;
                """;

        SQLResult res = DatabaseUtils.sqlPrepareStatement(sql, userId);

        if (!res.isSuccess()) {
            System.err.println("Error fetching this week's stats: " + res.getErrorMessage());
            return Collections.emptyList();
        }

        Map<Integer, Row> statsMap = new HashMap<>();
        for (Row row : res.getData()) {
            statsMap.put(((Number) row.get("day_of_week")).intValue(), row);
        }

        List<Row> finalStats = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            int mysqlDayOfWeek = (day.getDayOfWeek().getValue() % 7) + 1;

            if (statsMap.containsKey(mysqlDayOfWeek)) {
                finalStats.add(statsMap.get(mysqlDayOfWeek));
            } else {
                Row emptyRow = new Row();
                emptyRow.put("day_of_week", mysqlDayOfWeek);
                emptyRow.put("review_date", java.sql.Date.valueOf(day));
                emptyRow.put("total_reviews", 0);
                emptyRow.put("total_correct", 0);
                emptyRow.put("total_failed", 0);
                finalStats.add(emptyRow);
            }
        }

        return finalStats;
    }

    public static Row getLastestReview(Long userId) {
        String sql = """
                    SELECT
                        c.*,
                        lv.level_name,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern
                    FROM learned_cardtb lc
                    LEFT JOIN cardtb c ON lc.card_id_FK = c.card_id_PK
                    LEFT JOIN card_leveltb lv ON c.level_id_FK = lv.level_id_PK
                    LEFT JOIN themetb t ON lv.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON lv.modifier_id_FK = m.modifier_id_PK
                    WHERE lc.user_id_FK = ?
                      AND DATE(lc.lastest_review) = CURDATE()
                    ORDER BY lc.lastest_review DESC
                    LIMIT 1;
                """;
        Row row = DatabaseUtils.sqlSingleRowStatement(sql, userId);
        return row;
    }

}

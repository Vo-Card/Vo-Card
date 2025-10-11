package com.voc.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.voc.server.Snowflake;
import com.voc.utils.Convertors;
import com.voc.utils.Row;
import com.voc.utils.ThemeTypes;

/**
 * DeckManager is intended to handle operations related to decks in the system,
 * such as creation, modification, and retrieval of deck data from the database.
 * <p>
 * Currently unfinished. Implementation will be added later.
 * </p>
 * 
 * <p>
 * Example usage (future):
 * </p>
 * 
 * <pre>
 * DeckManager.createDeck("My First Deck");
 * List<Card> cards = DeckManager.getDeckCards(deckId);
 * </pre>
 */
public class DeckManager {

    /**
     * Initialize the defaultdeck to the root user of the project.
     */
    public static void initializeDeckTable() {
        long deckCount = ((Number) DatabaseUtils.sqlSingleRowStatement(
                "SELECT COUNT(*) FROM decktb").get("COUNT(*)")).longValue();
        if (deckCount != 0)
            return;

        Long rootUserID = ((Number) DatabaseUtils.sqlSingleRowStatement(
                "SELECT user_id_PK FROM usertb WHERE username = ?", DatabaseUtils.getRootUsername())
                .get("user_id_PK")).longValue();

        Long deckId = Snowflake.nextId();
        createNewDeck(deckId, "Default", "This is the VoCard official default deck.", true,
                null, null, rootUserID);

        Long themeId = createNewTheme(deckId, "Default", ThemeTypes.Deck, "/components/template/deck_template.svg");
        Long themeModifierId = createNewModifier(deckId, "#0f1114", "#C38A39", null, ThemeTypes.Deck);

        DatabaseUtils.sqlPrepareStatement(
                "UPDATE decktb SET theme_id_FK = ?, modifier_id_FK = ? WHERE deck_id_PK = ?",
                themeId, themeModifierId, deckId);

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = DeckManager.class.getClassLoader()
                .getResourceAsStream("datasets/default_deck_sample.json")) {
            if (input == null)
                throw new RuntimeException("File not found!");

            Map<String, Map<String, Map<String, Map<String, List<String>>>>> rootJson = mapper.readValue(input,
                    new TypeReference<>() {
                    });
            Map<String, Map<String, Map<String, List<String>>>> defaultDeck = rootJson.get("default");

            List<Object[]> levelBatch = new ArrayList<>();
            List<Long> levelIds = new ArrayList<>();
            List<Object[]> cardBatch = new ArrayList<>();
            List<Object[]> posBatch = new ArrayList<>();
            List<Object[]> defBatch = new ArrayList<>();

            int weight = 1;
            for (Map.Entry<String, Map<String, Map<String, List<String>>>> levelData : defaultDeck.entrySet()) {
                Long levelId = Snowflake.nextId();
                levelIds.add(levelId);

                // Insert level with NULL theme/modifier initially
                levelBatch.add(new Object[] { levelId, levelData.getKey(), weight, null, null, deckId });

                for (Map.Entry<String, Map<String, List<String>>> cardData : levelData.getValue().entrySet()) {
                    Long cardId = Snowflake.nextId();
                    cardBatch.add(new Object[] { cardId, levelId, cardData.getKey() });

                    for (Map.Entry<String, List<String>> posData : cardData.getValue().entrySet()) {
                        Long posId = Snowflake.nextId();
                        posBatch.add(new Object[] { posId, cardId, posData.getKey() });

                        for (String def : posData.getValue()) {
                            Long defId = Snowflake.nextId();
                            defBatch.add(new Object[] { defId, posId, def });
                        }
                    }
                }

                weight++;
            }

            // Batch insert levels, cards, poses, definitions
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO card_leveltb (level_id_PK, level_name, level_weight, theme_id_FK, modifier_id_FK, deck_id_FK) "
                            +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    levelBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO cardtb (card_id_PK, level_id_FK, card_word) VALUES (?, ?, ?)", cardBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)", posBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)", defBatch);

            // Now create themes/modifiers per level and update them
            weight = 1;
            for (Long levelId : levelIds) {
                Long levelThemeId = null;
                Long levelThemeModifierId = null;

                switch (weight) {
                    case 1: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#395B8E", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    case 2: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#26226B", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    case 3: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#6B1D42", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    case 4: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#AF3935", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    case 5: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#6CA233", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    case 6: {
                        levelThemeId = createNewTheme(levelId, "Default", ThemeTypes.Card,
                                "/components/template/card_template.svg");
                        levelThemeModifierId = createNewModifier(levelId, "#000000", "#277243", "dots",
                                ThemeTypes.Card);
                        break;
                    }
                    default: {
                        break;
                    }
                }

                if (levelThemeId != null || levelThemeModifierId != null) {
                    DatabaseUtils.sqlPrepareStatement(
                            "UPDATE card_leveltb SET theme_id_FK = ?, modifier_id_FK = ? WHERE level_id_PK = ?",
                            levelThemeId, levelThemeModifierId, levelId);
                }

                weight++;
            }

            updateAllLevelContainCards(deckId);
            updateDeckContainCard(deckId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param userId
     * @return True if successfilly fork the default deck to new user.
     */
    public static boolean initializeForkedDeck(Long userId) {

        Long rootUserID = ((Number) DatabaseUtils.sqlSingleRowStatement(
                "SELECT user_id_PK FROM usertb WHERE username = ?", DatabaseUtils.getRootUsername()).get("user_id_PK"))
                .longValue();

        boolean isRoot = (rootUserID == userId);

        if (!isRoot) {
            SQLResult rootDecks = DatabaseUtils.sqlPrepareStatement(
                    "SELECT deck_id_PK FROM decktb WHERE user_id_FK = ? AND deck_is_public = 1",
                    rootUserID);
            if (rootDecks.isSuccess()) {
                for (Row deck : rootDecks.getData()) {
                    Long rootDeckId = ((Number) deck.get("deck_id_PK")).longValue();
                    if (!forkDeck(rootDeckId, userId)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static Boolean toggleCloneDeck(Long deckId, Long userId) {
        try {
            Row deckRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT * FROM decktb WHERE deck_id_PK = ?", deckId);
            if (deckRow == null)
                return null;

            Boolean currentPerm = (Boolean) deckRow.get("deck_clone_perm");
            Boolean newPerm = !currentPerm;

            DatabaseUtils.sqlPrepareStatement(
                    "UPDATE decktb SET deck_clone_perm = ?, deck_lastest_updated = NOW() WHERE deck_id_PK = ?",
                    newPerm, deckId);

            return newPerm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean cloneDeck(Long deckId, Long userId) {
        try {
            Row deckRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT * FROM decktb WHERE deck_id_PK = ?", deckId);
            if (deckRow == null)
                return false;

            Long newDeckId = Snowflake.nextId();

            Long originalThemeId = ((Number) deckRow.get("theme_id_FK")).longValue();
            Long originalModifierId = ((Number) deckRow.get("modifier_id_FK")).longValue();

            Row themeRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT * FROM themetb WHERE theme_id_PK = ?", originalThemeId);
            Row modifierRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT * FROM theme_modifiertb WHERE modifier_id_PK = ?", originalModifierId);

            createNewDeck(
                    newDeckId,
                    deckRow.get("deck_name") + " (Clone)",
                    (String) deckRow.get("deck_description"),
                    false, null, null, userId);

            Long newThemeId = Snowflake.nextId();
            DatabaseUtils.sqlPrepareStatement(
                    "INSERT INTO themetb (theme_id_PK, theme_name, theme_type, theme_url, deck_id_FK) VALUES (?, ?, ?, ?, ?)",
                    newThemeId,
                    themeRow.get("theme_name") + " (Clone)",
                    themeRow.get("theme_type"),
                    themeRow.get("theme_url"),
                    newDeckId);

            Long newModifierId = Snowflake.nextId();
            DatabaseUtils.sqlPrepareStatement(
                    "INSERT INTO theme_modifiertb (modifier_id_PK, primary_color, secondary_color, card_pattern, deck_id_FK) VALUES (?, ?, ?, ?, ?)",
                    newModifierId,
                    modifierRow.get("primary_color"),
                    modifierRow.get("secondary_color"),
                    modifierRow.get("card_pattern"),
                    newDeckId);

            DatabaseUtils.sqlPrepareStatement(
                    "UPDATE decktb SET theme_id_FK = ?, modifier_id_FK = ? WHERE deck_id_PK = ?",
                    newThemeId, newModifierId, newDeckId);

            List<Row> levels = DatabaseUtils.sqlPrepareStatement(
                    "SELECT * FROM card_leveltb WHERE deck_id_FK = ?", deckId).getData();

            Map<Long, Long> levelMap = new HashMap<>();
            List<Object[]> levelBatch = new ArrayList<>();
            Map<Long, Long> levelThemeMap = new HashMap<>();
            Map<Long, Long> levelModifierMap = new HashMap<>();

            for (Row level : levels) {
                Long oldLevelId = ((Number) level.get("level_id_PK")).longValue();
                Long newLevelId = Snowflake.nextId();
                levelMap.put(oldLevelId, newLevelId);

                // Create independent theme & modifier for this level
                Long levelThemeId = Snowflake.nextId();

                Long levelModifierId = Snowflake.nextId();

                levelThemeMap.put(oldLevelId, levelThemeId);
                levelModifierMap.put(oldLevelId, levelModifierId);

                levelBatch.add(new Object[] {
                        newLevelId,
                        level.get("level_name"),
                        level.get("level_weight"),
                        newDeckId
                });
            }

            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO card_leveltb (level_id_PK, level_name, level_weight, deck_id_FK) VALUES (?, ?, ?, ?)",
                    levelBatch);

            // insert theme and modifier to level
            List<Object[]> themeBatch = new ArrayList<>();
            List<Object[]> modifierBatch = new ArrayList<>();

            for (Row level : levels) {
                Long oldLevelId = ((Number) level.get("level_id_PK")).longValue();
                Long newLevelId = levelMap.get(oldLevelId);

                // Clone theme
                Row oldTheme = null;
                if (level.get("theme_id_FK") != null) {
                    oldTheme = DatabaseUtils.sqlSingleRowStatement(
                            "SELECT * FROM themetb WHERE theme_id_PK = ?", level.get("theme_id_FK"));
                }
                Long newLevelThemeId = levelThemeMap.get(oldLevelId);
                if (oldTheme != null) {
                    themeBatch.add(new Object[] {
                            newLevelThemeId,
                            oldTheme.get("theme_name") + " (Clone)",
                            newLevelId,
                            oldTheme.get("theme_type"),
                            oldTheme.get("theme_url")
                    });
                }

                // Clone modifier
                Row oldModifier = null;
                if (level.get("modifier_id_FK") != null) {
                    oldModifier = DatabaseUtils.sqlSingleRowStatement(
                            "SELECT * FROM theme_modifiertb WHERE modifier_id_PK = ?", level.get("modifier_id_FK"));
                }
                Long newLevelModifierId = levelModifierMap.get(oldLevelId);
                if (oldModifier != null) {
                    modifierBatch.add(new Object[] {
                            newLevelModifierId,
                            oldModifier.get("primary_color"),
                            newLevelId,
                            oldModifier.get("secondary_color"),
                            oldModifier.get("card_pattern")
                    });
                }

                // Update new level with theme/modifier
                DatabaseUtils.sqlPrepareStatement(
                        "UPDATE card_leveltb SET theme_id_FK = ?, modifier_id_FK = ? WHERE level_id_PK = ?",
                        newLevelThemeId, newLevelModifierId, newLevelId);
            }

            if (!themeBatch.isEmpty()) {
                DatabaseUtils.sqlExecuteBatch(
                        "INSERT INTO themetb (theme_id_PK, theme_name, level_id_FK, theme_type, theme_url) VALUES (?, ?, ?, ?, ?)",
                        themeBatch);
            }
            if (!modifierBatch.isEmpty()) {
                DatabaseUtils.sqlExecuteBatch(
                        "INSERT INTO theme_modifiertb (modifier_id_PK, primary_color, level_id_FK, secondary_color, card_pattern) VALUES (?, ?, ?, ?, ?)",
                        modifierBatch);
            }

            List<Row> cards = DatabaseUtils.sqlPrepareStatement(
                    "SELECT * FROM cardtb WHERE level_id_FK IN " +
                            "(SELECT level_id_PK FROM card_leveltb WHERE deck_id_FK = ?)",
                    deckId).getData();

            Map<Long, Long> cardMap = new HashMap<>();
            List<Object[]> cardBatch = new ArrayList<>();

            for (Row card : cards) {
                Long oldCardId = ((Number) card.get("card_id_PK")).longValue();
                Long newCardId = Snowflake.nextId();
                cardMap.put(oldCardId, newCardId);

                cardBatch.add(new Object[] {
                        newCardId,
                        levelMap.get(((Number) card.get("level_id_FK")).longValue()),
                        card.get("card_word")
                });
            }

            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO cardtb (card_id_PK, level_id_FK, card_word) VALUES (?, ?, ?)",
                    cardBatch);

            List<Row> poses = DatabaseUtils.sqlPrepareStatement(
                    "SELECT * FROM postb WHERE card_id_FK IN " +
                            "(SELECT card_id_PK FROM cardtb WHERE level_id_FK IN " +
                            "(SELECT level_id_PK FROM card_leveltb WHERE deck_id_FK = ?))",
                    deckId).getData();

            Map<Long, Long> posMap = new HashMap<>();
            List<Object[]> posBatch = new ArrayList<>();

            for (Row pos : poses) {
                Long oldPosId = ((Number) pos.get("pos_id_PK")).longValue();
                Long newPosId = Snowflake.nextId();
                posMap.put(oldPosId, newPosId);

                posBatch.add(new Object[] {
                        newPosId,
                        cardMap.get(((Number) pos.get("card_id_FK")).longValue()),
                        pos.get("part_of_speech")
                });
            }

            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)",
                    posBatch);

            List<Row> defs = DatabaseUtils.sqlPrepareStatement(
                    "SELECT * FROM definitiontb WHERE pos_id_FK IN " +
                            "(SELECT pos_id_PK FROM postb WHERE card_id_FK IN " +
                            "(SELECT card_id_PK FROM cardtb WHERE level_id_FK IN " +
                            "(SELECT level_id_PK FROM card_leveltb WHERE deck_id_FK = ?)))",
                    deckId).getData();

            List<Object[]> defBatch = new ArrayList<>();
            for (Row def : defs) {
                Long newDefId = Snowflake.nextId();
                defBatch.add(new Object[] {
                        newDefId,
                        posMap.get(((Number) def.get("pos_id_FK")).longValue()),
                        def.get("definition")
                });
            }

            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)",
                    defBatch);

            updateAllLevelContainCards(newDeckId);
            updateDeckContainCard(newDeckId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check ownership of a deck and return type ("owned" or "forked").
     *
     * @param userId
     * @param deckId
     * @return Optional<String> ownershipType
     */
    public static Optional<String> getOwnershipType(Long userId, Long deckId) {
        String sql = """
                    SELECT 'owned' AS type
                    FROM decktb d
                    WHERE d.user_id_FK = ? AND d.deck_id_PK = ?
                    UNION
                    SELECT 'forked' AS type
                    FROM forktb f
                    WHERE f.user_id_FK = ? AND f.deck_id_FK = ?
                    LIMIT 1
                """;

        Row row = DatabaseUtils.sqlSingleRowStatement(sql, userId, deckId, userId, deckId);

        if (row != null && row.get("type") != null) {
            return Optional.of(row.get("type").toString());
        }

        return Optional.empty();
    }

    /**
     * Validate ownership for deck/level/card/pos in a single query.
     *
     * @param userId
     * @param deckId  required
     * @param levelId optional
     * @param cardId  optional
     * @param posId   optional
     * @return true if the user owns (or forked) the deck/item, false otherwise
     */
    public static boolean validateOwnership(Boolean checkFork, Long userId, Long deckId, Long levelId, Long cardId,
            Long posId, Long definitionId) {

        String sql = """
                    SELECT 1
                    FROM decktb d
                    LEFT JOIN card_leveltb cl ON cl.deck_id_FK = d.deck_id_PK
                    LEFT JOIN cardtb c ON c.level_id_FK = cl.level_id_PK
                    LEFT JOIN postb p ON p.card_id_FK = c.card_id_PK
                    LEFT JOIN definitiontb def ON def.pos_id_FK = p.pos_id_PK
                    LEFT JOIN forktb f ON f.deck_id_FK = d.deck_id_PK AND f.user_id_FK = ?
                    WHERE d.deck_id_PK = ?
                      AND (
                          d.user_id_FK = ?
                          OR (? = TRUE AND f.user_id_FK IS NOT NULL)
                      )
                      AND (? IS NULL OR cl.level_id_PK = ?)
                      AND (? IS NULL OR c.card_id_PK = ?)
                      AND (? IS NULL OR p.pos_id_PK = ?)
                      AND (? IS NULL OR def.definition_id_PK = ?)
                    LIMIT 1
                """;

        // Prepare parameters
        Row row = DatabaseUtils.sqlSingleRowStatement(
                sql,
                userId, // for forktb join
                deckId,
                userId, // check ownership
                checkFork, // control fork check
                levelId, levelId,
                cardId, cardId,
                posId, posId,
                definitionId, definitionId);

        return row != null;
    }

    /**
     * 
     * @param userid
     * @return
     */
    public static List<Row> getOwnedDecks(Long userid) {
        String sql = """
                    SELECT d.*,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern
                    FROM decktb d
                    LEFT JOIN themetb t ON d.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON d.modifier_id_FK = m.modifier_id_PK
                    WHERE d.user_id_FK = ?
                """;

        SQLResult ownedDecksList = DatabaseUtils.sqlPrepareStatement(sql, userid);
        List<Row> rows = ownedDecksList.getData();

        Convertors.convertIdsToString(rows, "deck_id_PK", "user_id_FK", "theme_id_FK", "modifier_id_FK");

        return rows;
    }

    /**
     * 
     * @param userid
     * @return
     */
    public static List<Row> getForkedDecks(Long userid) {
        String sql = """
                    SELECT d.*,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern
                    FROM forktb f
                    INNER JOIN decktb d ON f.deck_id_FK = d.deck_id_PK
                    LEFT JOIN themetb t ON d.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON d.modifier_id_FK = m.modifier_id_PK
                    WHERE f.user_id_FK = ?
                """;

        SQLResult forkedDecksList = DatabaseUtils.sqlPrepareStatement(sql, userid);
        List<Row> rows = forkedDecksList.getData();

        Convertors.convertIdsToString(rows, "deck_id_PK", "user_id_FK", "theme_id_FK", "modifier_id_FK");

        return rows;
    }

    /**
     * 
     * @param deckId
     * @return
     */
    public static List<Row> getDeckLevel(Long deckId) {
        String sql = """
                    SELECT cl.*,
                        d.deck_name,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern
                    FROM card_leveltb cl
                    INNER JOIN decktb d ON cl.deck_id_FK = d.deck_id_PK
                    LEFT JOIN themetb t ON cl.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON cl.modifier_id_FK = m.modifier_id_PK
                    WHERE cl.deck_id_FK = ?
                    ORDER BY cl.level_weight
                """;

        SQLResult deckLevelList = DatabaseUtils.sqlPrepareStatement(sql, deckId);
        List<Row> rows = deckLevelList.getData();

        Convertors.convertIdsToString(rows, "deck_id_FK", "level_id_PK", "theme_id_FK", "modifier_id_FK");

        return rows;
    }

    /**
     * Get all cards for a specific deck level
     *
     * @param deckId
     * @param levelId
     * @return List of cards (joined with themes & modifiers from their level)
     */
    public static List<Row> getCardsOfLevel(Long levelId) {
        String sql = """
                    SELECT c.*,
                        cl.level_name, cl.level_weight,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern
                    FROM cardtb c
                    INNER JOIN card_leveltb cl ON c.level_id_FK = cl.level_id_PK
                    LEFT JOIN themetb t ON cl.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON cl.modifier_id_FK = m.modifier_id_PK
                    WHERE cl.level_id_PK = ?
                """;

        SQLResult cardList = DatabaseUtils.sqlPrepareStatement(sql, levelId);
        List<Row> rows = cardList.getData();

        Convertors.convertIdsToString(
                rows,
                "card_id_PK", "level_id_FK", "level_id_PK",
                "theme_id_FK", "modifier_id_FK");

        return rows;
    }

    public static Row getCardInfo(Long cardId) {
        String sql = """
                    SELECT c.card_id_PK, c.card_word,
                        cl.level_name,
                        p.pos_id_PK, p.part_of_speech,
                        d.definition_id_PK, d.definition
                    FROM cardtb c
                    INNER JOIN card_leveltb cl ON c.level_id_FK = cl.level_id_PK
                    INNER JOIN decktb dck ON cl.deck_id_FK = dck.deck_id_PK
                    LEFT JOIN postb p ON c.card_id_PK = p.card_id_FK
                    LEFT JOIN definitiontb d ON p.pos_id_PK = d.pos_id_FK
                    WHERE c.card_id_PK = ?
                    ORDER BY p.pos_id_PK, d.definition_id_PK
                """;

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, cardId);
        List<Row> rows = result.getData();

        if (rows.isEmpty())
            return null;

        Row cardInfo = new Row();
        cardInfo.put("card_id_PK", rows.get(0).get("card_id_PK").toString());
        cardInfo.put("card_word", rows.get(0).get("card_word"));
        cardInfo.put("level_name", rows.get(0).get("level_name"));

        Map<String, Row> posMap = new LinkedHashMap<>();
        for (Row row : rows) {
            if (row.get("pos_id_PK") == null)
                continue;

            String posId = row.get("pos_id_PK").toString();
            Row posRow = posMap.computeIfAbsent(posId, id -> {
                Row newPos = new Row();
                newPos.put("pos_id_PK", id);
                newPos.put("part_of_speech", row.get("part_of_speech"));
                newPos.put("definitions", new ArrayList<Row>());
                return newPos;
            });

            if (row.get("definition_id_PK") != null && row.get("definition") != null) {
                @SuppressWarnings("unchecked")
                List<Row> definitions = (List<Row>) posRow.get("definitions");

                Row defRow = new Row();
                defRow.put("definition_id_PK", row.get("definition_id_PK").toString());
                defRow.put("definition", row.get("definition"));

                definitions.add(defRow);
            }
        }

        cardInfo.put("pos", new ArrayList<>(posMap.values()));
        return cardInfo;
    }

    /**
     * 
     * @param deckId
     * @return All card data inside the deck
     */
    public static List<Row> getAllCardFromDeck(Long deckId) {
        String sql = """
                    SELECT
                        c.card_id_PK,
                        c.card_word,
                        cl.level_name,
                        cl.level_id_PK,
                        cl.deck_id_FK,
                        t.theme_url,
                        m.primary_color,
                        m.secondary_color,
                        m.card_pattern
                    FROM cardtb c
                    INNER JOIN card_leveltb cl ON c.level_id_FK = cl.level_id_PK
                    LEFT JOIN themetb t ON cl.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON cl.modifier_id_FK = m.modifier_id_PK
                    WHERE cl.deck_id_FK = ?
                """;

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, deckId);
        List<Row> rows = result.getData();

        Convertors.convertIdsToString(rows, "card_id_PK", "level_id_PK", "deck_id_FK");

        return rows;
    }

    public static List<Row> getPublicDecks(Long userId) {
        // Also include upvote and downvote count from reviewtb

        String sql = """
                    SELECT d.*,
                        t.theme_name, t.theme_type, t.theme_url,
                        m.primary_color, m.secondary_color, m.card_pattern,
                        u.username AS owner_username,
                        (SELECT COUNT(*) FROM reviewtb r WHERE r.deck_id_FK = d.deck_id_PK AND r.review_action = 1) AS upvotes,
                        (SELECT COUNT(*) FROM reviewtb r WHERE r.deck_id_FK = d.deck_id_PK AND r.review_action = 2) AS downvotes,
                        CAST((SELECT r.review_action FROM reviewtb r WHERE r.deck_id_FK = d.deck_id_PK AND r.user_id_FK = ?) AS UNSIGNED) AS user_review_action
                    FROM decktb d
                    LEFT JOIN themetb t ON d.theme_id_FK = t.theme_id_PK
                    LEFT JOIN theme_modifiertb m ON d.modifier_id_FK = m.modifier_id_PK
                    LEFT JOIN usertb u ON d.user_id_FK = u.user_id_PK
                    WHERE d.deck_is_public = 1 AND d.user_id_FK != ?
                    ORDER BY d.deck_created_date DESC
                """;

        SQLResult publicDecksList = DatabaseUtils.sqlPrepareStatement(sql, userId, userId);
        List<Row> rows = publicDecksList.getData();

        Convertors.convertIdsToString(rows, "deck_id_PK", "user_id_FK", "theme_id_FK", "modifier_id_FK");

        return rows;
    }

    public static Long getDeckIdFromCardId(Long cardId) {
        String sql = """
                    SELECT dck.deck_id_PK
                    FROM cardtb c
                    INNER JOIN card_leveltb cl ON c.level_id_FK = cl.level_id_PK
                    INNER JOIN decktb dck ON cl.deck_id_FK = dck.deck_id_PK
                    WHERE c.card_id_PK = ?
                """;
        Row res = DatabaseUtils.sqlSingleRowStatement(sql, cardId);
        return ((Number) res.get("deck_id_PK")).longValue();
    }

    /* -------------------------- */
    /* ----- CREATE Methods ----- */
    /* -------------------------- */

    /**
     * 
     * @param themeId
     * @param themeName
     * @param themeType
     * @param themeURL
     */
    public static Long createNewTheme(Long targetId, String themeName, ThemeTypes themeType, String themeURL) {
        Long themeId = Snowflake.nextId();

        switch (themeType) {
            case Card:
                DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO themetb (theme_id_PK, theme_name, level_id_FK, theme_type, theme_url) VALUES (?, ?, ?, ?, ?)",
                        themeId, themeName, targetId, themeType.getValue(), themeURL);
                break;
            case Deck:
                DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO themetb (theme_id_PK, theme_name, deck_id_FK, theme_type, theme_url) VALUES (?, ?, ?, ?, ?)",
                        themeId, themeName, targetId, themeType.getValue(), themeURL);
                break;
            default:
                break;
        }
        return themeId;
    }

    /**
     * 
     * @param primaryColor
     * @param secondaryColor
     * @param pattern
     * @param themeType
     * @return
     */
    public static Long createNewModifier(Long targetId, String primaryColor, String secondaryColor, String pattern,
            ThemeTypes themeType) {
        Long modifierId = Snowflake.nextId();
        switch (themeType) {
            case Card:
                DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO theme_modifiertb (modifier_id_PK, primary_color, level_id_FK, secondary_color, card_pattern) VALUES (?, ?, ?, ?, ?)",
                        modifierId, primaryColor, targetId, secondaryColor, pattern);
                break;
            case Deck:
                DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO theme_modifiertb (modifier_id_PK, primary_color, deck_id_FK, secondary_color) VALUES (?, ?, ?, ?)",
                        modifierId, primaryColor, targetId, secondaryColor);
                break;
            default:
                break;
        }
        return modifierId;
    }

    /**
     * 
     * @param deckId
     * @param name
     * @param description
     * @param isPublic
     * @param userId
     */
    public static Long createNewDeck(
            Long deckId,
            String name,
            String description, Boolean isPublic,
            Long themeId, Long modifierId,
            Long userId) {

        String sql = """
                INSERT INTO decktb
                (deck_id_PK, deck_name, deck_description, deck_is_public, theme_id_FK, modifier_id_FK, user_id_FK)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        DatabaseUtils.sqlSingleRowStatement(
                sql,
                deckId,
                name, description, isPublic,
                themeId, modifierId, userId);

        return deckId;
    }

    /**
     * 
     * @param levelId
     * @param levelName
     * @param levelWeight
     * @param deckId
     */
    public static void createNewLevel(Long levelId, String levelName, int levelWeight, Long themeId, Long modifierId,
            Long deckId) {
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO card_leveltb " +
                        "(level_id_PK, level_name, level_weight, theme_id_FK, modifier_id_FK, deck_id_FK) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                levelId != null ? levelId : Snowflake.nextId(), levelName, levelWeight,
                themeId, modifierId, deckId);
    }

    /**
     * 
     * @param cardId
     * @param levelId
     * @param cardName
     */
    public static void createNewCard(Long cardId, Long levelId, String cardName) {
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO cardtb (card_id_PK, level_id_FK, card_word) VALUES (?, ?, ?)",
                cardId != null ? cardId : Snowflake.nextId(), levelId, cardName);
        updateLevelContainCard(levelId);
    }

    /**
     * 
     * @param posId
     * @param cardId
     * @param pos
     */
    public static void createNewPartOfSpeech(Long posId, Long cardId, String pos) {
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)",
                posId != null ? posId : Snowflake.nextId(), cardId, pos);
    }

    /**
     * 
     * @param definitionId
     * @param posId
     * @param definition
     */
    public static void createNewDefinition(Long definitionId, Long posId, String definition) {
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)",
                definitionId != null ? definitionId : Snowflake.nextId(), posId,
                definition != null ? definition : "New Definition");
    }

    // if targetDeck doesn't in forktb
    // add deck_id to forktb first
    // otherwise add user_id
    public static boolean forkDeck(Long deck_id, Long user_id) {
        // Prevent forking own deck
        Row ownDeck = DatabaseUtils.sqlSingleRowStatement(
                "SELECT deck_id_PK FROM decktb WHERE deck_id_PK = ? AND user_id_FK = ?", deck_id, user_id);
        if (ownDeck != null) {
            return false;
        }

        // Check if deck is public
        Row deckInfo = DatabaseUtils.sqlSingleRowStatement(
                "SELECT deck_is_public FROM decktb WHERE deck_id_PK = ?", deck_id);
        if (deckInfo == null || !(Boolean) deckInfo.get("deck_is_public")) {
            return false;
        }

        // Check if already forked
        Row forkExists = DatabaseUtils.sqlSingleRowStatement(
                "SELECT * FROM forktb WHERE deck_id_FK = ? AND user_id_FK = ?", deck_id, user_id);
        if (forkExists != null) {
            return false;
        }

        // Insert fork record
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO forktb (deck_id_FK, user_id_FK) VALUES (?, ?)", deck_id, user_id);
        return true;
    }

    public static Boolean createPOS(Long cardId, String posName) {
        Long posId = Snowflake.nextId();
        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)", posId, cardId, posName);
        if (!result.isSuccess()) {
            return false;
        }
        return true;
    }

    public static Boolean createDefinition(Long posId, String definitionText) {
        Long definitionId = Snowflake.nextId();
        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)",
                definitionId, posId, definitionText);
        if (!result.isSuccess()) {
            return false;
        }
        return true;
    }

    /* -------------------------- */
    /* ----- UPDATE Methods ----- */
    /* -------------------------- */

    public static Boolean updateCard(Long cardId, String cardWord) {
        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE cardtb SET card_word = ? WHERE card_id_PK = ?", cardWord, cardId);
        if (!result.isSuccess()) {
            return false;
        }
        return true;
    }

    public static Boolean toggleDeckPublic(Long deckId) {
        Row deck = DatabaseUtils.sqlSingleRowStatement(
                "SELECT deck_is_public FROM decktb WHERE deck_id_PK = ?", deckId);
        if (deck != null) {
            boolean currentStatus = (Boolean) deck.get("deck_is_public");
            DatabaseUtils.sqlPrepareStatement(
                    "UPDATE decktb SET deck_is_public = ? WHERE deck_id_PK = ?",
                    !currentStatus, deckId);
            return !currentStatus;
        }
        return null;
    }

    public static String addReviewToDeck(Long deck_id, Long user_id, int review_action) {
        // review_action: 1 = upvote, 2 = downvote, 0 = neutral/remove vote

        Row existingReview = DatabaseUtils.sqlSingleRowStatement(
                "SELECT * FROM reviewtb WHERE deck_id_FK = ? AND user_id_FK = ?", deck_id, user_id);
        if (existingReview != null) {
            if (review_action == 0) {
                DatabaseUtils.sqlPrepareStatement(
                        "DELETE FROM reviewtb WHERE deck_id_FK = ? AND user_id_FK = ?", deck_id, user_id);
                return "Your review has been removed.";
            } else {
                DatabaseUtils.sqlPrepareStatement(
                        "UPDATE reviewtb SET review_action = ? WHERE deck_id_FK = ? AND user_id_FK = ?",
                        review_action, deck_id, user_id);
                return "Your review has been updated.";
            }
        } else {
            if (review_action == 0) {
                return "No existing review to remove.";
            } else {
                DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO reviewtb (deck_id_FK, user_id_FK, review_action) VALUES (?, ?, ?)",
                        deck_id, user_id, review_action);
                return "Your review has been added.";
            }
        }
    }

    public static String updateDeck(Long deck_id, String deckName, String deckDescription, String primaryColor,
            String secondaryColor, boolean deckIsPublic,
            boolean clonePerm) {
        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE decktb SET deck_name = ?, deck_description = ?, deck_is_public = ?, deck_clone_perm = ?, deck_lastest_updated = NOW() WHERE deck_id_PK = ?",
                deckName, deckDescription, deckIsPublic, clonePerm, deck_id);

        if (primaryColor != null || secondaryColor != null) {
            Row deckRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT modifier_id_FK FROM decktb WHERE deck_id_PK = ?", deck_id);
            if (deckRow != null && deckRow.get("modifier_id_FK") != null) {
                Long modifierId = ((Number) deckRow.get("modifier_id_FK")).longValue();
                DatabaseUtils.sqlPrepareStatement(
                        "UPDATE theme_modifiertb SET primary_color = ?, secondary_color = ? WHERE modifier_id_PK = ?",
                        primaryColor, secondaryColor, modifierId);
            }
        }

        if (result.isSuccess()) {
            return "This Deck has been updated successfully";
        }

        return "Deck was not found";
    }

    public static String updateLevel(Long levelId, String levelValue, int levelWeight, String primaryColor,
            String secondaryColor) {

        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE card_leveltb SET level_weight = ?, level_name = ? WHERE level_id_PK = ?",
                levelWeight, levelValue, levelId);

        if (primaryColor != null || secondaryColor != null) {
            Row levelRow = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT modifier_id_FK FROM card_leveltb WHERE level_id_PK = ?", levelId);
            if (levelRow != null && levelRow.get("modifier_id_FK") != null) {
                Long modifierId = ((Number) levelRow.get("modifier_id_FK")).longValue();
                DatabaseUtils.sqlPrepareStatement(
                        "UPDATE theme_modifiertb SET primary_color = ?, secondary_color = ? WHERE modifier_id_PK = ?",
                        primaryColor, secondaryColor, modifierId);
            }
        }
        if (result.isSuccess()) {
            return "This level has been updated successfully";
        }

        return "Level was not found";
    }

    public static String updatePos(Long pos_id, String part_of_speech) {

        SQLResult result = DatabaseUtils.sqlPrepareStatement("UPDATE postb SET part_of_speech = ? WHERE pos_id_PK = ?",
                part_of_speech, pos_id);

        if (result.isSuccess()) {
            return "Part of speech has been updated successfully";
        }

        return "Part of speech was not found";
    }

    public static String updateDefinition(Long definition_id, String new_definition) {

        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE definitiontb SET definition = ? WHERE definition_id_PK = ?",
                new_definition, definition_id);

        if (result.isSuccess()) {
            return "Definition has been updated successfully";
        }

        return "Definition was not found";
    }

    /**
     * Summarize all cards in current deck
     *
     * @param deck_id
     * @return number in deck_contain_card from decktb
     */
    public static void updateDeckContainCard(Long deckId) {
        String sql = """
                    UPDATE decktb d
                    SET deck_contain_card = (
                        SELECT SUM(cl.level_contain_card)
                        FROM card_leveltb cl
                        WHERE cl.deck_id_FK = d.deck_id_PK
                    )
                    WHERE d.deck_id_PK = ?
                """;

        DatabaseUtils.sqlPrepareStatement(sql, deckId);
    }

    public static void updateLevelContainCard(Long levelId) {
        String sql = """
                    UPDATE card_leveltb cl
                    SET level_contain_card = (
                        SELECT COUNT(*)
                        FROM cardtb c
                        WHERE c.level_id_FK = cl.level_id_PK
                    )
                    WHERE cl.level_id_PK = ?
                """;

        DatabaseUtils.sqlPrepareStatement(sql, levelId);

        Row row = DatabaseUtils.sqlSingleRowStatement(
                "SELECT deck_id_FK FROM card_leveltb WHERE level_id_PK = ?", levelId);
        if (row != null && row.get("deck_id_FK") != null) {
            Long deckId = ((Number) row.get("deck_id_FK")).longValue();

            updateDeckContainCard(deckId);
        }
    }

    private static void updateAllLevelContainCards(Long deckId) {
        String sql = """
                    UPDATE card_leveltb cl
                    SET level_contain_card = (
                        SELECT COUNT(*)
                        FROM cardtb c
                        WHERE c.level_id_FK = cl.level_id_PK
                    )
                    WHERE cl.deck_id_FK = ?
                """;

        DatabaseUtils.sqlPrepareStatement(sql, deckId);
    }

    public static Row getLevelColor(Long levelId) {
        // Get pricolor and secolor from theme_modifiertb
        String sql = """
                    SELECT m.primary_color, m.secondary_color
                    FROM card_leveltb cl
                    LEFT JOIN theme_modifiertb m ON cl.modifier_id_FK = m.modifier_id_PK
                    WHERE cl.level_id_PK = ?
                """;
        Row levelInfo = DatabaseUtils.sqlSingleRowStatement(sql, levelId);
        return levelInfo;
    }

    // -------------- //
    // DELETE METHODS //
    // -------------- //

    public static String deletePos(Long pos_id) {
        SQLResult result = DatabaseUtils
                .sqlPrepareStatement("DELETE FROM postb WHERE pos_id_PK = ?", pos_id);
        if (result.isSuccess()) {
            return "Part of Speech has been deleted successfully";
        }
        return "Part of Speech was not found on current card";
    }

    public static String deleteDefinition(Long definition_id) {
        SQLResult result = DatabaseUtils
                .sqlPrepareStatement("DELETE FROM definitiontb WHERE definition_id_PK = ?", definition_id);
        if (result.isSuccess()) {
            return "Definition has been deleted successfully";
        }
        return "Definition was not found";
    }

    public static Boolean deleteDeckCascade(Long deckId, Long userId) {
        SQLResult res = DatabaseUtils.sqlPrepareStatement(
                "DELETE FROM decktb WHERE deck_id_PK = ? AND user_id_FK = ?",
                deckId, userId);
        return res.getAffectedRow() > 0;
    }

    public static Boolean deleteLevelCascade(Long levelId, Long deckId) {
        SQLResult res = DatabaseUtils.sqlPrepareStatement(
                "DELETE FROM card_leveltb WHERE level_id_PK = ? AND deck_id_FK = ?",
                levelId, deckId);
        updateDeckContainCard(deckId);
        return res.getAffectedRow() > 0;
    }

    public static Boolean deleteCardCascade(Long cardId, Long levelId) {
        SQLResult res = DatabaseUtils.sqlPrepareStatement(
                "DELETE FROM cardtb WHERE card_id_PK = ?",
                cardId);
        updateLevelContainCard(levelId);
        return res.getAffectedRow() > 0;
    }

    public static Boolean unForkDeck(Long deckId, Long userId) {
        SQLResult res = DatabaseUtils.sqlPrepareStatement(
                "DELETE FROM forktb WHERE deck_id_FK = ? AND user_id_FK = ?",
                deckId, userId);
        return res.getAffectedRow() > 0;
    }

}

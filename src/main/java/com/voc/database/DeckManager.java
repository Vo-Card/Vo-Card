package com.voc.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.voc.server.Snowflake;
import com.voc.utils.Row;

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
        createNewDeck(deckId, "Default", "This is a VoCard official default deck.", true, rootUserID);

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
            List<Object[]> cardBatch = new ArrayList<>();
            List<Object[]> posBatch = new ArrayList<>();
            List<Object[]> defBatch = new ArrayList<>();

            int weight = 1;
            for (Map.Entry<String, Map<String, Map<String, List<String>>>> levelData : defaultDeck.entrySet()) {
                Long levelId = Snowflake.nextId();
                levelBatch.add(new Object[] { levelId, weight, levelData.getKey(), deckId });
                weight++;

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
            }

            // Execute batches
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO card_leveltb (level_id_PK, level_weight, level_name, deck_id_FK) VALUES (?, ?, ?, ?)",
                    levelBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO cardtb (card_id_PK, level_id_FK, card_word) VALUES (?, ?, ?)", cardBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)", posBatch);
            DatabaseUtils.sqlExecuteBatch(
                    "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)", defBatch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param userId
     * @return True if successfilly fork the default deck to new user.
     */
    public static boolean initializeForkedDeck(Long userId){
        
        Long rootUserID = ((Number) DatabaseUtils.sqlSingleRowStatement("SELECT user_id_PK FROM usertb WHERE username = ?", DatabaseUtils.getRootUsername()).get("user_id_PK")).longValue();

        boolean isRoot = (rootUserID == userId);
        
        if (!isRoot){
            SQLResult rootDecks = DatabaseUtils.sqlPrepareStatement(
                "SELECT deck_id_PK FROM decktb WHERE user_id_FK = ?",
                rootUserID
                );
            if(rootDecks.isSuccess()){
                for (Row deck: rootDecks.getData()){
                    Long rootDeckId = ((Number) deck.get("deck_id_PK")).longValue();
                    if(!forkDeck(rootDeckId, userId)){
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param deckId
     * @param name
     * @param description
     * @param isPublic
     * @param userId
     */
    public static void createNewDeck(
        Long deckId, String name,
        String description, Boolean isPublic,
        Long userId) {

        String sql ="INSERT INTO decktb " +
                    "(deck_id_PK, deck_name, deck_is_public, user_id_FK) " + 
                    "VALUES (?, ?, ?, ?)";
        
        DatabaseUtils.sqlSingleRowStatement(sql, deckId != null ? deckId : Snowflake.nextId(), name, isPublic, userId);
    }



    /**
     * 
     * @param levelId
     * @param levelName
     * @param levelWeight
     * @param deckId
     */
    public static void createNewLevel(Long levelId, String levelName, int levelWeight, Long deckId) {
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO card_leveltb (level_id_PK, level_name, level_weight, deck_id_FK) VALUES (?, ?, ?, ?)",
                levelId != null ? levelId : Snowflake.nextId(), levelName, levelWeight, deckId);
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

    /**
     * 
     * @param userid
     * @return
     */
    public static List<Row> getOwnedDecks(Long userid) {
        SQLResult deckList = DatabaseUtils.sqlPrepareStatement("SELECT * FROM decktb WHERE user_id_FK = ? ", userid);
        return deckList.getData();
    }

    /**
     * 
     * @param userid
     * @return
     */
    public static List<Row> getForkedDecks(Long userid) {
        SQLResult forkedDecksList = DatabaseUtils.sqlPrepareStatement("SELECT deck_id_FK FROM forktb WHERE user_id_fk = ?", userid);
        List<Row> totalDecks = new ArrayList<>();
        
        if(forkedDecksList.isSuccess()){
            
            List<Row> forkedDeck = forkedDecksList.getData();
            for (Row forkedDeckData : forkedDeck){
                Long deckId = ((Number) forkedDeckData.get("deck_id_FK")).longValue();
                Row deckData = DatabaseUtils.sqlSingleRowStatement("SELECT * FROM decktb WHERE deck_id_PK = ?", deckId);
                totalDecks.add(deckData);
            }
        }

        return totalDecks;
    }

    // if targetDeck doesn't in forktb
    // add deck_id to forktb first
    // otherwise add user_id
    public static boolean forkDeck(Long deck_id, Long user_id) {
        // Check if they're trying to fork their own deck
        Row isOwnDeck = DatabaseUtils.sqlSingleRowStatement("SELECT dekc_id_PK FROM decktb WHERE user_id_FK = ?", user_id);

        if (isOwnDeck != null)
            return false;

        // Check if deck already paired
        SQLResult deckFork = DatabaseUtils
                .sqlPrepareStatement("SELECT * FROM forktb WHERE deck_id_FK = ? AND user_id_FK = ?", deck_id, user_id);

        if (deckFork.getData().isEmpty()) {
            DatabaseUtils.sqlPrepareStatement("INSERT INTO forktb (deck_id_FK, user_id_FK) VALUES (?, ?)", deck_id,
                    user_id);
            return true;
        }
        // not sure there anything more
        return false;
    }

    public static String deleteDeck(Long deck_id, Long user_id) {
        Row deleteDeck = DatabaseUtils.sqlSingleRowStatement(
                "SELECT * FROM decktb WHERE deck_id_PK = ? AND user_id_FK = ?", deck_id, user_id);

        if (deleteDeck != null) {
            DatabaseUtils.sqlSingleRowStatement("DELETE FROM decktb WHERE deck_id_PK = ? AND user_id_FK = ?", deck_id,
                    user_id);
            return "This Deck has been deleted successfully";
        }
        return "Deck was not found";
    }

    public static String updateDeck(Long deck_id, String deckName, String deckDescription, boolean deckIsPublic) {
        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE decktb SET deck_name = ?, deck_description = ?, deck_is_public = ? WHERE deck_id_PK = ?",
                deckName, deckDescription, deckIsPublic,
                deck_id);

        if (result.isSuccess()) {
            return "This Deck has been updated successfully";
        }

        return "Deck was not found";
    }

    public static String updateLevel(Long level_id, int level_weight, String level_name) {

        SQLResult result = DatabaseUtils.sqlPrepareStatement(
                "UPDATE card_leveltb SET level_weight = ?, level_name = ? WHERE level_id_PK = ?",
                level_weight, level_name, level_id);
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

        SQLResult result = DatabaseUtils.sqlPrepareStatement("UPDATE definition SET definition = ? WHERE = ?",
                new_definition, definition_id);

        if (result.isSuccess()) {
            return "Definition has been updated successfully";
        }

        return "Definition was not found";
    }

    public static String deleteLevel(Long level_id, Long deck_id) {

        SQLResult result = DatabaseUtils
                .sqlPrepareStatement("DELETE FROM card_leveltb WHERE level_id_PK = ? AND deck_id_FK = ? ", level_id,
                        deck_id);
        if (result.isSuccess()) {
            return "Level has been deleted successfully";
        }
        return "Level was not found";
    }

    public static String deleteCard(Long card_id) {
        SQLResult result = DatabaseUtils
                .sqlPrepareStatement("DELETE FROM cardtb WHERE card_id_PK = ?", card_id);
        if (result.isSuccess()) {
            return "Card has been deleted successfully";
        }
        return "Card was not found";
    }

    public static String deletePos(Long pos_id, Long card_id) {
        SQLResult result = DatabaseUtils
                .sqlPrepareStatement("DELETE FROM postb WHERE pos_id_PK = ? AND card_id_FK = ?", pos_id, card_id);
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
}

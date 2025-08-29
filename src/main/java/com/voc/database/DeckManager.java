package com.voc.database;

import static com.voc.utils.AnsiColor.TAG_SUCCESS;

import java.io.InputStream;
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

    public static void forceDefaultDeck() {
        // Force Create Default Deck
        Row temp = DatabaseUtils.sqlSingleRowStatement("SELECT deck_id_PK FROM decktb WHERE deck_id_PK = ? ", 1);

        if (temp == null) {
            DatabaseUtils.sqlSingleRowStatement(
                    "INSERT INTO decktb (deck_name,deck_is_public, user_id_FK ) VALUES (?,?,?)",
                    "Default", 1, 1);
            System.out.println("Create Deck complete");
        }
    }

    public static void createNewDeck(Long deckId, String name, String description, Boolean isPublic, Long userId) {
        DatabaseUtils.sqlSingleRowStatement(
                "INSERT INTO decktb (deck_id_PK, deck_name, deck_is_public, user_id_FK) VALUES (?, ?, ?, ?)",
                deckId != null ? deckId : Snowflake.nextId(), name, isPublic, userId);
    }

    public static void createNewLevel(Long levelId, String levelName, int levelWeight, Long deckId){
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO card_leveltb (level_id_PK, level_name, level_weight, deck_id_FK) VALUES (?, ?, ?, ?)",
                levelId != null ? levelId : Snowflake.nextId(), levelName, levelWeight, deckId);
    }

    public static void createNewCard(Long cardId, Long levelId, String cardName){
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO cardtb (card_id_PK, level_id_FK, card_word) VALUES (?, ?, ?)",
                cardId != null ? cardId : Snowflake.nextId(), levelId, cardName);
    }

    public static void createNewPartOfSpeech(Long posID, Long cardId, String pos){
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO postb (pos_id_PK, card_id_FK, part_of_speech) VALUES (?, ?, ?)",
                posID != null ? posID : Snowflake.nextId(), cardId, pos);
    }
    
    public static void createNewDefinition(Long definitionId, Long posID, String definition){
        DatabaseUtils.sqlPrepareStatement(
                "INSERT INTO definitiontb (definition_id_PK, pos_id_FK, definition) VALUES (?, ?, ?)",
                definitionId != null ? definitionId : Snowflake.nextId(), posID, definition);
    }

    /**
     * Initialize the defaultdeck to the root user of the project.
     */
    public static void initializeDeckTable() {
        long deckCount = ((Number) DatabaseUtils.sqlSingleRowStatement("SELECT COUNT(*) FROM decktb").get("COUNT(*)"))
                .longValue();

        if (deckCount == 0) {
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

                int weight = 1;

                for (Map.Entry<String, Map<String, Map<String, List<String>>>> levelData : defaultDeck.entrySet()) {
                    String levelName = levelData.getKey();
                    Map<String, Map<String, List<String>>> levelContent = levelData.getValue();

                    Long levelId = Snowflake.nextId();
                    createNewLevel(levelId, levelName, weight, deckId);
                    weight = weight + 1;

                    for (Map.Entry<String, Map<String, List<String>>> cardData : levelContent.entrySet()) {
                        String cardName = cardData.getKey();
                        Map<String, List<String>> cardContent = cardData.getValue();

                        Long cardId = Snowflake.nextId();

                        createNewCard(cardId, levelId, cardName);

                        for (Map.Entry<String, List<String>> posData : cardContent.entrySet()){
                            String partOfSpeech = posData.getKey();
                            List<String> definitions = posData.getValue();

                            Long posId = Snowflake.nextId();
                            createNewPartOfSpeech(posId, cardId, partOfSpeech);

                            for (String def : definitions) {
                                Long defId = Snowflake.nextId();
                                createNewDefinition(defId, posId, def);
                            }
                        }
                    }
                }

                System.out.println(TAG_SUCCESS + "Create default complete");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Check Deck_ID that contain User_ID_FK For loading in decks.jsp
    // select * FROM DECK_ID WHERE USER_id_FK = {user_id};
    public static List<Row> deckLoader(long userid) {
        SQLResult deckList = DatabaseUtils.sqlPrepareStatement("SELECT * FROM decktb WHERE user_id_FK = ? ", userid);

        System.out.println(deckList);
        return deckList.getData();
    }
}

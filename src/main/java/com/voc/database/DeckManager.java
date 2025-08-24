package com.voc.database;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voc.helper.Row;

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
 * 
 * @author Zartex
 * @author Krittitee
 * @version 0.0.1a
 * @since 2025-08-23
 */
public class DeckManager {

    static {
        initializeDeckTable();
    }

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

    // not complete yet
    public static void createDeck(String name, String description, Long user_id) {
        DatabaseUtils.sqlSingleRowStatement(
                "INSERT INTO decktb (deck_name,deck_description, user_id_FK) VALUES (?,?,?,?)", name,
                description, user_id);
    }

    /**
     * Initialize the defaultdeck to the root user of the project.
     */
    private static void initializeDeckTable() {
        Long rootUserID  = ((Number) DatabaseUtils.sqlSingleRowStatement(
                        "SELECT user_id_PK FROM usertb WHERE username = ?", "vocard").get("user_id_PK")).longValue();

        Row rootDefaultDeck = DatabaseUtils.sqlSingleRowStatement("SELECT deck_id_PK FROM decktb WHERE deck_id_PK = ?", rootUserID);

        if (rootDefaultDeck == null) {
            DatabaseUtils.sqlSingleRowStatement(
                    "INSERT INTO decktb (deck_name,deck_is_public, user_id_FK ) VALUES (?,?,?)",
                    "Default", 1, 1);
            System.out.println("Create Deck complete");
            ObjectMapper mapper = new ObjectMapper();
    
            //Get data from the defaultdeck json
            try (InputStream input = DeckManager.class.getClassLoader()
                    .getResourceAsStream("datasets/default_deck_sample.json")) {
    
                if (input == null) throw new RuntimeException("File not found!");
    
                Map<String, Map<String, Map<String, Map<String, List<String>>>>> rootJson =
                        mapper.readValue(input, new TypeReference<>() {});
    
                Map<String, Map<String, Map<String, List<String>>>> defaultDeck = rootJson.get("default");
                
                int weight = 1;
    
                for (Map.Entry<String, Map<String, Map<String, List<String>>>> entry : defaultDeck.entrySet()) {
                    String level = entry.getKey();
                    Map<String, Map<String, List<String>>> levelData = entry.getValue();
                    DatabaseUtils.sqlPrepareStatement(
                        "INSERT INTO card_leveltb (level_weight, level_name, deck_id_FK) VALUES (?, ?, ?)",
                        weight, level, 1
                    );
                    Long level_id  = ((Number) DatabaseUtils.sqlSingleRowStatement(
                        "SELECT level_id_PK FROM card_leveltb WHERE level_weight = ? AND level_name = ?", 
                        weight, level).get("level_id_PK")).longValue();
                    weight = weight + 1;
    
    
                    for (Map.Entry<String, Map<String, List<String>>> wordData : levelData.entrySet()){
                        String word = wordData.getKey();
                        Map<String, List<String>> wordContent = wordData.getValue();
    
                        String wordContentJSON = mapper.writeValueAsString(wordContent);
                        
                        DatabaseUtils.sqlPrepareStatement(
                            "INSERT INTO cardtb (card_word, card_content, level_id_FK, deck_id_FK) VALUES (?, ?, ?, ?)", 
                            word, wordContentJSON, level_id, 1);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

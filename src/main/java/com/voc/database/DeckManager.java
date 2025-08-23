package com.voc.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voc.database.DatabaseUtils;
import com.voc.helper.Row;
import com.voc.handler.Formatter;

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

    private static Map<String, Object> defaultDeck = new HashMap<>();

    static {
        // This block runs when the class is loaded
        System.out.println("Loading default dataset at class load...");

        try (InputStream input = DeckManager.class.getClassLoader()
                .getResourceAsStream("datasets/default_deck_sample.json")) {
            if (input == null) {
                throw new RuntimeException("default_deck_sample.json not found!");
            }
            ObjectMapper mapper = new ObjectMapper();
            defaultDeck = mapper.readValue(input, HashMap.class);
            System.out.println("Default dataset loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public static void createDeck(String name, String description) {
        DatabaseUtils.sqlSingleRowStatement(
                "INSERT INTO decktb (deck_name,deck_description,deck_is_public, user_id_FK) VALUES (?,?,?,?)", name,
                description, 0, 1);
    }

    // FIXME: <get words for card_words >
    public static void createCard(HashMap<String, Object> stack) {
        if (stack == null || stack.isEmpty()) {
            System.out.println("Using default deck as provided deck is empty or null.");
            stack = new HashMap<>(defaultDeck);
        }

        HashMap<String, Object> currentDepth = stack;

        // Randomize decks
        ArrayList<String> decks = new ArrayList<>(currentDepth.keySet());
        currentDepth = (HashMap<String, Object>) currentDepth.get(decks);

        System.out.println(currentDepth);

    }
}

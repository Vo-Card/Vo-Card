package com.voc.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Formatter {

    private static Map<String, Object> defaultDeck = new HashMap<>();

    static {
        // This block runs when the class is loaded
        System.out.println("Loading default dataset at class load...");

        try (InputStream input = Formatter.class.getClassLoader()
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

    private static Set<String> getKeysFromObject(Map<String, Object> obj) {
        return obj.keySet();
    }

    public static HashMap<String, Object> formatDeck(String data) {
        HashMap<String, Object> formattedDeck = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            formattedDeck = mapper.readValue(data, HashMap.class);
            System.out.println("Deck formatted successfully.");
        } catch (IOException e) {
            System.err.println("Error formatting deck: " + e.getMessage());
        }
        return formattedDeck;
    }

    public void printDeck(HashMap<String, Object> deck, int depth) {
        if (deck == null || deck.isEmpty()) {
            deck = new HashMap<>(defaultDeck);
            System.out.println("Using default deck as provided deck is empty or null.");
        }

        for (Map.Entry<String, Object> entry : deck.entrySet()) {
            System.out.println(" ".repeat(depth * 2) + entry.getKey() + ":");
            if (entry.getValue() instanceof Map) {
                printDeck((HashMap<String, Object>) entry.getValue(), depth + 1);
            } else if (entry.getValue() instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) entry.getValue();
                System.out.println(" ".repeat((depth + 1) * 2) + "ArrayList of size: " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(" ".repeat((depth + 2) * 2) + "Item " + i + ": " + list.get(i));
                }
            } else if (entry.getValue() instanceof String) {
                System.out.println(" ".repeat((depth + 1) * 2) + "String: " + entry.getValue());
            } else {
                System.out.println(" ".repeat((depth + 1) * 2) + "Value: " + entry.getValue());
            }
        }
    }

    public void getDefinition(String word, String deck, HashMap<String, Object> customDeck) {
        if (customDeck == null || customDeck.isEmpty()) {
            System.out.println("Using default deck as custom deck is empty or null.");
            customDeck = new HashMap<>(defaultDeck);
        }

    }

    public void randomizeWordsFromeDeck(HashMap<String, Object> deck) {
        if (deck == null || deck.isEmpty()) {
            System.out.println("Using default deck as provided deck is empty or null.");
            deck = new HashMap<>(defaultDeck);
        }

        // Randomize categories
        List<String> category = new ArrayList<>();
        for (Map.Entry<String, Object> entry : deck.entrySet()) {
            if (entry.getValue() instanceof Map) {
                category = new ArrayList<>(getKeysFromObject((Map<String, Object>) entry.getValue()));
                System.out.println(category);
            }
        }
        String randomed_category = category.get((int) (Math.random() * category.size()));

        System.out.println("Randomized categories: " + randomed_category);
        for (Map.Entry<String, Object> entry : deck.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> subDeck = (Map<String, Object>) entry.getValue();
                List<String> words = new ArrayList<>(subDeck.keySet());
                Collections.shuffle(words);
                System.out.println("Randomized words in category '" + entry.getKey() + "': " + words);
            }
        }
        System.out.println("Cards randomized successfully.");
    }
}

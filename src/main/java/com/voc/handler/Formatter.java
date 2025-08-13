package com.voc.handler;

import com.voc.handler.deckConfig;

import java.util.Scanner; // temporary

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static String randomArray(ArrayList<String> arr) {
        return arr.get((int) (Math.random() * arr.size()));
    }

    public void randomizeWordsFromeDeck(HashMap<String, Object> stack) {
        if (stack == null || stack.isEmpty()) {
            System.out.println("Using default deck as provided deck is empty or null.");
            stack = new HashMap<>(defaultDeck);
        }

        HashMap<String, Object> currentDepth = stack;

        // Randomize decks
        ArrayList<String> decks = new ArrayList<>(getKeysFromObject(currentDepth));
        currentDepth = (HashMap<String, Object>) currentDepth.get(randomArray(decks));

        ArrayList<String> categories = new ArrayList<>(getKeysFromObject(currentDepth));
        String innerCategories = randomArray(categories);
        currentDepth = (HashMap<String, Object>) currentDepth.get(innerCategories);

        ArrayList<String> words = new ArrayList<>(getKeysFromObject(currentDepth));
        String rWord = randomArray(words);
        currentDepth = (HashMap<String, Object>) currentDepth.get(rWord);

        ArrayList<String> perp = new ArrayList<>(getKeysFromObject(currentDepth));

        // creating usr tool
        Scanner sc = new Scanner(System.in);
        timer usrTimer = new timer();
        Thread thread = new Thread(usrTimer);
        thread.setDaemon(true);

        System.out.println("You have " + usrTimer.getTimer() + " to answer");
        thread.start();

        System.out.println("---------- " + rWord + " ----------");
        System.out.println("Enter text : ");
        String text = sc.nextLine(); // del later
        sc.close();
        // System.out.println("Has category: " + categories);
        // System.out.println("Random category: " + innerCategories);
        // System.out.println("Randomized word: " + rWord);
        // System.out.println("perp : " + perp);
        for (int i = 0; i < perp.size(); i++) {
            System.out.println("part of speech: " + perp.get(i));
            ArrayList<String> define = (ArrayList<String>) currentDepth.get(perp.get(i));
            for (int j = 0; j < define.size(); j++) {
                System.out.println("Definition " + ": " + define.get(j));
            }
        }
        // System.out.println("Cards randomized successfully.");
    }

}

class timer implements Runnable {
    private int timer;

    timer() {
        this.timer = 5;
    }

    timer(int time) {
        this.timer = time;
    }

    public int getTimer() {
        return this.timer;
    }

    @Override
    public void run() {
        for (int i = timer; i >= 0; i--) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
            if (i == 0) {
                System.out.println("Time out");
                continue;
            }
        }
    }
}
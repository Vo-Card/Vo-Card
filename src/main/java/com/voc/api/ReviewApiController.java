package com.voc.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DeckManager;
import com.voc.utils.Row;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/review")
public class ReviewApiController {

    public static class reviewRequest {
        ArrayList<Long> allCards = new ArrayList<>();
    }

    /**
     * TODO<> continue this
     * Javascript POST Array contain deck_id
     * GET cards from each deck_id
     * then randomize later
     * 
     * @return
     */
    @GetMapping("/getCards")
    public static ResponseEntity<Map<String, Object>> reviewCard(@RequestParam ArrayList<Long> arrayDeckId) {
        Map<String, Object> response = new HashMap<>();
        // It's still an array
        if (arrayDeckId != null) {
            List<Object> decks = new ArrayList<>();
            for (int i = 0; i < arrayDeckId.size(); i++) {
                List<Row> cards = DeckManager.getAllCardFromDeck(arrayDeckId.get(i));
                decks.add(cards);
            }
            response.put("Cards", decks);
            return ResponseEntity.ok(response);
        } else {
            System.out.println("No deck_id has send");
        }
        System.out.println(arrayDeckId);
        return null;
    }
}

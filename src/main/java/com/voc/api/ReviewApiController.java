package com.voc.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DeckManager;
import com.voc.database.ReviewManager;
import com.voc.jwt.JwtManager;
import com.voc.utils.Row;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/review")
public class ReviewApiController {

    private static class LearnCardRequest {
        public Long deckId;
        public Long cardId;
    }

    public static class CompletionRequest {
        public Integer totalCards;
        public Integer totalCorrect;
        public Integer totalFailed;
    }

    /**
     * Javascript send Array of deck_id
     * GET cards from each deck_id
     * then randomize later
     * 
     * @return cards in specific decks
     */
    @GetMapping("/getCards")
    public static ResponseEntity<Map<String, Object>> getDeckCards(@RequestParam ArrayList<Long> arrayDeckId) {
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

    @GetMapping("/mostRecentLearning")
    public static ResponseEntity<Map<String, Object>> getMostRecentLearning(
            @RequestHeader(value = "Authorization", required = false) String authToken) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {

            Row row = ReviewManager.getLastestReview(userId.get());

            response.put("recent_review", row);
            response.put("message", "successfully send the review");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }

    }

    @GetMapping("/thisWeekStats")
    public static ResponseEntity<Map<String, Object>> getThisWeekStats(
            @RequestHeader(value = "Authorization", required = false) String authToken) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {

            List<Row> stats = ReviewManager.getThisWeekStats(userId.get());

            response.put("stats", stats);
            response.put("message", "Successfully updated user cards.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }

    }

    @PutMapping("/addLearnedCard")
    public static ResponseEntity<Map<String, Object>> addLearnedCard(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody LearnCardRequest learnedCard) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(true, userId.get(), learnedCard.deckId, null, null,
                    null,
                    null);
            if (!validateOwner || !(DeckManager.getDeckIdFromCardId(learnedCard.cardId) != learnedCard.deckId)) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            if (!ReviewManager.updateReviewedCard(userId.get(), learnedCard.deckId, learnedCard.cardId)) {
                response.put("message", "Failed to update learned cards.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("message", "Successfully updated user cards.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PutMapping("/addCompletedSession")
    public static ResponseEntity<Map<String, Object>> addCompletedSession(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody CompletionRequest completedSession) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            if (!ReviewManager.addCompletionSession(userId.get(), completedSession.totalCards,
                    completedSession.totalCorrect, completedSession.totalFailed)) {
                response.put("message", "Failed to added user session.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("message", "Successfully added user session.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

}

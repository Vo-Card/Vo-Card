package com.voc.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DeckManager;
import com.voc.jwt.JwtManager;
import com.voc.utils.Row;

@RestController
@RequestMapping("/api/decks")
public class DeckApiController {

    private Optional<Long> getUserIdFromJWT(String authToken) {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            return JwtManager.validateJwt(token);
        }
        return Optional.empty();
    }

    @GetMapping("/getDecks")
    public ResponseEntity<Map<String, Object>> deckLoader(
            @RequestHeader(value = "Authorization", required = false) String authToken) {

        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            List<Row> ownedDecks = DeckManager.getOwnedDecks(userId.get());
            List<Row> forkedDecks = DeckManager.getForkedDecks(userId.get());
            response.put("ownedDecks", ownedDecks);
            response.put("forkedDecks", forkedDecks);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }

    }

    @GetMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> getDeckData(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId) {

        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);
        Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);

        if (ownershipType.isEmpty()) {
            response.put("message", "You do not owned this deck.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("ownership_type", ownershipType.get());

        if (userId.isPresent()) {
            List<Row> deckLevels = DeckManager.getDeckLevel(deckId);
            response.put("deckLevels", deckLevels);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/{deckId}/{levelId}")
    public ResponseEntity<Map<String, Object>> getLevelData(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        // Validate Ownership
        Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
        if (ownershipType.isEmpty()) {
            response.put("message", "You do not owned this deck.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("ownership_type", ownershipType.get());

        if (userId.isPresent()) {
            List<Row> deckLevels = DeckManager.getCardsOfLevel(deckId, levelId);
            response.put("cards", deckLevels);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/{deckId}/{levelId}/{cardId}")
    public ResponseEntity<Map<String, Object>> getDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId,
            @PathVariable Long cardId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        // Validate Ownership
        Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
        if (ownershipType.isEmpty()) {
            response.put("message", "You do not owned this deck.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("ownership_type", ownershipType.get());

        if (userId.isPresent()) {
            Row cardData = DeckManager.getCardInfo(deckId, levelId, cardId);
            response.put("card_data", cardData);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }
}
package com.voc.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DeckManager;
import com.voc.jwt.JwtManager;
import com.voc.server.Snowflake;
import com.voc.utils.Row;
import com.voc.utils.ThemeTypes;

@RestController
@RequestMapping("/api/decks")
public class DeckApiController {

    public static Optional<Long> getUserIdFromJWT(String authToken) {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            return JwtManager.validateJwt(token);
        }
        return Optional.empty();
    }

    private static class DeckCreateRequest {
        public String deckName;
        public String deckDescription;
        public String primaryColor;
        public String secondaryColor;
    }

    private static class LevelCreateRequest {
        public String levelValue;
        public int levelWeight;
        public String primaryColor;
        public String secondaryColor;
    }

    private static class CardCreateRequest {
        public String cardWord;
    }

    /* ----------- */
    /* PUT MAPPING */
    /* ----------- */

    @PutMapping("/create")
    public ResponseEntity<Map<String, Object>> createDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody DeckCreateRequest deckData) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Long deckId = Snowflake.nextId();
            Long themeId = DeckManager.createNewTheme("Custom_" + deckId,
                    ThemeTypes.Deck,
                    "/components/template/deck_template.svg");
            Long modifierId = DeckManager.createNewModifier(deckData.primaryColor,
                    deckData.secondaryColor, "dots",
                    ThemeTypes.Deck);

            DeckManager.createNewDeck(deckId, deckData.deckName,
                    deckData.deckDescription, false, themeId,
                    modifierId, userId.get());

            response.put("message", "Successfully created Deck");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PutMapping("/{deckId}/create")
    public ResponseEntity<Map<String, Object>> createLevel(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody LevelCreateRequest levelData,
            @PathVariable Long deckId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(userId.get(), deckId, null, null, null, null);
            if (!validateOwner) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            Long levelId = Snowflake.nextId();
            Long themeId = DeckManager.createNewTheme("Custom_" + levelId, ThemeTypes.Deck,
                    "/components/template/card_template.svg");
            Long modifierId = DeckManager.createNewModifier(levelData.primaryColor, levelData.secondaryColor, "dots",
                    ThemeTypes.Deck);

            DeckManager.createNewLevel(levelId, levelData.levelValue, levelData.levelWeight, themeId,
                    modifierId, deckId);

            response.put("message", "Successfully created Level");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PutMapping("/{deckId}/{levelId}/create")
    public ResponseEntity<Map<String, Object>> createCard(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody CardCreateRequest cardData,
            @PathVariable Long deckId,
            @PathVariable Long levelId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(userId.get(), deckId, null, null, null, null);
            if (!validateOwner) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Long cardId = Snowflake.nextId();

            DeckManager.createNewCard(cardId, levelId, cardData.cardWord);

            response.put("message", "Successfully created Level");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    /* ----------- */
    /* GET MAPPING */
    /* ----------- */

    @GetMapping({ "", "/" })
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

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(userId.get(), deckId, null, null, null, null);
            Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
            if (!validateOwner || ownershipType.isEmpty()) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("ownership_type", ownershipType.get());

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

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(userId.get(), deckId, levelId, null, null, null);
            Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
            if (!validateOwner || ownershipType.isEmpty()) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("ownership_type", ownershipType.get());

            List<Row> deckLevels = DeckManager.getCardsOfLevel(levelId);
            response.put("cards", deckLevels);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/{deckId}/{levelId}/{cardId}")
    public ResponseEntity<Map<String, Object>> getCardData(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId,
            @PathVariable Long cardId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(userId.get(), deckId, levelId, cardId, null, null);
            Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
            if (!validateOwner || ownershipType.isEmpty()) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("ownership_type", ownershipType.get());

            Row cardData = DeckManager.getCardInfo(cardId);
            response.put("card_data", cardData);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(401).body(response);
        }
    }
}
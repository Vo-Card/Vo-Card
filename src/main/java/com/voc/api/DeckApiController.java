package com.voc.api;
// TODO:<request get cookie send to DeckManager>

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
    @GetMapping("/getDecks")
    public ResponseEntity<Map<String, Object>> deckLoader(
            @RequestHeader(value = "Authorization", required = false) String authToken) {

        Map<String, Object> response = new HashMap<>();

        if (authToken != null && authToken.startsWith("Bearer ")) {
            
            String token = authToken.substring(7);
            Optional<Long> optionalUserId = JwtManager.validateJwt(token);

            if (optionalUserId.isPresent()) {
                List<Row> ownedDecks = DeckManager.getOwnedDecks(optionalUserId.get());
                List<Row> forkedDecks = DeckManager.getForkedDecks(optionalUserId.get());
                response.put("ownedDecks", ownedDecks);
                response.put("forkedDecks", forkedDecks);
            }
        }
        response.put("status", "session is valid");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> getDeckData(
        @RequestHeader(value = "Authorization", required = false) String authToken,
        @PathVariable Long deckId) { 
        
            
        Map<String, Object> response = new HashMap<>();

        if (authToken != null && authToken.startsWith("Bearer ")) {
                
            String token = authToken.substring(7);
            Optional<Long> optionalUserId = JwtManager.validateJwt(token);

            // Validate Ownership
            if(!DeckManager.validateOwnership(optionalUserId.get(), deckId)){
                response.put("status", "error");
                return ResponseEntity.ok(response);
            }
            
            if (optionalUserId.isPresent()) {
                List<Row> deckLevels = DeckManager.getDeckLevel(deckId);
                response.put("deckLevels", deckLevels);
            }
        }

        response.put("status", "pass");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deckId}/{levelId}")
    public ResponseEntity<Map<String, Object>> getLevelData(
        @RequestHeader(value = "Authorization", required = false) String authToken,
        @PathVariable Long deckId,
        @PathVariable Long levelId) { 
        Map<String, Object> response = new HashMap<>();

        response.put("status", "session is valid");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deckId}/{levelId}/{cardId}")
    public ResponseEntity<Map<String, Object>> getDeck(
        @RequestHeader(value = "Authorization", required = false) String authToken,
        @PathVariable Long deckId,
        @PathVariable Long levelId,
        @PathVariable Long cardId) { 
        Map<String, Object> response = new HashMap<>();

        response.put("status", "session is valid");
        return ResponseEntity.ok(response);
    }

}
// TODO:<api for create Empty deck>
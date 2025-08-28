package com.voc.api;
// TODO:<request get cookie send to DeckManager>

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DeckManager;
import com.voc.jwt.JwtManager;

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
                Object temp = DeckManager.deckLoader(optionalUserId.get());
                response.put("decks", temp);
            }
        }
        response.put("status", "session is valid");
        return ResponseEntity.ok(response);
    }
}
// TODO:<api for create Empty deck>
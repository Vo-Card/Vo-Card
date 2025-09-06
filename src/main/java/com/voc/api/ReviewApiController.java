package com.voc.api;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Map<String, Object>> reviewCard(@RequestParam ArrayList<Long> arrayDeckId) {
        
        return null;
    }
}

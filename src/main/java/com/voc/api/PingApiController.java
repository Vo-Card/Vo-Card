package com.voc.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
public class PingApiController {

    /**
     * A simple, authenticated endpoint to verify a user's session.
     * The SessionInterceptor automatically validates the JWT before this
     * method is executed. If validation fails, the interceptor will
     * return a 401 response and this method will not be called.
     *
     * @return A success response with a status message.
     */
    @GetMapping("/api/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Collections.singletonMap("status", "Session is valid."));
    }
}
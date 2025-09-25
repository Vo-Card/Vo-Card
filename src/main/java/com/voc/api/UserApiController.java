package com.voc.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    // User-related API endpoints would go here

    private static class UserProfileBody {
        public String username;
        public String display_name;
        public String new_password;
        public String current_password;
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody UserProfileBody profileBody) {
        return null;
    }
}

package com.voc.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DatabaseUtils;
import com.voc.database.DeckManager;
import com.voc.jwt.JwtManager;
import com.voc.security.Permission;
import com.voc.server.Snowflake;
import com.voc.utils.Row;
import com.voc.utils.ThemeTypes;

@RestController
@RequestMapping("/api/decks")
public class DeckApiController {

    private static class UpdateCardRequest {
        public String newCardName;
        public List<DeleteData> deleteData;
        public List<EditData> editData;
        public List<CreateData> addData;

        public static class DeleteData {
            public Long posId;
            public Boolean isPos;
            public List<Definitions> definitions;

            public static class Definitions {
                public Long defId;
            }
        }

        public static class EditData {
            public Long posId;
            public String newPos;
            public List<Definitions> definitions;

            public static class Definitions {
                public Long defId;
                public String newDef;
            }
        }

        public static class CreateData {
            public String dataType;
            public String targetId;
            public String updatedContent;
            public String uuid;
        }
    }

    private static class UpdateDeckRequest {
        public String deckName;
        public String deckDescription;
        public String primaryColor;
        public String secondaryColor;
        public Boolean isPublic;
        public Boolean allowCloning;
    }

    private static class UpdateLevelRequest {
        public String levelValue;
        public int levelWeight;
        public String primaryColor;
        public String secondaryColor;
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

    private static class ForkDeckRequest {
        public Long deckId;
    }

    private static class VoteRequest {
        public Long deckId;
    }

    /* ----------- */
    /* PUT MAPPING */
    /* ----------- */

    @PutMapping("/downvote")
    public ResponseEntity<Map<String, Object>> downvoteDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody VoteRequest voteData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String message = DeckManager.addReviewToDeck(voteData.deckId, userId.get(), 2);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PutMapping("/upvote")
    public ResponseEntity<Map<String, Object>> upvoteDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody VoteRequest voteData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String message = DeckManager.addReviewToDeck(voteData.deckId, userId.get(), 1);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PutMapping("/fork")
    public ResponseEntity<Map<String, Object>> forkDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody ForkDeckRequest forkData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean success = DeckManager.forkDeck(forkData.deckId, userId.get());
            if (success) {
                response.put("message", "Successfully forked Deck");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to fork Deck");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PutMapping("/create")
    public ResponseEntity<Map<String, Object>> createDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody DeckCreateRequest deckData) {
        Map<String, Object> response = new HashMap<>();

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Long deckId = Snowflake.nextId();
            DeckManager.createNewDeck(deckId, deckData.deckName,
                    deckData.deckDescription, false, null,
                    null, userId.get());
            Long themeId = DeckManager.createNewTheme(deckId, "Custom_" + deckId,
                    ThemeTypes.Deck,
                    "/components/template/deck_template.svg");
            Long modifierId = DeckManager.createNewModifier(deckId, deckData.primaryColor,
                    deckData.secondaryColor, "dots",
                    ThemeTypes.Deck);
            DatabaseUtils.sqlPrepareStatement(
                    "UPDATE decktb SET theme_id_FK = ?, modifier_id_FK = ? WHERE deck_id_PK = ?",
                    themeId, modifierId, deckId);

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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_DELETE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, null, null, null, null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Long levelId = Snowflake.nextId();
            DeckManager.createNewLevel(levelId, levelData.levelValue, levelData.levelWeight, null,
                    null, deckId);

            Long themeId = DeckManager.createNewTheme(levelId, "Custom_" + levelId, ThemeTypes.Card,
                    "/components/template/card_template.svg");
            Long modifierId = DeckManager.createNewModifier(levelId, levelData.primaryColor, levelData.secondaryColor,
                    "dots",
                    ThemeTypes.Card);

            DatabaseUtils.sqlPrepareStatement(
                    "UPDATE card_leveltb SET theme_id_FK = ?, modifier_id_FK = ? WHERE level_id_PK = ?",
                    themeId, modifierId, levelId);

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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_DELETE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, null, null, null, null);
            if (!validateOwner && !bypass) {
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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicDecks(
            @RequestHeader(value = "Authorization", required = false) String authToken) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            List<Row> publicDecks = DeckManager.getPublicDecks(userId.get());
            response.put("publicDecks", publicDecks);
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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(true, userId.get(), deckId, null, null, null, null);
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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(true, userId.get(), deckId, levelId, null, null,
                    null);
            Optional<String> ownershipType = DeckManager.getOwnershipType(userId.get(), deckId);
            if (!validateOwner || ownershipType.isEmpty()) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            response.put("ownership_type", ownershipType.get());

            List<Row> deckLevels = DeckManager.getCardsOfLevel(levelId);
            Row levelInfo = DeckManager.getLevelColor(levelId);

            response.put("cards", deckLevels);
            response.put("theme", levelInfo);
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

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(true, userId.get(), deckId, levelId, cardId, null,
                    null);
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

    /* ------------- */
    /* POST MAPPINGS */
    /* ------------- */

    @PostMapping("/modExplore")
    public ResponseEntity<Map<String, Object>> modExplore(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestParam Long deckId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.MODERATE_EXPLORER);

            if (!bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            DeckManager.toggleDeckPublic(deckId);
            response.put("message", "Successfully toggled Deck public status");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PostMapping("/{deckId}/{levelId}/{cardId}/update")
    public ResponseEntity<Map<String, Object>> updateCard(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId,
            @PathVariable Long cardId,
            @RequestBody UpdateCardRequest updateReq) {
        Map<String, Object> response = new HashMap<>();

        // Check Force Bypass

        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isEmpty()) {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }

        Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_UPDATE_ITEM);

        Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, cardId, null, null);
        if (!validateOwner && !bypass) {
            response.put("message", "You don't have permission.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
        }

        // Prepare data
        Map<Long, String> posToDelete = new HashMap<>();
        Map<Long, List<Long>> defToDelete = new HashMap<>();
        Map<Long, String> posToEdit = new HashMap<>();
        Map<Long, Map<Long, String>> defToEdit = new HashMap<>();
        Map<String, Map<Long, String>> posToCreate = new HashMap<>();
        Map<String, Map<Long, String>> defToCreate = new HashMap<>();

        // Skip definition deletes if the parent POS is already deleted
        if (updateReq.deleteData != null) {
            for (UpdateCardRequest.DeleteData delPos : updateReq.deleteData) {
                if (delPos.posId != null) {
                    boolean isPosDelete = delPos.isPos != null && delPos.isPos;
                    if (isPosDelete) {
                        // mark the POS for deletion
                        posToDelete.put(delPos.posId, "pos");
                    }

                    // Only process definition-level deletes if POS itself is NOT deleted
                    if (!isPosDelete && delPos.definitions != null && !delPos.definitions.isEmpty()) {
                        for (UpdateCardRequest.DeleteData.Definitions def : delPos.definitions) {
                            if (def.defId != null) {
                                defToDelete
                                        .computeIfAbsent(delPos.posId, k -> new ArrayList<>())
                                        .add(def.defId);
                            }
                        }
                    }
                }
            }
        }

        // Filter editData to exclude any edits for POS marked deleted
        if (updateReq.editData != null) {
            for (UpdateCardRequest.EditData editPos : updateReq.editData) {
                if (editPos.posId != null) {
                    // skip pos edits if pos is deleted
                    if (editPos.newPos != null && !posToDelete.containsKey(editPos.posId)) {
                        posToEdit.put(editPos.posId, editPos.newPos);
                    }

                    // skip definition edits if pos is deleted or definition itself is deleted
                    if (editPos.definitions != null && !editPos.definitions.isEmpty()) {
                        for (UpdateCardRequest.EditData.Definitions def : editPos.definitions) {
                            if (def.defId != null &&
                                    def.newDef != null &&
                                    !defToDelete.getOrDefault(editPos.posId, List.of()).contains(def.defId) &&
                                    !posToDelete.containsKey(editPos.posId)) {
                                defToEdit
                                        .computeIfAbsent(editPos.posId, k -> new HashMap<>())
                                        .put(def.defId, def.newDef);
                            }
                        }
                    }
                }
            }
        }

        if (updateReq.addData != null) {
            for (UpdateCardRequest.CreateData createData : updateReq.addData) {
                if (createData.dataType == null || createData.dataType.isBlank()
                        || createData.updatedContent == null || createData.updatedContent.isBlank()
                        || createData.uuid == null || createData.uuid.isBlank()) {
                    continue;
                }

                switch (createData.dataType) {
                    case "pos":
                        posToCreate.put(
                                createData.uuid,
                                Map.of(Snowflake.nextId(), createData.updatedContent));
                        break;
                    case "definition":
                        if (createData.targetId == null)
                            continue;

                        String[] split = createData.targetId.split("_");
                        if (split.length == 1) {
                            try {
                                Long targetPosId = Long.valueOf(createData.targetId);
                                if (posToDelete.containsKey(targetPosId)) {
                                    continue;
                                }
                                Map<Long, String> defMap = new HashMap<>();
                                defMap.put(targetPosId, createData.updatedContent);
                                defToCreate.put(createData.uuid, defMap);
                            } catch (NumberFormatException e) {
                                response.put("message", "invalid def id");
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
                            }
                        } else if (split.length > 1 && split[0].equals("fake")) {
                            Map<Long, String> posMap = posToCreate.get(createData.targetId);
                            if (posMap != null && !posMap.isEmpty()) {
                                Long generatedPosId = posMap.keySet().iterator().next();
                                Map<Long, String> defMap = new HashMap<>();
                                defMap.put(generatedPosId, createData.updatedContent);
                                defToCreate.put(createData.uuid, defMap);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        if (updateReq.newCardName != null && !updateReq.newCardName.isBlank()) {
            DeckManager.updateCard(cardId, updateReq.newCardName);
        }

        for (Map.Entry<Long, String> entry : posToDelete.entrySet()) {
            validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId,
                    cardId, entry.getKey(), null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }

            DeckManager.deletePos(entry.getKey());
        }

        for (Map.Entry<Long, List<Long>> entry : defToDelete.entrySet()) {
            for (Long defId : entry.getValue()) {
                validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId,
                        cardId, entry.getKey(), defId);
                if (!validateOwner && !bypass) {
                    response.put("message", "You don't have permission.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
                }
                DeckManager.deleteDefinition(defId);
            }
        }

        // Edit POS
        for (Map.Entry<Long, String> entry : posToEdit.entrySet()) {
            validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, cardId, entry.getKey(),
                    null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            DeckManager.updatePos(entry.getKey(), entry.getValue());
        }

        // Edit Definitions
        for (Map.Entry<Long, Map<Long, String>> entry : defToEdit.entrySet()) {
            Long posId = entry.getKey();
            Map<Long, String> defs = entry.getValue();
            for (Map.Entry<Long, String> defEntry : defs.entrySet()) {
                Long defId = defEntry.getKey();
                String newDef = defEntry.getValue();
                validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, cardId, posId,
                        defId);
                if (!validateOwner && !bypass) {
                    response.put("message", "You don't have permission.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
                }
                DeckManager.updateDefinition(defId, newDef);
            }
        }

        // Create POS and Def
        for (Map.Entry<String, Map<Long, String>> entry : posToCreate.entrySet()) {
            Map<Long, String> posMap = entry.getValue();
            for (Map.Entry<Long, String> posEntry : posMap.entrySet()) {
                Long newPosId = posEntry.getKey();
                String newPosValue = posEntry.getValue();
                DeckManager.createNewPartOfSpeech(newPosId, cardId, newPosValue);
            }
        }

        for (Map.Entry<String, Map<Long, String>> entry : defToCreate.entrySet()) {

            Map<Long, String> defMap = entry.getValue();

            for (Map.Entry<Long, String> defEntry : defMap.entrySet()) {
                Long posId = defEntry.getKey();
                validateOwner = DeckManager.validateOwnership(true, userId.get(), deckId, levelId, cardId, posId, null);
                if (!validateOwner && !bypass) {
                    response.put("message", "You don't have permission.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
                }
                String newDefValue = defEntry.getValue();
                DeckManager.createNewDefinition(Snowflake.nextId(), posId, newDefValue);
            }
        }

        response.put("message", "Data passed");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{deckId}/update")
    public ResponseEntity<Map<String, Object>> deckUpdate(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @RequestBody UpdateDeckRequest deckUpd) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_UPDATE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, null, null, null, null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            DeckManager.updateDeck(
                    deckId, deckUpd.deckName, deckUpd.deckDescription,
                    deckUpd.primaryColor, deckUpd.secondaryColor, deckUpd.isPublic,
                    deckUpd.allowCloning);
            response.put("message", "Successfully toggled Deck public status");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PostMapping("/{deckId}/clone")
    public ResponseEntity<Map<String, Object>> cloneDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean validateOwner = DeckManager.validateOwnership(true, userId.get(), deckId, null, null, null, null);
            if (!validateOwner) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Boolean success = DeckManager.cloneDeck(deckId, userId.get());
            if (success) {
                response.put("message", "Successfully cloned Deck");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to clone Deck");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PostMapping("/{deckId}/{levelId}/update")
    public ResponseEntity<Map<String, Object>> levelUpdate(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId,
            @RequestBody UpdateLevelRequest levelUpd) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_UPDATE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, null, null,
                    null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            DeckManager.updateLevel(
                    levelId, levelUpd.levelValue, levelUpd.levelWeight,
                    levelUpd.primaryColor, levelUpd.secondaryColor);
            response.put("message", "Successfully toggled Deck public status");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    /* --------------- */
    /* DELETE MAPPINGS */
    /* --------------- */

    @DeleteMapping("/removeVote")
    public ResponseEntity<Map<String, Object>> deleteVote(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestBody VoteRequest voteData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String message = DeckManager.addReviewToDeck(voteData.deckId, userId.get(), 0);
            response.put("message", message);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/{deckId}/unfork")
    public ResponseEntity<Map<String, Object>> unForkDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean success = DeckManager.unForkDeck(deckId, userId.get());
            if (success) {
                response.put("message", "Successfully un-forked Deck");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to un-fork Deck");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/{deckId}/delete")
    public ResponseEntity<Map<String, Object>> deleteDeck(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_DELETE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, null, null, null, null);

            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Boolean success = DeckManager.deleteDeckCascade(deckId, userId.get());
            if (success) {
                response.put("message", "Successfully deleted Deck");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to delete Deck");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/{deckId}/{levelId}/delete")
    public ResponseEntity<Map<String, Object>> deleteLevel(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_DELETE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, null, null,
                    null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Boolean success = DeckManager.deleteLevelCascade(levelId, deckId);
            if (success) {
                response.put("message", "Successfully deleted Level");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to delete Level");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/{deckId}/{levelId}/{cardId}/delete")
    public ResponseEntity<Map<String, Object>> deleteCard(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @PathVariable Long deckId,
            @PathVariable Long levelId,
            @PathVariable Long cardId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            Boolean bypass = Permission.checkUserPermission(userId.get(), Permission.Values.FORCE_DELETE_ITEM);

            Boolean validateOwner = DeckManager.validateOwnership(false, userId.get(), deckId, levelId, cardId, null,
                    null);
            if (!validateOwner && !bypass) {
                response.put("message", "You don't have permission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
            }
            Boolean success = DeckManager.deleteCardCascade(cardId, levelId);
            if (success) {
                response.put("message", "Successfully deleted Card");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to delete Card");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }
}
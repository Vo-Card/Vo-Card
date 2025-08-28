-- vo_card.permissiontb definition

CREATE TABLE `permissiontb` (
  `permission_id_PK` tinyint(4) NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(50) NOT NULL DEFAULT '"normal"',
  `permission_level` bigint(20) NOT NULL DEFAULT 0,
  `permission_description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`permission_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card.usertb definition

CREATE TABLE `usertb` (
  `user_id_PK` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `display_name` varchar(100) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `join_date` datetime DEFAULT current_timestamp(),
  `permission_level_FK` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`user_id_PK`),
  UNIQUE KEY `usertb_unique` (`username`),
  KEY `usertb_permissiontb_FK` (`permission_level_FK`),
  CONSTRAINT `usertb_permissiontb_FK` FOREIGN KEY (`permission_level_FK`) REFERENCES `permissiontb` (`permission_id_PK`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card.decktb definition

CREATE TABLE `decktb` (
  `deck_id_PK` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `deck_name` varchar(100) NOT NULL DEFAULT '"New Deck"',
  `deck_description` text DEFAULT NULL,
  `deck_created_date` datetime DEFAULT current_timestamp(),
  `deck_contain_card` int(11) NOT NULL DEFAULT 0,
  `deck_lastest_updated` datetime DEFAULT current_timestamp(),
  `deck_is_public` bit(1) NOT NULL DEFAULT b'0',
  `user_id_FK` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`deck_id_PK`),
  KEY `decktb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `decktb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card.moderation_action definition

CREATE TABLE `moderation_action` (
  `action_id_PK` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `action_type` varchar(255) DEFAULT NULL,
  `action_message` text DEFAULT NULL,
  `action_time_stamp` datetime DEFAULT current_timestamp(),
  `user_id_FK` int(10) unsigned DEFAULT 0,
  PRIMARY KEY (`action_id_PK`),
  KEY `moderation_action_usertb_FK` (`user_id_FK`),
  CONSTRAINT `moderation_action_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card.reviewtb definition

CREATE TABLE `reviewtb` (
  `deck_id_FK` int(10) unsigned DEFAULT NULL,
  `user_id_FK` int(10) unsigned DEFAULT NULL,
  `review_action` bit(2) NOT NULL DEFAULT b'0',
  `lastest_review` datetime DEFAULT current_timestamp(),
  KEY `reviewtb_decktb_FK` (`deck_id_FK`),
  KEY `reviewtb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `reviewtb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `reviewtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card.sessiontb definition

CREATE TABLE `sessiontb` (
  `session_id_PK` varchar(255) NOT NULL,
  `user_id_FK` int(10) unsigned NOT NULL,
  `refresh_token_hash` varchar(100) NOT NULL,
  `expires_at` datetime NOT NULL DEFAULT (current_timestamp() + interval 30 day),
  `remember_me` bit(1) NOT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`session_id_PK`),
  KEY `session_usertb_FK` (`user_id_FK`),
  CONSTRAINT `session_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card.stattb definition

CREATE TABLE `stattb` (
  `stat_id_PK` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `stat_streak` int(10) unsigned NOT NULL DEFAULT 0,
  `stat_most_learn_deck` smallint(5) unsigned DEFAULT NULL,
  `stat_total_number_of_review` int(10) unsigned NOT NULL DEFAULT 0,
  `user_id_FK` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`stat_id_PK`),
  KEY `stattb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `stattb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card.card_leveltb definition

CREATE TABLE `card_leveltb` (
  `level_id_PK` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `level_weight` int(11) NOT NULL DEFAULT 1,
  `level_name` varchar(10) NOT NULL DEFAULT 'New Level',
  `deck_id_FK` int(10) unsigned NOT NULL,
  PRIMARY KEY (`level_id_PK`),
  KEY `card_leveltb_decktb_FK` (`deck_id_FK`),
  CONSTRAINT `card_leveltb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card.cardtb definition

CREATE TABLE `cardtb` (
  `card_id_PK` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `card_word` varchar(255) NOT NULL DEFAULT 'New  Card',
  `card_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`card_content`)),
  `level_id_FK` int(10) unsigned DEFAULT NULL,
  `deck_id_FK` int(10) unsigned NOT NULL,
  PRIMARY KEY (`card_id_PK`),
  KEY `cardtb_decktb_FK` (`deck_id_FK`),
  KEY `cardtb_card_leveltb_FK` (`level_id_FK`),
  CONSTRAINT `cardtb_card_leveltb_FK` FOREIGN KEY (`level_id_FK`) REFERENCES `card_leveltb` (`level_id_PK`),
  CONSTRAINT `cardtb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card.learned_cardtb definition

CREATE TABLE `learned_cardtb` (
  `learn_card_ease_factor` float NOT NULL DEFAULT 2.5,
  `learn_card_interval` smallint(5) unsigned NOT NULL DEFAULT 1,
  `learned_card_latest_review` datetime DEFAULT current_timestamp(),
  `card_id_PK` bigint(20) unsigned DEFAULT NULL,
  `user_id_PK` int(10) unsigned DEFAULT NULL,
  KEY `learned_cardtb_cardtb_FK` (`card_id_PK`),
  KEY `learned_cardtb_usertb_FK` (`user_id_PK`),
  CONSTRAINT `learned_cardtb_cardtb_FK` FOREIGN KEY (`card_id_PK`) REFERENCES `cardtb` (`card_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `learned_cardtb_usertb_FK` FOREIGN KEY (`user_id_PK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- vo_card.jwt_keys definition

CREATE TABLE `jwt_keys` (
  `kid` varchar(36) NOT NULL,
  `secret_key` varchar(128) NOT NULL,
  `expire_at` timestamp NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_primary` bit(1) NOT NULL,
  PRIMARY KEY (`kid`),
  KEY `jwt_keys_expire_at_IDX` (`expire_at`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- vocard.permissiontb definition

CREATE TABLE `permissiontb` (
  `permission_level_PK` tinyint NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '"normal"',
  `permission_description` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`permission_level_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vocard.usertb definition

CREATE TABLE `usertb` (
  `user_id_PK` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `join_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `permission_level_FK` tinyint DEFAULT NULL,
  PRIMARY KEY (`user_id_PK`),
  UNIQUE KEY `usertb_unique` (`username`),
  KEY `usertb_permissiontb_FK` (`permission_level_FK`),
  CONSTRAINT `usertb_permissiontb_FK` FOREIGN KEY (`permission_level_FK`) REFERENCES `permissiontb` (`permission_level_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vocard.`session` definition

CREATE TABLE `session` (
  `session_id_PK` varchar(255) NOT NULL,
  `session_last_login` datetime DEFAULT CURRENT_TIMESTAMP,
  `session_login_info` varchar(255) DEFAULT NULL,
  `session_browser_id` varchar(255) DEFAULT NULL,
  `user_id_PK` int unsigned DEFAULT NULL,
  PRIMARY KEY (`session_id_PK`),
  KEY `session_usertb_FK` (`user_id_PK`),
  CONSTRAINT `session_usertb_FK` FOREIGN KEY (`user_id_PK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vocard.decktb definition

CREATE TABLE `decktb` (
  `deck_id_PK` int unsigned NOT NULL AUTO_INCREMENT,
  `deck_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '"New Deck"',
  `deck_description` text COLLATE utf8mb4_unicode_ci,
  `deck_created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `deck_contain_card` int NOT NULL DEFAULT '0',
  `deck_lastest_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  `deck_is_public` bit(1) NOT NULL DEFAULT b'0',
  `user_id_FK` int unsigned DEFAULT NULL,
  PRIMARY KEY (`deck_id_PK`),
  KEY `decktb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `decktb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vocard.moderation_action definition

CREATE TABLE `moderation_action` (
  `action_id_PK` int unsigned NOT NULL AUTO_INCREMENT,
  `action_type` varchar(255) DEFAULT NULL,
  `action_message` text,
  `action_time_stamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `user_id_FK` int unsigned DEFAULT '0',
  PRIMARY KEY (`action_id_PK`),
  KEY `moderation_action_usertb_FK` (`user_id_FK`),
  CONSTRAINT `moderation_action_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE SET DEFAULT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vocard.reviewtb definition

CREATE TABLE `reviewtb` (
  `deck_id_FK` int unsigned DEFAULT NULL,
  `user_id_FK` int unsigned DEFAULT NULL,
  `review_action` bit(2) NOT NULL DEFAULT b'0',
  `lastest_review` datetime DEFAULT CURRENT_TIMESTAMP,
  KEY `reviewtb_decktb_FK` (`deck_id_FK`),
  KEY `reviewtb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `reviewtb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`),
  CONSTRAINT `reviewtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vocard.stattb definition

CREATE TABLE `stattb` (
  `stat_id_PK` int unsigned NOT NULL AUTO_INCREMENT,
  `stat_streak` int unsigned NOT NULL DEFAULT '0',
  `stat_most_learn_deck` smallint unsigned DEFAULT NULL,
  `stat_total_number_of_review` int unsigned NOT NULL DEFAULT '0',
  `user_id_FK` int unsigned DEFAULT NULL,
  PRIMARY KEY (`stat_id_PK`),
  KEY `stattb_usertb_FK` (`user_id_FK`),
  CONSTRAINT `stattb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vocard.cardtb definition

CREATE TABLE `cardtb` (
  `card_id_PK` bigint unsigned NOT NULL AUTO_INCREMENT,
  `card_word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'New  Card',
  `card_content` text,
  `card_weight` int NOT NULL DEFAULT '1',
  `card_level_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'New Level',
  `deck_id_FK` int unsigned DEFAULT NULL,
  PRIMARY KEY (`card_id_PK`),
  KEY `cardtb_decktb_FK` (`deck_id_FK`),
  CONSTRAINT `cardtb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vocard.learned_cardtb definition

CREATE TABLE `learned_cardtb` (
  `learn_card_ease_factor` float NOT NULL DEFAULT '2.5',
  `learn_card_interval` smallint unsigned NOT NULL DEFAULT '1',
  `learned_card_latest_review` datetime DEFAULT CURRENT_TIMESTAMP,
  `card_id_PK` bigint unsigned DEFAULT NULL,
  `user_id_PK` int unsigned DEFAULT NULL,
  KEY `learned_cardtb_cardtb_FK` (`card_id_PK`),
  KEY `learned_cardtb_usertb_FK` (`user_id_PK`),
  CONSTRAINT `learned_cardtb_cardtb_FK` FOREIGN KEY (`card_id_PK`) REFERENCES `cardtb` (`card_id_PK`),
  CONSTRAINT `learned_cardtb_usertb_FK` FOREIGN KEY (`user_id_PK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
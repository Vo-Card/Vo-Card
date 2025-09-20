-- vo_card_test.blogtb definition

CREATE TABLE `blogtb` (
  `post_id_PK` bigint(20) unsigned NOT NULL,
  `post_type` int(10) unsigned NOT NULL,
  `subject` text DEFAULT NULL,
  `content` text DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp(),
  `updated_date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`post_id_PK`),
  KEY `blogtb_post_type_IDX` (`post_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.jwt_keys definition

CREATE TABLE `jwt_keys` (
  `kid` char(36) NOT NULL,
  `secret_key` varchar(128) NOT NULL,
  `expire_at` timestamp NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_primary` tinyint(1) NOT NULL,
  PRIMARY KEY (`kid`),
  KEY `jwt_keys_expire_at_IDX` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.permissiontb definition

CREATE TABLE `permissiontb` (
  `permission_id_PK` bigint(20) unsigned NOT NULL,
  `permission_name` varchar(50) NOT NULL DEFAULT 'normal',
  `permission_level` bigint(20) NOT NULL DEFAULT 0,
  `permission_description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`permission_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.theme_modifiertb definition

CREATE TABLE `theme_modifiertb` (
  `modifier_id_PK` bigint(20) unsigned NOT NULL,
  `primary_color` varchar(255) NOT NULL DEFAULT '0',
  `secondary_color` varchar(255) NOT NULL DEFAULT '0',
  `card_pattern` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`modifier_id_PK`),
  KEY `theme_modifiertb_modifier_id_PK_IDX` (`modifier_id_PK`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card_test.themetb definition

CREATE TABLE `themetb` (
  `theme_id_PK` bigint(20) unsigned NOT NULL,
  `theme_name` varchar(255) NOT NULL DEFAULT 'New theme',
  `theme_type` enum('Card','Deck') NOT NULL,
  `theme_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`theme_id_PK`),
  KEY `deck_themetb_theme_id_PK_IDX` (`theme_id_PK`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card_test.workertb definition

CREATE TABLE `workertb` (
  `worker_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `hostname` varchar(128) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  `last_heartbeat` timestamp NOT NULL DEFAULT current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expires_at` timestamp NOT NULL DEFAULT (current_timestamp() + interval 7 day),
  PRIMARY KEY (`worker_id`),
  UNIQUE KEY `workertb_unique` (`uuid`),
  KEY `workertb_expires_at_IDX` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.usertb definition

CREATE TABLE `usertb` (
  `user_id_PK` bigint(20) unsigned NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `join_date` datetime NOT NULL DEFAULT current_timestamp(),
  `permission_level_FK` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`user_id_PK`),
  UNIQUE KEY `usertb_unique` (`username`),
  KEY `usertb_permissiontb_FK_IDX` (`permission_level_FK`),
  CONSTRAINT `usertb_permissiontb_FK` FOREIGN KEY (`permission_level_FK`) REFERENCES `permissiontb` (`permission_id_PK`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.decktb definition

CREATE TABLE `decktb` (
  `deck_id_PK` bigint(20) unsigned NOT NULL,
  `deck_name` varchar(100) NOT NULL DEFAULT 'New Deck',
  `deck_description` text DEFAULT NULL,
  `deck_created_date` datetime NOT NULL DEFAULT current_timestamp(),
  `deck_contain_card` int(11) NOT NULL DEFAULT 0,
  `deck_lastest_updated` datetime NOT NULL DEFAULT current_timestamp(),
  `deck_clone_perm` tinyint(1) NOT NULL DEFAULT 0,
  `deck_is_public` bit(1) NOT NULL DEFAULT b'0',
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  `theme_id_FK` bigint(20) unsigned DEFAULT NULL,
  `modifier_id_FK` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`deck_id_PK`),
  KEY `decktb_usertb_FK_IDX` (`user_id_FK`),
  KEY `decktb_deck_themetb_FK` (`theme_id_FK`),
  KEY `decktb_theme_modifiertb_FK` (`modifier_id_FK`),
  CONSTRAINT `decktb_theme_modifiertb_FK` FOREIGN KEY (`modifier_id_FK`) REFERENCES `theme_modifiertb` (`modifier_id_PK`),
  CONSTRAINT `decktb_themetb_FK` FOREIGN KEY (`theme_id_FK`) REFERENCES `themetb` (`theme_id_PK`),
  CONSTRAINT `decktb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.forktb definition

CREATE TABLE `forktb` (
  `deck_id_FK` bigint(20) unsigned DEFAULT NULL,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  KEY `forktb_decktb_fk` (`deck_id_FK`),
  KEY `forktb_usertb_fk` (`user_id_FK`),
  CONSTRAINT `forktb_decktb_fk` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `forktb_usertb_fk` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- vo_card_test.inboxtb definition

CREATE TABLE `inboxtb` (
  `inbox_id_PK` bigint(20) unsigned NOT NULL,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp(),
  `updated_date` datetime DEFAULT current_timestamp(),
  `is_closed` tinyint(1) NOT NULL DEFAULT 0,
  `inbox_type` int(10) unsigned NOT NULL,
  `exp_date` datetime NOT NULL,
  PRIMARY KEY (`inbox_id_PK`),
  KEY `inboxtb_usertb_FK` (`user_id_FK`),
  KEY `inboxtb_exp_date_IDX` (`exp_date`) USING BTREE,
  CONSTRAINT `inboxtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.message_boxtb definition

CREATE TABLE `message_boxtb` (
  `message_id_PK` bigint(20) unsigned NOT NULL,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  `inbox_id_FK` bigint(20) unsigned DEFAULT NULL,
  `subject` text DEFAULT NULL,
  `content` text DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`message_id_PK`),
  KEY `message_boxtb_usertb_FK` (`user_id_FK`),
  KEY `message_boxtb_inboxtb_FK` (`inbox_id_FK`),
  KEY `message_boxtb_created_date_IDX` (`created_date`) USING BTREE,
  CONSTRAINT `message_boxtb_inboxtb_FK` FOREIGN KEY (`inbox_id_FK`) REFERENCES `inboxtb` (`inbox_id_PK`),
  CONSTRAINT `message_boxtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.moderation_action definition

CREATE TABLE `moderation_action` (
  `action_id_PK` bigint(20) unsigned NOT NULL,
  `action_type` varchar(255) DEFAULT NULL,
  `action_message` text DEFAULT NULL,
  `action_time_stamp` datetime NOT NULL DEFAULT current_timestamp(),
  `user_id_FK` bigint(20) unsigned DEFAULT 0,
  PRIMARY KEY (`action_id_PK`),
  KEY `moderation_action_usertb_FK_IDX` (`user_id_FK`),
  CONSTRAINT `moderation_action_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.notificationtb definition

CREATE TABLE `notificationtb` (
  `notification_id_PK` bigint(20) unsigned NOT NULL,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  `inbox_id_FK` bigint(20) unsigned DEFAULT NULL,
  `is_viewed` tinyint(1) NOT NULL DEFAULT 0,
  `pushed_date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`notification_id_PK`),
  KEY `notificationtb_usertb_FK` (`user_id_FK`),
  KEY `notificationtb_inboxtb_FK` (`inbox_id_FK`),
  KEY `notificationtb_pushed_date_IDX` (`pushed_date`) USING BTREE,
  CONSTRAINT `notificationtb_inboxtb_FK` FOREIGN KEY (`inbox_id_FK`) REFERENCES `inboxtb` (`inbox_id_PK`),
  CONSTRAINT `notificationtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.reviewtb definition

CREATE TABLE `reviewtb` (
  `deck_id_FK` bigint(20) unsigned DEFAULT NULL,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  `review_action` bit(2) NOT NULL DEFAULT b'0',
  `lastest_review` datetime NOT NULL DEFAULT current_timestamp(),
  KEY `reviewtb_decktb_FK_IDX` (`deck_id_FK`),
  KEY `reviewtb_usertb_FK_IDX` (`user_id_FK`),
  CONSTRAINT `reviewtb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `reviewtb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.sessiontb definition

CREATE TABLE `sessiontb` (
  `session_id_PK` varchar(64) NOT NULL,
  `user_id_FK` bigint(20) unsigned NOT NULL,
  `refresh_token_hash` varchar(100) NOT NULL,
  `expires_at` datetime NOT NULL DEFAULT (current_timestamp() + interval 30 day),
  `remember_me` tinyint(1) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`session_id_PK`),
  KEY `session_usertb_FK_IDX` (`user_id_FK`),
  CONSTRAINT `session_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.stattb definition

CREATE TABLE `stattb` (
  `stat_id_PK` bigint(20) unsigned NOT NULL,
  `stat_streak` int(10) unsigned NOT NULL DEFAULT 0,
  `stat_most_learn_deck` smallint(5) unsigned DEFAULT NULL,
  `stat_total_number_of_review` int(10) unsigned NOT NULL DEFAULT 0,
  `user_id_FK` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`stat_id_PK`),
  KEY `stattb_usertb_FK_IDX` (`user_id_FK`),
  CONSTRAINT `stattb_usertb_FK` FOREIGN KEY (`user_id_FK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.card_leveltb definition

CREATE TABLE `card_leveltb` (
  `level_id_PK` bigint(20) unsigned NOT NULL,
  `level_weight` int(11) NOT NULL DEFAULT 1,
  `level_name` varchar(10) NOT NULL DEFAULT 'New Level',
  `deck_id_FK` bigint(20) unsigned NOT NULL,
  `theme_id_FK` bigint(20) unsigned DEFAULT NULL,
  `modifier_id_FK` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`level_id_PK`),
  KEY `card_leveltb_decktb_FK_IDX` (`deck_id_FK`),
  KEY `card_leveltb_themetb_FK` (`theme_id_FK`),
  KEY `card_leveltb_theme_modifiertb_FK` (`modifier_id_FK`),
  CONSTRAINT `card_leveltb_decktb_FK` FOREIGN KEY (`deck_id_FK`) REFERENCES `decktb` (`deck_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `card_leveltb_theme_modifiertb_FK` FOREIGN KEY (`modifier_id_FK`) REFERENCES `theme_modifiertb` (`modifier_id_PK`),
  CONSTRAINT `card_leveltb_themetb_FK` FOREIGN KEY (`theme_id_FK`) REFERENCES `themetb` (`theme_id_PK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.cardtb definition

CREATE TABLE `cardtb` (
  `card_id_PK` bigint(20) unsigned NOT NULL,
  `level_id_FK` bigint(20) unsigned NOT NULL,
  `card_word` varchar(255) NOT NULL DEFAULT 'New Card',
  PRIMARY KEY (`card_id_PK`),
  KEY `cardtb_card_leveltb_FK_IDX` (`level_id_FK`),
  CONSTRAINT `cardtb_card_leveltb_FK` FOREIGN KEY (`level_id_FK`) REFERENCES `card_leveltb` (`level_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.learned_cardtb definition

CREATE TABLE `learned_cardtb` (
  `learn_card_ease_factor` float NOT NULL DEFAULT 2.5,
  `learn_card_interval` smallint(5) unsigned NOT NULL DEFAULT 1,
  `learned_card_latest_review` datetime NOT NULL DEFAULT current_timestamp(),
  `card_id_PK` bigint(20) unsigned DEFAULT NULL,
  `user_id_PK` bigint(20) unsigned DEFAULT NULL,
  KEY `learned_cardtb_cardtb_FK_IDX` (`card_id_PK`),
  KEY `learned_cardtb_usertb_FK_IDX` (`user_id_PK`),
  CONSTRAINT `learned_cardtb_cardtb_FK` FOREIGN KEY (`card_id_PK`) REFERENCES `cardtb` (`card_id_PK`) ON DELETE CASCADE,
  CONSTRAINT `learned_cardtb_usertb_FK` FOREIGN KEY (`user_id_PK`) REFERENCES `usertb` (`user_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.postb definition

CREATE TABLE `postb` (
  `pos_id_PK` bigint(20) unsigned NOT NULL,
  `card_id_FK` bigint(20) unsigned NOT NULL,
  `part_of_speech` varchar(255) NOT NULL DEFAULT 'None',
  PRIMARY KEY (`pos_id_PK`),
  KEY `postb_cardtb_FK_IDX` (`card_id_FK`),
  CONSTRAINT `postb_cardtb_FK` FOREIGN KEY (`card_id_FK`) REFERENCES `cardtb` (`card_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- vo_card_test.definitiontb definition

CREATE TABLE `definitiontb` (
  `definition_id_PK` bigint(20) unsigned NOT NULL,
  `pos_id_FK` bigint(20) unsigned NOT NULL,
  `definition` text NOT NULL,
  PRIMARY KEY (`definition_id_PK`),
  KEY `definitiontb_postb_FK_IDX` (`pos_id_FK`),
  CONSTRAINT `definitiontb_postb_FK` FOREIGN KEY (`pos_id_FK`) REFERENCES `postb` (`pos_id_PK`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
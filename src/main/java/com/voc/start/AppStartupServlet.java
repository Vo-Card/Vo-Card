package com.voc.start;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.voc.database.DatabaseUtils;
import com.voc.database.DeckManager;
import com.voc.jwt.JwtManager;
import com.voc.server.Snowflake;
import com.voc.server.WorkerSessionManager;

import static com.voc.utils.AnsiColor.*;

/**
 * AppStartupServlet now implements ServletContextListener to handle
 * both startup and shutdown properly.
 */
@WebListener
public class AppStartupServlet implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[" + BOLD + BLUE + "VO-CARD" + RESET + "] App Startup Initializing...");

        Map<String, String> data = null;

        // Try JSON config first
        try (InputStream input = DatabaseUtils.class.getClassLoader()
                .getResourceAsStream("config/database.json")) {
            if (input != null) {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(input, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
                System.out.println(TAG_SUCCESS + "Loaded config from database.json");
            }
        } catch (Exception e) {
            System.err.println(TAG_WARNING + "Failed to load database.json, will try ENV.");
        }

        // Fallback to ENV if JSON not found
        if (data == null) {
            try {
                data = Map.of(
                        "DB_URL", System.getenv("DB_URL"),
                        "DB_NAME", System.getenv("DB_NAME"),
                        "DB_USER", System.getenv("DB_USER"),
                        "DB_PASSWORD", System.getenv("DB_PASSWORD"),
                        "ROOT_USERNAME", System.getenv("ROOT_USERNAME"),
                        "ROOT_DISPLAYNAME", System.getenv("ROOT_DISPLAYNAME")
                );

                if (data.values().stream().anyMatch(v -> v == null)) {
                    throw new IllegalStateException("Missing ENV variables for database initialization.");
                }

                System.out.println(TAG_SUCCESS + "Loaded config from ENV.");
            } catch (Exception e) {
                System.err.println(TAG_ERROR + " No valid config source (JSON or ENV).");
                throw new RuntimeException(e);
            }
        }

        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("Could not determine local hostname: " + e.getMessage());
        }

        try {
            // Initialize Worker and Database
            DatabaseUtils.initDatabase(data);
            WorkerSessionManager.InitializeWorker(hostname);
            
            // Initialize Snowflake and Administrator after database
            Snowflake.InitializeSnowflake(WorkerSessionManager.getWorkerId());
            DatabaseUtils.initializeAdministrator();

            System.out.println(TAG_SUCCESS + "Database initialized successfully.");

            DeckManager.initializeDeckTable();
            System.out.println(TAG_SUCCESS + "Default deck ensured.");
            
            JwtManager.initializeKeys();
            System.out.println(TAG_SUCCESS + "JWT keys initialized.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(TAG_ERROR + "Failed to initialize default configuration.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[" + BOLD + BLUE + "VO-CARD" + RESET + "] App shutting down, cleaning resources...");

        // Stop scheduler in WorkerSessionManager
        WorkerSessionManager.shutdownScheduler();

        // Other cleanup if needed
        System.out.flush();
    }
}

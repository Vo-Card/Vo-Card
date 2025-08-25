package com.voc.start;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.voc.database.DatabaseUtils;
import com.voc.database.DeckManager;
import static com.voc.utils.AnsiColor.*;

@WebServlet(urlPatterns = "/startup", loadOnStartup = 1)
public class AppStartupServlet extends HttpServlet {

    /**
     * Initialize the servlet and set up the database connection.
     * <p>
     * This method is called when the servlet is first loaded into memory. It reads
     * the database configuration from a JSON file and initializes the database
     * connection. It also ensures that the default deck is created in the database.
     * </p>
     * @param config The servlet configuration object.
     * @throws ServletException if an error occurs during initialization.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("[" + BOLD + BLUE + "VO-CARD" + RESET + "] App Startup Initializing...");

        Map<String, String> data = null;

        // Try JSON config first
        try (InputStream input = DatabaseUtils.class.getClassLoader()
                .getResourceAsStream("config/database.json")) {
            if (input != null) {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(input, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
                System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Loaded config from database.json");
            }
        } catch (Exception e) {
            System.err.println("[" + BOLD + YELLOW + "VO-CARD" + RESET + "] Failed to load database.json, will try ENV.");
        }

        // Fallback to ENV if JSON not found
        if (data == null) {
            try {
                data = Map.of(
                    "DB_URL", System.getenv("DB_URL"),
                    "DB_NAME", System.getenv("DB_NAME"),
                    "DB_USER", System.getenv("DB_USER"),
                    "DB_PASSWORD", System.getenv("DB_PASSWORD"),
                    "CHECK_DATABASE", System.getenv("CHECK_DATABASE"),
                    "ROOT_USERNAME", System.getenv("ROOT_USERNAME"),
                    "ROOT_DISPLAYNAME", System.getenv("ROOT_DISPLAYNAME")
                );

                if (data.values().stream().anyMatch(v -> v == null)) {
                    throw new IllegalStateException("Missing ENV variables for database initialization.");
                }

                System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Loaded config from ENV.");
            } catch (Exception e) {
                throw new ServletException("[" + BOLD + RED + "VO-CARD" + RESET + "] No valid config source (JSON or ENV).", e);
            }
        }

        try {
            DatabaseUtils.initDatabase(data);
            System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Database initialized successfully.");

            DeckManager.initializeDeckTable();
            System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Default deck ensured.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("[" + BOLD + RED + "VO-CARD" + RESET + "] Failed to initialize default configuration.", e);
        }
    }
}
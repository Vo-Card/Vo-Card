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

        // Load database configuration from JSON file
        try (InputStream input = DatabaseUtils.class.getClassLoader()
                .getResourceAsStream("config/database.json")) {
            if (input == null) {
                throw new RuntimeException("database.json not found or not configured.");
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(input, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});

            DatabaseUtils.initDatabase(data); // Create a new init method in DatabaseUtils
            System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Database initialized successfully.");

            DeckManager.initializeDeckTable();
            System.out.println("[" + BOLD + GREEN + "VO-CARD" + RESET + "] Default deck ensured.");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("[" + BOLD + RED + "VO-CARD" + RESET + "] Failed to initialize default configuration.", e);
        }
    }
}
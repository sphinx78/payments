package com.expensetracker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads database connection settings from db.properties (project folder or classpath)
 * or from environment variables. Use this so the project runs on any computer
 * without editing Java source code.
 *
 * Order of precedence:
 * 1. db.properties file in current working directory (when you run from expense-tracker folder)
 * 2. db.properties on classpath (e.g. src/main/resources)
 * 3. Environment variables: EXPENSE_TRACKER_DB_URL, EXPENSE_TRACKER_DB_USER, EXPENSE_TRACKER_DB_PASSWORD
 * 4. Defaults: localhost:3306, user root, empty password
 */
public class DatabaseConfig {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static volatile String url;
    private static volatile String serverUrl;
    private static volatile String user;
    private static volatile String password;

    static {
        load();
    }

    public static void load() {
        Properties p = new Properties();

        // 1. Try current working directory (project folder when running mvn from expense-tracker)
        File fileInCwd = new File("db.properties");
        if (fileInCwd.exists()) {
            try (InputStream in = new FileInputStream(fileInCwd)) {
                p.load(in);
            } catch (Exception e) {
                System.err.println("Could not read db.properties from current directory: " + e.getMessage());
            }
        }

        // 2. Try classpath (e.g. src/main/resources/db.properties)
        if (p.isEmpty()) {
            try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in != null) {
                    p.load(in);
                }
            } catch (Exception e) {
                // ignore
            }
        }

        url = getPropOrEnv(p, "db.url", "EXPENSE_TRACKER_DB_URL", DEFAULT_URL);
        serverUrl = getPropOrEnv(p, "db.url.server", "EXPENSE_TRACKER_DB_URL_SERVER", DEFAULT_SERVER_URL);
        user = getPropOrEnv(p, "db.user", "EXPENSE_TRACKER_DB_USER", DEFAULT_USER);
        password = getPropOrEnv(p, "db.password", "EXPENSE_TRACKER_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String getPropOrEnv(Properties p, String propKey, String envKey, String defaultValue) {
        String v = p.getProperty(propKey);
        if (v != null && !v.trim().isEmpty()) return v.trim();
        v = System.getenv(envKey);
        if (v != null && !v.trim().isEmpty()) return v.trim();
        return defaultValue;
    }

    public static String getUrl() {
        return url;
    }

    /** URL without database name (for creating the DB in DatabaseSetup). */
    public static String getServerUrl() {
        return serverUrl;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
}

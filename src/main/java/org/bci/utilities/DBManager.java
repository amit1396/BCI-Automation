package org.bci.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bci.base.BaseClass;

public class DBManager {

    private static final Logger logger = LogManager.getLogger(DBManager.class);
    private static Connection connection = null;

    /**
     * Establish connection to SQL Server (SSMS)
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                
                // ================= SAFE PROPERTY FALLBACK =================
                Properties prop = BaseClass.getProp();
                if (prop == null) {
                    logger.warn("BaseClass.prop is null. Loading config.properties standalone...");
                    prop = new Properties();
                    String path = System.getProperty("user.dir") + "/src/test/resources/config.properties";
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(path)) {
                        prop.load(fis);
                    }
                }
                // ==========================================================

                String server = prop.getProperty("db.server", "localhost");
                String port = prop.getProperty("db.port", "1433");
                String dbName = prop.getProperty("db.name");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");

                String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;", 
                        server, port, dbName);

                logger.info("Connecting to SQL Server database at: " + server);
                connection = DriverManager.getConnection(url, username, password);
                logger.info("Successfully connected to SQL Server!");
            }
        } catch (Exception e) {
            logger.error("Failed to connect to SQL Server: " + e.getMessage(), e);
            throw new RuntimeException("Database connection failed", e);
        }
        return connection;
    }

    /**
     * Execute a SELECT query and return results as a List of Maps (Column Name -> Value)
     */
    public static List<Map<String, Object>> executeQuery(String query, Object... params) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set parameters dynamically if passed
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    resultList.add(row);
                }
            }
        } catch (SQLException e) {
            logger.error("Query execution failed: " + query, e);
            throw new RuntimeException("Database query failed", e);
        }
        return resultList;
    }

    /**
     * Execute INSERT, UPDATE, or DELETE queries
     */
    public static int executeUpdate(String query, Object... params) {
        int rowsAffected = 0;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rowsAffected = pstmt.executeUpdate();
            logger.info("Update executed successfully. Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            logger.error("Update execution failed: " + query, e);
            throw new RuntimeException("Database update failed", e);
        }
        return rowsAffected;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("SQL Server connection closed.");
            }
        } catch (SQLException e) {
            logger.warn("Failed to close database connection: " + e.getMessage());
        }
    }
}
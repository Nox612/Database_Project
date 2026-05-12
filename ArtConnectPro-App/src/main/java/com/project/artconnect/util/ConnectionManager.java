package com.project.artconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.project.artconnect.config.*;

/**
 * Utility class to manage JDBC connections.
 * TODO: Students must implementation the getConnection logic.
 */
public class ConnectionManager {

    /**
     * Provides a connection to the MySQL database.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        if (conn != null)
            return conn;
        throw new UnsupportedOperationException("ConnectionManager.getConnection() not implemented — see TODO");
    }
}

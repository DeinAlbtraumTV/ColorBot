package database;

import commons.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private final static Logger logger = LoggerFactory.getLogger(SQLHandler.class);

    public static void connect(boolean logOutput) {

        connection = null;

        try {
            if (logOutput) {

                logger.info("Attempting to connect to database");

                String url = "jdbc:mysql://" + Database.host + "/" + Database.database + "?user=" + Database.user + "&password="+ Database.password + "&useLegacyDatetimeCode=false&serverTimezone=" + Database.timezone;

                connection = DriverManager.getConnection(url);

                logger.info("Connected to database");
            }
            else {
                String url = "jdbc:mysql://" + Database.host + "/" + Database.database + "?user=" + Database.user + "&password="+ Database.password + "&useLegacyDatetimeCode=false&serverTimezone=" + Database.timezone;

                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            logger.error("An error occurred while trying to connect to database", e);
        }
    }

    public static void disconnect(boolean logOutput) {

        try {
            if (logOutput) {
                logger.info("Attempting to disconnect from database");

                if (connection != null) {
                    connection.close();
                }

                logger.info("Disconnected from database");
            }
            else {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred while trying to disconnect from database", e);
        }
    }

    public static void onUpdate(String sql) {
        try {
            /*if (sql.startsWith("DROP")) {
                disconnect(false);
                connect(false);
            }*/
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error("An error occurred while trying to update database", e);
        }
    }

    public static ResultSet onQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.closeOnCompletion();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.error("An error occurred while trying to retrieve data from database", e);
        }
        return null;
    }
}
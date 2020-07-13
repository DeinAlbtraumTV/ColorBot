package database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLHandler {

    private static Connection connection;

    public static void connect(boolean logOutput) {

        connection = null;

        try {
            if (logOutput) {

                System.out.println("[ColorBot ColorBot-Thread] INFO - Attempting to connect to database");

                File databaseFile = new File("database.db");

                if (!databaseFile.exists()) {
                    if (!databaseFile.createNewFile()) {
                        throw new IOException();
                    }
                }

                String url = "jdbc:sqlite:" + databaseFile.getPath();

                connection = DriverManager.getConnection(url);

                System.out.println("[ColorBot ColorBot-Thread] INFO - Connected to database");
            }
            else {
                File databaseFile = new File("database.db");

                if (!databaseFile.exists()) {
                    if (!databaseFile.createNewFile()) {
                        throw new IOException();
                    }
                }

                String url = "jdbc:sqlite:" + databaseFile.getPath();

                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException | IOException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to connect to database");
            e.printStackTrace();
        }
    }

    public static void disconnect(boolean logOutput) {

        try {
            if (logOutput) {
                System.out.println("[ColorBot ColorBot-Thread] INFO - Attempting to disconnect from database");

                if (connection != null) {
                    connection.close();
                }

                System.out.println("[ColorBot ColorBot-Thread] INFO - Disconnected from database");
            }
            else {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to disconnect from database");
            e.printStackTrace();
        }
    }

    public static void onUpdate(String sql) {
        try {
            if (sql.startsWith("DROP")) {
                disconnect(false);
                connect(false);
            }
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to update database");
            e.printStackTrace();
        }
    }

    public static ResultSet onQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.closeOnCompletion();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to retrieve data from database");
            e.printStackTrace();
        }
        return null;
    }
}
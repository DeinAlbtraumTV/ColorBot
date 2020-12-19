package database;

import commons.Database;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;

    public static void connect(boolean logOutput) {

        connection = null;

        try {
            if (logOutput) {

                System.out.println("[ColorBot Database] INFO - Attempting to connect to database");

                String url = "jdbc:mysql://" + Database.host + "/" + Database.database + "?user=" + Database.user + "&password="+ Database.password + "&useLegacyDatetimeCode=false&serverTimezone=" + Database.timezone;

                connection = DriverManager.getConnection(url);

                System.out.println("[ColorBot Database] INFO - Connected to database");
            }
            else {
                String url = "jdbc:mysql://" + Database.host + "/" + Database.database + "?user=" + Database.user + "&password="+ Database.password + "&useLegacyDatetimeCode=false&serverTimezone=" + Database.timezone;

                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot Database] ERROR - An error occurred while trying to connect to database");
            e.printStackTrace();
        }
    }

    public static void disconnect(boolean logOutput) {

        try {
            if (logOutput) {
                System.out.println("[ColorBot Database] INFO - Attempting to disconnect from database");

                if (connection != null) {
                    connection.close();
                }

                System.out.println("[ColorBot Database] INFO - Disconnected from database");
            }
            else {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot Database] ERROR - An error occurred while trying to disconnect from database");
            e.printStackTrace();
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
            System.out.println("[ColorBot Database] ERROR - An error occurred while trying to update database");
            e.printStackTrace();
        }
    }

    public static ResultSet onQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.closeOnCompletion();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("[ColorBot Database] ERROR - An error occurred while trying to retrieve data from database");
            e.printStackTrace();
        }
        return null;
    }
}
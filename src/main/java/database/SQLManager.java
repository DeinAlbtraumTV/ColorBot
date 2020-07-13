package database;

public class SQLManager {

    public static void onCreate() {

        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS customPrefix(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER UNIQUE, prefix VARCHAR DEFAULT \"!color\")");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS colorRolePosition(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER UNIQUE, roleid INTEGER)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS autoWhitelist(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER UNIQUE, state INTEGER DEFAULT 1)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS assignAmount(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER UNIQUE, amount INTEGER DEFAULT 0)");

    }
}
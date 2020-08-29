package database;

public class SQLManager {

    public static void onCreate() {

        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS customPrefix(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNIQUE, prefix VARCHAR(2001) DEFAULT \"!color\")");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS colorRolePosition(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNIQUE, roleid BIGINT)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS autoWhitelist(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNIQUE, state INTEGER DEFAULT 1)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS assignAmount(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNIQUE, amount BIGINT DEFAULT 0)");

    }
}
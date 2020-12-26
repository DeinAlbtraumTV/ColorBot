package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLManager {

    private final static Logger logger = LoggerFactory.getLogger(SQLManager.class);

    public static void onCreate() {

        logger.info("Setting up Database");
        SQLHandler.onUpdate("DROP TABLE IF EXISTS guilds");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS customPrefix(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNSIGNED UNIQUE, prefix VARChAR(25) DEFAULT \"!color\")");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS colorRolePosition(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNSIGNED UNIQUE, roleid BIGINT)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS autoWhitelist(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNSIGNED UNIQUE, state INTEGER DEFAULT 1)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS assignAmount(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNSIGNED UNIQUE, amount BIGINT UNSIGNED DEFAULT 0)");
        SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS guilds(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, guildid BIGINT UNSIGNED UNIQUE)");
        logger.info("Setup of Database finished");

    }
}
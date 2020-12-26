package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class OnReadyListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(OnReadyListener.class);

    public static Timer timer = new Timer();
    public static Timer databaseTimer = new Timer();

    public void onReady(ReadyEvent event) {

        JDA jda = event.getJDA();

        TimerTask statusUpdate = new TimerTask() {
            @Override
            public void run() {
                if (Objects.requireNonNull(jda.getPresence().getActivity()).getName().equals("Color of the Matrix")) {
                    jda.getPresence().setActivity(Activity.playing("Version: " + ColorBot.version));
                } else {
                    jda.getPresence().setActivity(Activity.watching("Color of the Matrix"));
                }
            }
        };

        timer.scheduleAtFixedRate(statusUpdate, 300000, 300000);

        TimerTask repairDatabase = new TimerTask() {
            @Override
            public void run() {

                if (jda.getShardInfo().getShardId() == 0) {
                    ColorBot.members = 0L;
                    ColorBot.servers = 0L;
                }

                logger.info("Starting Database Repair");

                Instant start = Instant.now();

                long invalidIds = 0;

                for (Guild g : event.getJDA().getGuilds()) {

                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + g.getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNSIGNED UNIQUE)");
                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + g.getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, word VARCHAR(25) UNIQUE)");
                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + g.getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNSIGNED UNIQUE)");
                    SQLHandler.onUpdate("INSERT IGNORE INTO customPrefix(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT IGNORE INTO colorRolePosition(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT IGNORE INTO autoWhitelist(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT IGNORE INTO assignAmount(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT IGNORE INTO guilds(guildid) VALUES(" + g.getIdLong() + ")");

                    ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + g.getIdLong());

                    try {
                        if (result != null && !result.isClosed() && result.next()) {
                            try {
                                do {
                                    String roleId = Objects.requireNonNull(result).getString("roleid");
                                    try {
                                        Objects.requireNonNull(g.getRoleById(roleId)).getAsMention();
                                    } catch (NullPointerException nullE) {
                                        SQLHandler.onUpdate("DELETE FROM _" + g.getIdLong() + " WHERE roleid=" + roleId);
                                        invalidIds++;
                                    }
                                } while (result.next());
                            } catch (SQLException e) {
                                logger.error("An error occurred while checking for invalid database entries", e);
                            }
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while checking for invalid database entries", e);
                    }
                    ResultSet resultSet = SQLHandler.onQuery("SELECT roleid FROM colorRolePosition WHERE guildid=" + g.getIdLong());

                    try {
                        if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
                            try {
                                do {
                                    String roleId = Objects.requireNonNull(resultSet).getString("roleid");
                                    try {
                                        if (roleId != null) {
                                            Objects.requireNonNull(g.getRoleById(roleId)).getAsMention();
                                        }
                                    } catch (NullPointerException nullE) {
                                        SQLHandler.onUpdate("UPDATE colorRolePosition SET roleid=" + null + " WHERE guildid=" + g.getIdLong());
                                        invalidIds++;
                                    }
                                } while (resultSet.next());
                            } catch (SQLException e) {
                                logger.error("An error occurred while checking for invalid database entries", e);
                            }
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while checking for invalid database entries", e);
                    }

                    ResultSet createRestrictions = SQLHandler.onQuery("SELECT roleid FROM _" + g.getIdLong());

                    try {
                        if (createRestrictions != null && !createRestrictions.isClosed() && createRestrictions.next()) {
                            try {
                                do {
                                    String roleId = Objects.requireNonNull(createRestrictions).getString("roleid");
                                    try {
                                        Objects.requireNonNull(g.getRoleById(roleId)).getAsMention();
                                    } catch (NullPointerException nullE) {
                                        SQLHandler.onUpdate("DELETE FROM createRestrictions_" + g.getIdLong() + " WHERE roleid=" + roleId);
                                        invalidIds++;
                                    }
                                } while (createRestrictions.next());
                            } catch (SQLException e) {
                                logger.error("An error occurred while checking for invalid database entries", e);
                            }
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while checking for invalid database entries", e);
                    }

                    try {
                        if (result != null) {
                            result.close();
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while closing a resultSet", e);
                    }
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while closing a resultSet", e);
                    }
                    try {
                        if (createRestrictions != null) {
                            createRestrictions.close();
                        }
                    } catch (SQLException e) {
                        logger.error("An error occurred while closing a resultSet", e);
                    }

                }

                logger.info("Found and deleted {} invalid Role-Ids in Shard: {}", invalidIds, event.getJDA().getShardInfo().getShardId());

                for (Guild g : event.getJDA().getGuilds()) {
                    ColorBot.servers++;
                    ColorBot.members = ColorBot.members + g.getMembers().size();
                }

                logger.info("So many Servers: {} and so many Members: {} after Shard: {} loaded", ColorBot.servers, ColorBot.members, event.getJDA().getShardInfo().getShardId());
                logger.info("Database Repair finished at {}. Next one scheduled in 1h",  new Timestamp(System.currentTimeMillis()));


                Instant end = Instant.now();

                if (Duration.between(start, end).toMillis() / 1000 > 0) {
                    long duration = Duration.between(start, end).toMillis() / 1000;

                    logger.info("The Database Repair took: {} Seconds", duration);

                }
                else {
                    long duration = Duration.between(start, end).toMillis();

                    logger.info("The Database Repair took: {} Milliseconds", duration);

                }
            }
        };

        databaseTimer.scheduleAtFixedRate(repairDatabase, 0, 3600000);

    }
}
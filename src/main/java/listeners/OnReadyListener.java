package listeners;
import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class OnReadyListener extends ListenerAdapter {

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

                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] Info - Starting Database Repair");

                Instant start = Instant.now();

                long invalidIds = 0;

                for (Guild g : event.getJDA().getGuilds()) {

                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + g.getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, roleid INTEGER UNIQUE)");
                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + g.getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, word VARCHAR UNIQUE)");
                    SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + g.getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, roleid INTEGER UNIQUE)");
                    SQLHandler.onUpdate("INSERT OR IGNORE INTO customPrefix(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT OR IGNORE INTO colorRolePosition(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT OR IGNORE INTO autoWhitelist(guildid) VALUES(" + g.getIdLong() + ")");
                    SQLHandler.onUpdate("INSERT OR IGNORE INTO assignAmount(guildid) VALUES(" + g.getIdLong() + ")");

                    ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + g.getIdLong());

                    try {
                        if (result != null && !result.isClosed()) {
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
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                                e.printStackTrace();
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                        e.printStackTrace();
                    }
                    ResultSet resultSet = SQLHandler.onQuery("SELECT roleid FROM colorRolePosition WHERE guildid=" + g.getIdLong());

                    try {
                        if (resultSet != null && !resultSet.isClosed()) {
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
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                                e.printStackTrace();
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                        e.printStackTrace();
                    }

                    ResultSet createRestrictions = SQLHandler.onQuery("SELECT roleid FROM _" + g.getIdLong());

                    try {
                        if (createRestrictions != null && !createRestrictions.isClosed()) {
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
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                                e.printStackTrace();
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while checking for invalid database entries:");
                        e.printStackTrace();
                    }

                    try {
                        if (result != null) {
                            result.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while closing a resultSet");
                    }
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while closing a resultSet");
                    }
                    try {
                        if (createRestrictions != null) {
                            createRestrictions.close();
                        }
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while closing a resultSet");
                    }

                }

                System.out.println("[ColorBot ColorBot-Thread] INFO - Found and deleted " + invalidIds + " invalid Role-Ids in Shard: " + event.getJDA().getShardInfo().getShardId());

                for (Guild g : event.getJDA().getGuilds()) {
                    ColorBot.servers++;
                    ColorBot.members = ColorBot.members + g.getMembers().size();
                }

                System.out.println("[ColorBot ColorBot-Thread] INFO - So many Servers: " + ColorBot.servers + " and so many Members: " + ColorBot.members + " after Shard: " + event.getJDA().getShardInfo().getShardId() + " loaded");

                System.out.println("[ColorBot ColorBot-Thread] Info - Database Repair finished. Next one scheduled in 1h");
                System.out.println("[ColorBot ColorBot-Thread] Info - Time when finished: " + new Timestamp(System.currentTimeMillis()));
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");

                Instant end = Instant.now();

                if (Duration.between(start, end).toMillis() / 1000 > 0) {
                    long duration = Duration.between(start, end).toMillis() / 1000;

                    System.out.println("[ColorBot ColorBot-Thread] Info - The Database Repair took: " + duration + " Seconds");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }
                else {
                    long duration = Duration.between(start, end).toMillis();

                    System.out.println("[ColorBot ColorBot-Thread] Info - The Database Repair took: " + duration + " Milliseconds");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }
            }
        };

        databaseTimer.scheduleAtFixedRate(repairDatabase, 0, 3600000);

    }
}
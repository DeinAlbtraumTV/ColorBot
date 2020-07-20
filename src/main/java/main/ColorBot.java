package main;

import commons.Tokens;
import database.SQLHandler;
import database.SQLManager;
import listeners.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ColorBot {

    public static String version = "0.7.04";
    public static Color orange = new Color(255, 127, 0);
    public static long servers = 0L;
    public static long members = 0L;
    public static boolean shutdown = false;

    private static ShardManager ShardManager;

    public static void main(String[] args) {
        new ColorBot(args);
    }

    private ColorBot(String[] args) {

        System.out.println("[ColorBot ColorBot-Thread] INFO - Starting initiated");

        SQLHandler.connect(true);
        SQLManager.onCreate();

        DefaultShardManagerBuilder ShardedBuilder;

        if (args.length > 0 && args[0].equals("testing")) {
            ShardedBuilder = DefaultShardManagerBuilder.create(Tokens.testing, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        }
        else {
            ShardedBuilder = DefaultShardManagerBuilder.create(Tokens.normal, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        }


        ShardedBuilder.addEventListeners(new OnReadyListener());
        ShardedBuilder.addEventListeners(new CommandListener());
        ShardedBuilder.addEventListeners(new OnGuildJoinListener());
        ShardedBuilder.addEventListeners(new OnGuildLeaveListener());
        ShardedBuilder.addEventListeners(new OnGuildBanListener());
        ShardedBuilder.addEventListeners(new OnRoleDeleted());

        ShardedBuilder.setShardsTotal(4);

        ShardedBuilder.setStatus(OnlineStatus.ONLINE);
        ShardedBuilder.setActivity(Activity.watching("Color of the Matrix"));

        try {
            ShardManager = ShardedBuilder.build();
            System.out.println("[ColorBot ColorBot-Thread] INFO - Starting finished");
        } catch (LoginException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to login to discord");
        }

        startShutdownThread();
        System.out.println("[ColorBot ColorBot-Thread] INFO - Shards running: " + ShardManager.getShardsRunning());
        System.out.println("[ColorBot ColorBot-Thread] INFO - Shards queued: " + ShardManager.getShardsQueued());
        System.out.println("[ColorBot ColorBot-Thread] INFO - Shards total: " + ShardManager.getShardsTotal());
    }

    private void startShutdownThread() {

        Thread shutdownThread = new Thread(() -> {

            String input;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                while ((input = reader.readLine()) != null) {
                    if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
                        if (ShardManager != null) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot Read-Thread#01 - Shutdown] INFO - Shutdown initiated");
                            shutdown = true;
                            ShardManager.setStatus(OnlineStatus.OFFLINE);
                            ShardManager.shutdown();
                            OnReadyListener.timer.cancel();
                            OnReadyListener.databaseTimer.cancel();
                        }
                        reader.close();
                        SQLHandler.disconnect(true);
                        System.out.println("[ColorBot Read-Thread#01 - Shutdown] INFO - Shutdown done");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.exit(0);
                    }
                }
            }
            catch (IOException e) {
                if (!e.getMessage().equalsIgnoreCase("Stream closed")) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot Read-Thread#01 - Shutdown] ERROR - An error occurred while trying to read the console input");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }
            }
            catch (Exception ignore){}
        });

        shutdownThread.setName("Read-Thread#01 - Shutdown");
        shutdownThread.start();
    }
}
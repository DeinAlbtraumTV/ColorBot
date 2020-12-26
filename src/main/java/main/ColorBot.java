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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ColorBot {

    public static String version = "0.9.01";
    public static Color orange = new Color(255, 127, 0);
    public static long servers = 0L;
    public static long members = 0L;
    public static boolean shutdown = false;

    private static ShardManager ShardManager;
    private final static Logger logger = LoggerFactory.getLogger(ColorBot.class);

    public static void main(String[] args) {
        try {
            new ColorBot(args);
        } catch (Exception e) {
            logger.error("Unknown exception encountered", e);
        }
    }

    private ColorBot(String[] args) {

        logger.info("Starting initiated");

        SQLHandler.connect(true);
        SQLManager.onCreate();

        DefaultShardManagerBuilder ShardedBuilder;

        if (args.length > 0 && args[0].equals("testing")) {
            logger.warn("Running on testing mode");
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
            logger.info("Starting finished");
        } catch (LoginException e) {
            logger.error("An error occurred while trying to login to discord", e);
        }

        startShutdownThread();
        logger.info("Shards running: {}", ShardManager.getShardsRunning());
        logger.info("Shards queued: {}", ShardManager.getShardsQueued());
        logger.info("Shards total: {}", ShardManager.getShardsTotal());
    }

    private void startShutdownThread() {

        Thread shutdownThread = new Thread(() -> {

            String input;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                while ((input = reader.readLine()) != null) {
                    if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
                        if (ShardManager != null) {
                            logger.info("Shutdown initiated");
                            shutdown = true;
                            ShardManager.setStatus(OnlineStatus.OFFLINE);
                            ShardManager.shutdown();
                            OnReadyListener.timer.cancel();
                            OnReadyListener.databaseTimer.cancel();
                        }
                        reader.close();
                        SQLHandler.disconnect(true);
                        logger.info("Shutdown done");
                        System.exit(0);
                    }
                }
            }
            catch (IOException e) {
                if (!e.getMessage().equalsIgnoreCase("Stream closed")) {
                    logger.error("An error occurred while trying to read the console input", e);
                }
            }
            catch (Exception e){
                logger.error("Unknown exception encountered", e);
            }
        });

        shutdownThread.setName("Read-Thread#01 - Shutdown");
        shutdownThread.start();
    }
}
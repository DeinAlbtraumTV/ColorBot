package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class OnGuildJoinListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(OnGuildJoinListener.class);

    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if (!ColorBot.shutdown) {
            logger.info("Joined new Guild with {} Members", event.getGuild().getMemberCount());
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, word VARCHAR(25) UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNIQUE)");
            SQLHandler.onUpdate("INSERT IGNORE INTO customPrefix(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT IGNORE INTO colorRolePosition(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT IGNORE INTO autoWhitelist(guildid) VALUES(" + event.getGuild().getIdLong()+")");
            SQLHandler.onUpdate("INSERT IGNORE INTO assignAmount(guildid) VALUES(" + event.getGuild().getIdLong() +")");
            SQLHandler.onUpdate("INSERT IGNORE INTO guilds(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
        }
    }
}
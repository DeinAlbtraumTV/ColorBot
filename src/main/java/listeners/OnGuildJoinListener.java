package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnGuildJoinListener extends ListenerAdapter {

    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if (!ColorBot.shutdown) {
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, word VARCHAR(2001) UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNIQUE)");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO customPrefix(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO colorRolePosition(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO autoWhitelist(guildid) VALUES(" + event.getGuild().getIdLong()+")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO assignAmount(guildid) VALUES(" + event.getGuild().getIdLong() +")");
        }
    }
}
package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnGuildJoinListener extends ListenerAdapter {

    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        if (!ColorBot.shutdown) {
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + event.getGuild().getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, roleid INTEGER UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + event.getGuild().getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, word VARCHAR UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + event.getGuild().getIdLong() + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, roleid INTEGER UNIQUE)");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO customPrefix(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO colorRolePosition(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO autoWhitelist(guildid) VALUES(" + event.getGuild().getIdLong()+")");
            SQLHandler.onUpdate("INSERT OR IGNORE INTO assignAmount(guildid) VALUES(" + event.getGuild().getIdLong() +")");
        }
    }
}
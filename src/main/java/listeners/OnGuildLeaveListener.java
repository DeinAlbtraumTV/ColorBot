package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class OnGuildLeaveListener extends ListenerAdapter {

    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        if (!ColorBot.shutdown) {
            SQLHandler.onUpdate("DROP TABLE IF EXISTS _" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DROP TABLE IF EXISTS wordRestrictions_" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DROP TABLE IF EXISTS createRestrictions_" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM customPrefix WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM colorRolePosition WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM autoWhitelist WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM assignAmount WHERE guildid=" + event.getGuild().getIdLong());
        }
    }
}
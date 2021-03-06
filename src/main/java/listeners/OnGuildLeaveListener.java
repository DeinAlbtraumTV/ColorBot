package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class OnGuildLeaveListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(OnGuildLeaveListener.class);

    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        if (!ColorBot.shutdown) {
            logger.info("Kicked from guild");
            SQLHandler.onUpdate("DROP TABLE IF EXISTS _" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DROP TABLE IF EXISTS wordRestrictions_" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DROP TABLE IF EXISTS createRestrictions_" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM customPrefix WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM colorRolePosition WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM autoWhitelist WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM assignAmount WHERE guildid=" + event.getGuild().getIdLong());
            SQLHandler.onUpdate("DELETE FROM guilds WHERE guildid=" + event.getGuild().getIdLong());
        }
    }
}
package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class OnGuildBanListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(OnGuildBanListener.class);

    public void onGuildBan(@Nonnull GuildBanEvent event) {
        if (!ColorBot.shutdown) {
            logger.info("Banned from guild");
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

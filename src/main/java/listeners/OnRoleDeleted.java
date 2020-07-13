package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class OnRoleDeleted extends ListenerAdapter {

    @Override
    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
        if (!ColorBot.shutdown) {
            SQLHandler.onUpdate("DELETE FROM _" + event.getGuild().getIdLong() + " WHERE roleid=" + event.getRole().getIdLong());
            SQLHandler.onUpdate("UPDATE colorRolePosition SET roleid=" + null + " WHERE guildid=" + event.getGuild().getIdLong() + " AND roleid=" + event.getRole().getIdLong());
        }
    }
}
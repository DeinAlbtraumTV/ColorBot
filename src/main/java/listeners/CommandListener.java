package listeners;

import database.SQLHandler;
import main.ColorBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        if (!ColorBot.shutdown && !event.getAuthor().isBot()) {
            if (event.isFromType(ChannelType.TEXT)) {
                String message = event.getMessage().getContentRaw();
                ResultSet result = SQLHandler.onQuery("SELECT prefix from customPrefix WHERE guildid=" + event.getGuild().getId());

                try {
                    if (result != null && !result.isClosed() && result.next()) {
                        String customPrefix = result.getString("prefix");
                        if (message.startsWith("!color") || message.startsWith((customPrefix))) {

                            if (message.startsWith("!color")) {
                                message = message.substring(("!color").length());
                            } else if (message.startsWith(customPrefix)) {
                                message = message.substring((customPrefix).length());
                            }

                            message = message.trim();

                            if (message.startsWith("whitelist ")) {
                                whitelistRole(event);
                            } else if (message.startsWith("set-prefix ")) {
                                setPrefix(event, message.substring(("set-prefix ").length()));
                            } else if (message.startsWith("blacklist ")) {
                                blacklistRole(event);
                            } else if (message.startsWith("assignable")) {
                                getAssignable(event);
                            } else if (message.startsWith("transparent-all")) {
                                transparentAll(event);
                            } else if (message.startsWith("remote-delete ")) {
                                remoteDelete(event);
                            } else if (message.startsWith("set-role-position ")) {
                                setRolePosition(event);
                            } else if (message.startsWith("auto-whitelist ")) {
                                autoWhitelist(event, message.substring(("auto-whitelist ").length()));
                            } else if (message.startsWith("assign ")) {
                                assign(event);
                            } else if (message.startsWith("remove ")) {
                                remove(event);
                            } else if (message.startsWith("create ")) {
                                create(event, message.substring(("create ").length()));
                            } else if (message.startsWith("help ")) {
                                help(event, message.substring(("help ").length()));
                            } else if (message.startsWith("help")) {
                                help(event, message.substring(("help").length()));
                            } else if (message.startsWith("whitelist-words ")) {
                                whitelistWord(event, message.substring(("whitelist-words ").length()));
                            } else if (message.startsWith("blacklist-words ")) {
                                blacklistWord(event, message.substring(("blacklist-words ").length()));
                            } else if (message.startsWith("words-blacklisted")) {
                                getBlacklistedWords(event);
                            } else if (message.startsWith("create-whitelist ")) {
                                whitelistRoleForCreate(event);
                            } else if (message.startsWith("create-blacklist ")) {
                                blacklistRoleForCreate(event);
                            } else if (message.startsWith("can-create")) {
                                getRolesAbleToCreate(event);
                            } else if (message.startsWith("role-amount ")) {
                                setMaxAssignAmount(event, message.substring(("role-amount ").length()));
                            } else if (message.startsWith("setup")) {
                                setup(event);
                            } else if (message.startsWith("easter")) {
                                easter(event);
                            } else {
                                event.getChannel().sendMessage("Have you tried the help-command yet?").queue();
                            }
                        }
                    }
                } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to retrieve custom prefix from database");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        e.printStackTrace();
                        return;
                }
                try {
                    assert result != null;
                    result.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }
            } else {
                event.getChannel().sendMessage("This Bot is not intended to be used in private Channels!").queue();
            }
        }
    }

    private void setPrefix(MessageReceivedEvent event, String message) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            if (message.length() <= 20) {
                SQLHandler.onUpdate("UPDATE customPrefix SET prefix=\"" + message + "\" WHERE guildid=" + event.getGuild().getIdLong());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success")
                        .setDescription("The prefix has been set to: `" + message + "`")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Prefix too long")
                        .setDescription("The Prefix can't be longer than 20 characters!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to set a new Prefix! The Permission needed is: `Permission.MANAGE_SERVER`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void whitelistRole(MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();
            if (!event.getMessage().getMentionedRoles().isEmpty()) {
                SQLHandler.onUpdate("INSERT IGNORE INTO  _" + event.getGuild().getId() + "(roleid) VALUES(" + event.getMessage().getMentionedRoles().get(0).getIdLong() + ")");
                builder.append(event.getMessage().getMentionedRoles().get(0).getName());

                if (event.getMessage().getMentionedRoles().size() > 1) {
                    for (int x = 1; x < event.getMessage().getMentionedRoles().size(); x++) {
                        SQLHandler.onUpdate("INSERT IGNORE INTO _" + event.getGuild().getId() + "(roleid) VALUES(" + event.getMessage().getMentionedRoles().get(x).getIdLong() + ")");
                        builder.append("\n ").append(event.getMessage().getMentionedRoles().get(x).getName());
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success").setDescription("Whitelisted these Roles: \n" + builder.toString())
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Roles not found")
                        .setDescription("Please provide the roles you want to whitelist by mentioning them")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to whitelist Roles! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void blacklistRole(MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();

            SQLHandler.onUpdate("DELETE FROM _" + event.getGuild().getId() + " WHERE roleid=" + event.getMessage().getMentionedRoles().get(0).getIdLong());
            builder.append(event.getMessage().getMentionedRoles().get(0).getName());

            if (event.getMessage().getMentionedRoles().size() > 1) {
                for (int x = 1; x < event.getMessage().getMentionedRoles().size(); x++) {
                    SQLHandler.onUpdate("DELETE FROM _" + event.getGuild().getId() + " WHERE roleid=" + event.getMessage().getMentionedRoles().get(x).getIdLong());
                    builder.append("\n ").append(event.getMessage().getMentionedRoles().get(x).getName());
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success").setDescription("Blacklisted these Roles: \n" + builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to blacklist Roles! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void autoWhitelist(MessageReceivedEvent event, String message) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            if (message.equalsIgnoreCase("on") || message.equalsIgnoreCase("off")) {
                switch (message) {
                    case "off":
                        SQLHandler.onUpdate("UPDATE autoWhitelist set state=0 WHERE guildid=" + event.getGuild().getIdLong());
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success")
                                .setDescription("New ColorRoles will now not be added to the whitelist upon creation")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.GREEN)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                        break;
                    case "on":
                        SQLHandler.onUpdate("UPDATE autoWhitelist set state=1 WHERE guildid=" + event.getGuild().getIdLong());
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success")
                                .setDescription("New ColorRoles will now be added to the whitelist upon creation")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.GREEN)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                        break;
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Please enter off to disable the auto-whitelist feature and on to enable it!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to set the auto-whitelist feature! The Permission needed is: `Permission.MANAGE_SERVER`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void getAssignable(MessageReceivedEvent event) {
        ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + event.getGuild().getIdLong());
        StringBuilder builder = new StringBuilder();

        try {
            if (!Objects.requireNonNull(result).next() || result.isClosed()) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Roles not found")
                        .setDescription("There are no roles assignable in this server")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                if (!result.isClosed()) {
                    try {
                        result.close();
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }
                }

                return;
            } else {
                do {
                    builder.append("\n ").append(Objects.requireNonNull(event.getGuild().getRoleById(result.getString("roleid"))).getAsMention());
                } while (result.next());
            }

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Assignable roles")
                    .setDescription(builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(ColorBot.orange)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("An Error occurred while trying to get all roles that are assignable on this server.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
        }
    }

    private void transparentAll(MessageReceivedEvent event) {

        boolean hasHighestRole = false;
        Role highestRole = null;

        for (Role r : Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getRoles()) {
            if (r.equals(event.getGuild().getRoles().get(0))) {
                hasHighestRole = true;
                highestRole = r;
            }
        }

        if (hasHighestRole) {
            if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
                for (Role r : event.getGuild().getRoles()) {
                    if (r.getPosition() != highestRole.getPosition()) {
                        r.getManager().setColor(new Color(0, 0, 0)).queue();
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success")
                        .setDescription("Changed the Color of all Roles (except the highest as i can't modify my highest role) to be transparent!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Permission not found")
                        .setDescription("You don't have the Permission to make all Roles transparent! The Permission needed is: `Permission.MANAGE_ROLES`")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Requirements not met")
                    .setDescription("I have to have the highest Role in this Server for this command to function, as i can not modify roles above me")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void remoteDelete(MessageReceivedEvent event) {
        StringBuilder builder = new StringBuilder();

        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            try {
                for (Role r : event.getMessage().getMentionedRoles()) {
                    String roleName;
                    roleName = r.getName();
                    r.delete().reason("Remote-deletion requested").queue();
                    builder.append("\n ").append(roleName);
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success")
                        .setDescription("Deleted these Roles: " + builder.toString())
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("I failed to delete all roles because I suck at doing things right... I am only a bot.\n Anyways, the roles that I managed to delete are: " + builder.toString())
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to remote-delete roles! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void setRolePosition(MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            if (event.getMessage().getMentionedRoles().size() == 1) {

                Role highestRole = Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getRoles().get(0);

                if (event.getMessage().getMentionedRoles().get(0).getPosition() <= Objects.requireNonNull(highestRole).getPosition()) {
                    SQLHandler.onUpdate("UPDATE colorRolePosition SET roleid=" + event.getMessage().getMentionedRoles().get(0).getIdLong() + " WHERE guildid=" + event.getGuild().getIdLong());
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("All new ColorRoles will be created below this role: \n " + event.getMessage().getMentionedRoles().get(0).getAsMention())
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.GREEN)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("The role you tried to set is too high, i don't have access to it!")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Please mention only one role.")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to set the role-position! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void setMaxAssignAmount(MessageReceivedEvent event, String message) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            try {
                long amount = Long.parseLong(message);
                SQLHandler.onUpdate("UPDATE assignAmount set amount=" + amount + " WHERE guildid=" + event.getGuild().getIdLong());

                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success")
                        .setDescription("The max amount of assignable roles per user has been set to: " + amount)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

            } catch (Exception e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("Please enter a valid number!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to set the max amount of roles a user can assign! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void assign(MessageReceivedEvent event) {

        if (hasRoleSpace(event, event.getMessage().getMentionedRoles().size())) {

            ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + event.getGuild().getIdLong());
            ArrayList<Role> roles = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean rolesAssigned = false;

            if (result != null) {
                try {
                    if (!result.next() || result.isClosed()) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("No Role that you tried to assign is on the whitelist!")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                    } else {
                        do {
                            roles.add(event.getGuild().getRoleById(Objects.requireNonNull(result).getString("roleid")));
                        } while (result.next());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("An Error occurred while assigning the roles.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();

                    try {
                        result.close();
                    } catch (SQLException sqlE) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }

                    return;
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("You can´t assign yourself any roles in this guild!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                return;
            }
            if (roles.size() != 0) {
                Role highestRole = Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getRoles().get(0);
                for (Role r : event.getMessage().getMentionedRoles()) {
                    if (roles.contains(r)) {
                        if (r.getPosition() < Objects.requireNonNull(highestRole).getPosition()) {
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMessage().getMember()), r).queue();
                            builder.append("\n ").append(r.getAsMention());
                            rolesAssigned = true;
                        }
                    }
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("You can´t assign yourself any roles in this guild!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                try {
                    result.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            if (rolesAssigned) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success")
                        .setDescription("These roles have been assigned to you: " + builder.toString())
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("No roles were assigned to you")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }

            try {
                result.close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("The amount of roles you tried to assign would bring you over the max amount allowed on this server.")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void remove(MessageReceivedEvent event) {

        ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + event.getGuild().getIdLong());
        ArrayList<Role> roles = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean rolesRemoved = false;

        try {
            if (!Objects.requireNonNull(result).next() || result.isClosed()) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Roles not found")
                        .setDescription("No Role that you tried to remove is on the whitelist!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                if (!result.isClosed()) {
                    try {
                        result.close();
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }
                }

                return;
            } else {
                do {
                    roles.add(event.getGuild().getRoleById(Objects.requireNonNull(result).getString("roleid")));
                } while (result.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("An Error occurred while removing the roles.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();

            try {
                Objects.requireNonNull(result).close();
            } catch (SQLException sqlE) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

            return;
        }

        if (roles.size() != 0) {
            Role highestRole = Objects.requireNonNull(event.getGuild().getMember(event.getJDA().getSelfUser())).getRoles().get(0);
            for (Role r : event.getMessage().getMentionedRoles()) {
                if (roles.contains(r)) {
                    if (r.getPosition() < Objects.requireNonNull(highestRole).getPosition()) {
                        if (Objects.requireNonNull(event.getMessage().getMember()).getRoles().contains(r)) {
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMessage().getMember()), r).queue();
                            builder.append("\n ").append(r.getAsMention());
                            rolesRemoved = true;
                        }
                    }
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("You don´t have any roles that can be removed")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();

            try {
                result.close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

            return;
        }

        if (rolesRemoved) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success")
                    .setDescription("These roles were removed from you: \n " + builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();

        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("No roles were removed from you")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }

        try {
            result.close();
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
        }
    }

    private void create(MessageReceivedEvent event, String message) {
        String[] messageSplit = message.split(" ");

        if (hasRoleSpace(event, 1)) {

            ResultSet result = SQLHandler.onQuery("SELECT roleid FROM colorRolePosition WHERE guildid=" + event.getGuild().getIdLong());
            ResultSet resultSet = SQLHandler.onQuery("SELECT state FROM autoWhitelist WHERE guildid=" + event.getGuild().getIdLong());
            ResultSet resultBlacklist = SQLHandler.onQuery("SELECT word FROM wordRestrictions_" + event.getGuild().getIdLong());
            ResultSet createRestrictions = SQLHandler.onQuery("SELECT roleid FROM createRestrictions_" + event.getGuild().getIdLong());
            Role destination;

            if (result == null) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("No position for ColorRoles to be placed is defined for this guild.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!\nError-Code: result=null")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                try {
                    Objects.requireNonNull(resultSet).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(resultBlacklist).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(createRestrictions).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            if (resultSet == null) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!\nError-Code: resultSet=null")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                try {
                    result.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(resultBlacklist).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(createRestrictions).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            if (resultBlacklist == null) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!\nError-Code: resultBlacklist=null")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                try {
                    result.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(resultSet).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(createRestrictions).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            if (createRestrictions == null) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!\nError-Code: resultRestrictions=null")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                try {
                    result.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(resultBlacklist).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            try {
                if (result.next()) {
                    if (result.getString("roleid") == null) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("No position for ColorRoles to be placed is defined for this guild.\n Please report this to the team of this server.")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();

                        try {
                            result.close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }

                        try {
                            resultSet.close();
                        } catch (SQLException sqlE) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }

                        try {
                            resultBlacklist.close();
                        } catch (SQLException sqlE) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }

                        try {
                            Objects.requireNonNull(createRestrictions).close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }

                        return;
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!\nError-Code: result.next()=false")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                }
                destination = event.getGuild().getRoleById(result.getString("roleid"));
            } catch (SQLException e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                e.printStackTrace();

                try {
                    result.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    resultSet.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    resultBlacklist.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(createRestrictions).close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            try {
                boolean isAllowed = false;
                if (!createRestrictions.isClosed() && createRestrictions.next()) {
                    do {
                        if (Objects.requireNonNull(event.getMessage().getMember()).getRoles().contains(event.getGuild().getRoleById(createRestrictions.getString("roleid")))) {
                            isAllowed = true;
                        }
                    } while (createRestrictions.next());
                }

                if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
                    isAllowed = true;
                }

                if (!isAllowed) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("You don't have the permission to create roles on this server!")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();

                    try {
                        result.close();
                    } catch (SQLException sqlE) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }

                    try {
                        resultSet.close();
                    } catch (SQLException sqlE) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }

                    try {
                        resultBlacklist.close();
                    } catch (SQLException sqlE) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }

                    try {
                        Objects.requireNonNull(createRestrictions).close();
                    } catch (SQLException sqlE) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }

                    return;
                }
            } catch (SQLException e) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription("An Error occurred while creating the role.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                e.printStackTrace();

                try {
                    result.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    resultSet.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    resultBlacklist.close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                try {
                    Objects.requireNonNull(createRestrictions).close();
                } catch (SQLException sqlE) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

                return;
            }

            if (!message.startsWith("#")) {
                try {
                    int r = Integer.parseInt(messageSplit[0]);
                    int g = Integer.parseInt(messageSplit[1]);
                    int b = Integer.parseInt(messageSplit[2]);

                    String name = message.substring((r + " " + g + " " + b + " ").length());
                    ArrayList<String> blacklistedWords = new ArrayList<>();
                    boolean nameContainsBlacklisted = false;

                    if (!resultBlacklist.isClosed() && resultBlacklist.next()) {
                        do {
                            blacklistedWords.add(resultBlacklist.getString("word"));
                        } while (resultBlacklist.next());
                    }
                    if (!event.getMessage().getMember().hasPermission(Permission.MANAGE_ROLES)) {
                        for (String s : blacklistedWords) {
                            if (name.toLowerCase().contains(s.toLowerCase())) {
                                nameContainsBlacklisted = true;
                                break;
                            }
                        }
                    }

                    if (nameContainsBlacklisted) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("The name of your role contains blacklisted words. \n Please remove them and try again. \n \n To see all blacklisted words on this server, use the words-blacklisted command!")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                        try {
                            result.close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }
                        try {
                            resultBlacklist.close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }
                        try {
                            Objects.requireNonNull(createRestrictions).close();
                        } catch (SQLException e) {
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        }
                        return;
                    }

                    if (result.getString("roleid") != null) {
                        Role role = event.getGuild().createRole().setColor(new Color(r, g, b)).setName(name).setPermissions(event.getGuild().getRoles().get(event.getGuild().getRoles().size() - 1).getPermissions()).setMentionable(true).complete();
                        event.getGuild().modifyRolePositions(true).selectPosition(role).moveTo(Objects.requireNonNull(destination).getPosition() - 1).queue();
                        event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMessage().getMember()), role).queue();
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Creation successful")
                                .setDescription("Created your Role " + role.getAsMention())
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(new Color(r, g, b))
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();

                        if (resultSet.next() && resultSet.getInt("state") == 1) {
                            SQLHandler.onUpdate("INSERT IGNORE INTO _" + event.getGuild().getId() + "(roleid) VALUES(" + role.getIdLong() + ")");
                        }

                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("No position for ColorRoles to be placed is defined for this guild.\n Please report this to the team of this server.")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                    }

                } catch (SQLException sqlE) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("An Error occurred while whitelisting the role.\n \nPlease create a bug report in my support server")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                } catch (Exception e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("Please supply a valid rgb/hex code and a name.\n \nFor more Information take a look at the help-menu.")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                }
            } else {
                try {
                    String hex = messageSplit[0];
                    if (hex.length() == 7) {
                        String name = message.substring((hex + " ").length());
                        ArrayList<String> blacklistedWords = new ArrayList<>();
                        boolean nameContainsBlacklisted = false;

                        if (!resultBlacklist.isClosed()) {
                            do {
                                blacklistedWords.add(resultBlacklist.getString("word"));
                            } while (resultBlacklist.next());
                        }

                        for (String s : blacklistedWords) {
                            if (name.toLowerCase().contains(s.toLowerCase())) {
                                nameContainsBlacklisted = true;
                                break;
                            }
                        }

                        if (nameContainsBlacklisted) {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("The name of your role contains blacklisted words. \n Please remove them and try again. \n \n To see all blacklisted words on this server, use the words-blacklisted command!")
                                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                    .setColor(Color.RED)
                                    .setFooter("Developed by DeinAlbtraum#6224")
                                    .build()).queue();
                            try {
                                result.close();
                            } catch (SQLException e) {
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            }
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            }
                            try {
                                resultBlacklist.close();
                            } catch (SQLException e) {
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            }
                            try {
                                Objects.requireNonNull(createRestrictions).close();
                            } catch (SQLException e) {
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                            }
                            return;
                        }

                        if (result.getString("roleid") != null) {
                            Role role = event.getGuild().createRole().setColor(Color.decode(hex)).setName(name).setPermissions(event.getGuild().getRoles().get(event.getGuild().getRoles().size() - 1).getPermissions()).setMentionable(true).complete();
                            event.getGuild().modifyRolePositions(true).selectPosition(role).moveTo(Objects.requireNonNull(destination).getPosition() - 1).queue();
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMessage().getMember()), role).queue();
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Creation successful")
                                    .setDescription("Created your Role " + role.getAsMention())
                                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                    .setColor(Color.decode(hex))
                                    .setFooter("Developed by DeinAlbtraum#6224")
                                    .build()).queue();

                            if (resultSet.next() && resultSet.getInt("state") == 1) {
                                SQLHandler.onUpdate("INSERT IGNORE INTO _" + event.getGuild().getId() + "(roleid) VALUES(" + role.getIdLong() + ")");
                            }

                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error")
                                    .setDescription("No position for ColorRoles to be placed is defined for this guild.\n Please report this to the team of this server.")
                                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                    .setColor(Color.RED)
                                    .setFooter("Developed by DeinAlbtraum#6224")
                                    .build()).queue();
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("Please supply a valid rgb/hex code and a name.\n \nFor more Information take a look at the help-menu.")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                    }
                } catch (SQLException sqlE) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("An Error occurred while whitelisting the role.\n \nPlease create a bug report in my support server")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                } catch (Exception e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("Please supply a valid rgb/hex code and a name.\n \nFor more Information take a look at the help-menu.")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                }
            }
            try {
                result.close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }
            try {
                resultBlacklist.close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }
            try {
                Objects.requireNonNull(createRestrictions).close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("You have reached the max amount of assignable roles per user.")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void whitelistWord(MessageReceivedEvent event, String message) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();
            String[] messageSplit = message.split(" ");

            for (String s : messageSplit) {
                if (s.endsWith(",")) {
                    s = s.substring(0, s.length() - 1);
                }
                SQLHandler.onUpdate("DELETE FROM wordRestrictions_" + event.getGuild().getId() + " WHERE word='" + s.toLowerCase() + "'");
                builder.append(s).append("\n");
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success").setDescription("Whitelisted these words: \n" + builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to whitelist words! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void blacklistWord(MessageReceivedEvent event, String message) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();
            String[] messageSplit = message.split(" ");

            for (String s : messageSplit) {
                if (s.endsWith(",")) {
                    s = s.substring(0, s.length() - 1);
                }
                if (s.length() <= 20) {
                    SQLHandler.onUpdate("INSERT IGNORE INTO wordRestrictions_" + event.getGuild().getIdLong() + "(word) VALUES('" + s.toLowerCase() + "')");
                    builder.append(s).append("\n");
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success").setDescription("Blacklisted these words: \n " + builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to blacklist words! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void getBlacklistedWords(MessageReceivedEvent event) {
        ResultSet result = SQLHandler.onQuery("SELECT word FROM wordRestrictions_" + event.getGuild().getIdLong());
        StringBuilder builder = new StringBuilder();

        try {
            if (!Objects.requireNonNull(result).next() || result.isClosed()) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("No words blacklisted")
                        .setDescription("There are no words blacklisted on this server")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                if (!result.isClosed()) {
                    try {
                        result.close();
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }
                }

                return;
            } else {
                do {
                    builder.append("\n ").append(result.getString("word"));
                } while (result.next());
            }

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Blacklisted Words")
                    .setDescription(builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(ColorBot.orange)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("An Error occurred while trying to get the blacklisted words on this server.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
        }
    }

    private void whitelistRoleForCreate(MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();
            if (!event.getMessage().getMentionedRoles().isEmpty()) {
                SQLHandler.onUpdate("INSERT IGNORE INTO  createRestrictions_" + event.getGuild().getIdLong() + "(roleid) VALUES(" + event.getMessage().getMentionedRoles().get(0).getIdLong() + ")");
                builder.append(event.getMessage().getMentionedRoles().get(0).getName());

                if (event.getMessage().getMentionedRoles().size() > 1) {
                    for (int x = 1; x < event.getMessage().getMentionedRoles().size(); x++) {
                        SQLHandler.onUpdate("INSERT IGNORE INTO createRestrictions_" + event.getGuild().getId() + "(roleid) VALUES(" + event.getMessage().getMentionedRoles().get(x).getIdLong() + ")");
                        builder.append("\n ").append(event.getMessage().getMentionedRoles().get(x).getName());
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success").setDescription("Whitelisted these Roles: \n" + builder.toString())
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.GREEN)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Roles not found")
                        .setDescription("Please provide the roles you want to whitelist by mentioning them")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to whitelist Roles! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void blacklistRoleForCreate(MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.MANAGE_ROLES)) {
            StringBuilder builder = new StringBuilder();

            SQLHandler.onUpdate("DELETE FROM createRestrictions_" + event.getGuild().getId() + " WHERE roleid=" + event.getMessage().getMentionedRoles().get(0).getIdLong());
            builder.append(event.getMessage().getMentionedRoles().get(0).getName());

            if (event.getMessage().getMentionedRoles().size() > 1) {
                for (int x = 1; x < event.getMessage().getMentionedRoles().size(); x++) {
                    SQLHandler.onUpdate("DELETE FROM createRestrictions_" + event.getGuild().getId() + " WHERE roleid=" + event.getMessage().getMentionedRoles().get(x).getIdLong());
                    builder.append("\n ").append(event.getMessage().getMentionedRoles().get(x).getName());
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success").setDescription("Blacklisted these Roles: \n" + builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to blacklist Roles! The Permission needed is: `Permission.MANAGE_ROLES`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void getRolesAbleToCreate(MessageReceivedEvent event) {
        ResultSet result = SQLHandler.onQuery("SELECT roleid FROM createRestrictions_" + event.getGuild().getIdLong());
        StringBuilder builder = new StringBuilder();

        try {
            if (!Objects.requireNonNull(result).next() || result.isClosed()) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Open for everybody")
                        .setDescription("Everybody can use the create command")
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(Color.RED)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();

                if (!result.isClosed()) {
                    try {
                        result.close();
                    } catch (SQLException e) {
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                        System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                        System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    }
                }

                return;
            } else {
                do {
                    builder.append("\n ").append(Objects.requireNonNull(event.getGuild().getRoleById(result.getString("roleid"))).getAsMention());
                } while (result.next());
            }

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Roles able to create")
                    .setDescription(builder.toString())
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(ColorBot.orange)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("An Error occurred while trying to get all roles that can use the create command on this server.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
        }
    }

    private void setup (MessageReceivedEvent event) {
        if (Objects.requireNonNull(event.getMember()).isOwner()) {
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS _" + event.getGuild().getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNSIGNED UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS wordRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, word VARCHAR(20) UNIQUE)");
            SQLHandler.onUpdate("CREATE TABLE IF NOT EXISTS createRestrictions_" + event.getGuild().getIdLong() + "(id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, roleid BIGINT UNIQUE)");
            SQLHandler.onUpdate("INSERT IGNORE INTO customPrefix(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT IGNORE INTO colorRolePosition(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT IGNORE INTO autoWhitelist(guildid) VALUES(" + event.getGuild().getIdLong() + ")");
            SQLHandler.onUpdate("INSERT IGNORE INTO assignAmount(guildid) VALUES(" + event.getGuild().getIdLong() + ")");

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success").setDescription("Bot is now Setup for use!")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.GREEN)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Permission not found")
                    .setDescription("You don't have the Permission to setup the bot! The Permission needed is: `Permission.OWNER`")
                    .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                    .setColor(Color.RED)
                    .setFooter("Developed by DeinAlbtraum#6224")
                    .build()).queue();
        }
    }

    private void easter(MessageReceivedEvent event) {
        event.getChannel().sendMessage("May i offer you a nice egg in this trying-time? :egg:").queue();
    }

    private void help(MessageReceivedEvent event, String message) {
        switch (message) {
            case "whitelist":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> whitelist <mention (multiple) roles to whitelist here>", false)
                        .addField("Description", "The whitelist command is used to manually add roles to the whitelist.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. \nThe whitelist is used for the assign and remove command.", false)
                        .addField("See also", "assign \n remove \n blacklist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "set-prefix":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> set-prefix <new prefix>", false)
                        .addField("Description", "The set-prefix command is used to set the third prefix the bot responds to. Prefixes can´t be longer than 20 characters.\n To perform this command the user needs the Permission `Permission.MANAGE_SERVER`.", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "blacklist":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> blacklist <mention (multiple) roles to blacklist here>", false)
                        .addField("Description", "The blacklist command is used to manually remove roles from the whitelist.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. \nThe whitelist is used for the assign and remove command.", false)
                        .addField("See also", "assign \n remove \n whitelist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "assignable":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> assignable", false)
                        .addField("Description", "The assignable command is used to get all roles that are in the whitelist.\n To perform this command no permission is needed.", false)
                        .addField("See also", "assign \n remove \n whitelist \n blacklist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "transparent-all":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> transparent-all", false)
                        .addField("Description", "The transparent-all command is used to remove the color from every role in the server.\n For this command to work, the Bot has to have the highest role in the server\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`.", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "remote-delete":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> remote-delete <mention (multiple) roles to delete here>", false)
                        .addField("Description", "The remote-delete command is used to delete many roles at once, even if the user has no access to them but the bot.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`.", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "set-role-position":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> set-role-position <mention one role here>", false)
                        .addField("Description", "The set-role-position command is used to define under which role ColorRoles get placed.\n This only affects new roles.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`.", false)
                        .addField("See also", "create", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "auto-whitelist":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> auto-whitelist <on/off>", false)
                        .addField("Description", "The auto-whitelist command is used to toggle whether ColorRoles should be added to the whitelist automatically.\n This only affects new roles.\n 0 disables this feature, 1 enables it. This feature is turned on by default.\n To perform this command the user needs the Permission `Permission.MANAGE_SERVER`.", false)
                        .addField("See also", "create", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "assign":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> assign <mention (multiple) roles to assign yourself here>", false)
                        .addField("Description", "The assign command is used to give yourself roles that already exist.\n This only works with roles in the whitelist.\n To perform this command no permission is needed.", false)
                        .addField("See also", "remove \n whitelist \n blacklist \n assignable", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "remove":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> remove <mention (multiple) roles to remove from yourself here>", false)
                        .addField("Description", "The remove command is used to remove roles from yourself.\n This only works with roles in the whitelist.\n To perform this command no permission is needed.", false)
                        .addField("See also", "assign \n whitelist \n blacklist \n assignable", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "create":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> create <rgb/hex code> <name>", false)
                        .addField("Description", "The create command is used to create new roles with a provided color and name.\n For HEX-codes to work, you must supply a 6 digit code with a `#` in front of it.\n To perform this command no permission is needed", false)
                        .addField("See also", "set-role-position \n auto-whitelist \n create-whitelist \n create-blacklist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "blacklist-words":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> blacklist-words <(multiple) words to blacklist here>", false)
                        .addField("Description", "The blacklist-words command is used to add words to the blacklist. The words are not case-sensitive. Words can´t be longer than 20 characters.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. In addition, users with the Permission `Permission.MANAGE_ROLES` override the blacklist. \nWords in the blacklist can **NOT** be used in the create command.", false)
                        .addField("See also", "create \n whitelist-words", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "whitelist-words":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> whitelist-words <(multiple) words to whitelist here>", false)
                        .addField("Description", "The whitelist-words command is used to remove words from the blacklist. The words are not case-sensitive.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. In addition, users with the Permission `Permission.MANAGE_ROLES` override the blacklist. \nWords in the blacklist can **NOT** be used in the create command.", false)
                        .addField("See also", "create \n blacklist-words \n words-blacklisted", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "words-blacklisted":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> words-blacklisted", false)
                        .addField("Description", "The words-blacklisted command is used to get all words that are on the blacklist. The words are not case-sensitive.\n To perform this command no permission is needed.", false)
                        .addField("See also", "create \n whitelist-words \n blacklist-words", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "create-whitelist":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> create-whitelist <mention (multiple) roles to whitelist here>", false)
                        .addField("Description", "The create-whitelist command is used to add roles to the whitelist for the create-command.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. \nOnly roles in the whitelist will be able to use the create command. If the whitelist is empty, everybody can use the command. \n In addition, users with the permission `Permission.MANAGE_ROLES` will not be affected by this.", false)
                        .addField("See also", "create \n create-blacklist \n can-create", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "create-blacklist":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> create-blacklist <mention (multiple) roles to blacklist here>", false)
                        .addField("Description", "The create-blacklist command is used to remove roles from the whitelist for the create-command.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. \n Only roles in the whitelist will be able to use the create command. If the whitelist is empty, everybody can use the command. \n In addition, users with the permission `Permission.MANAGE_ROLES` will not be affected by this.", false)
                        .addField("See also", "create \n create-whitelist \n can-create", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "can-create":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> can-create", false)
                        .addField("Description", "The can-create command is used to get all roles that can use the create command.\n To perform this command no permission is needed. \n Only roles in the whitelist will be able to use the create command. If the whitelist is empty, everybody can use the command. \n In addition, users with the permission `Permission.MANAGE_ROLES` will not be affected by this.", false)
                        .addField("See also", "create \n create-whitelist \n create-blacklist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "role-amount":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> role-amount <amount>", false)
                        .addField("Description", "The role-amount command is used to set a max number of roles that a user can have assigned.\n To perform this command the user needs the Permission `Permission.MANAGE_ROLES`. \n Only roles in the whitelist count towards this limit, setting it to 0 disables it. \n In addition, users with the permission `Permission.MANAGE_ROLES` will not be affected by this.", false)
                        .addField("See also", "create \n create-whitelist \n create-blacklist", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "setup":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> setup", false)
                        .addField("Description", "This Command is used to setup the Database of the Bot. You only need to run this, if the Bot joined your server while being offline!\n To perform this command the user needs the Permission `Permission.OWNER`.", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;
            case "help":
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .addField("Usage", "<prefix> help <command (optional)>", false)
                        .addField("Description", "The help command is used to get information on how to use a certain command or to get all commands.\n When a valid command is supplied, you will get more information on the given command.\n To perform this command no permission is needed.", false)
                        .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                        .setColor(ColorBot.orange)
                        .setFooter("Developed by DeinAlbtraum#6224")
                        .build()).queue();
                break;

            default:
                try {
                    ResultSet result = SQLHandler.onQuery("SELECT prefix from customPrefix WHERE guildid=" + event.getGuild().getId());

                    if (result != null && !result.isClosed() && result.next()) {

                        String customPrefix = result.getString("prefix");

                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Help")
                                .setDescription("Default Prefix (this works on every server the bot is on): `!color` \n Prefix on this server: `" + customPrefix + "`\n \n \n \n These commands exist. Write <prefix> help <command> to get more information on how to use the command.\n \n **General** \n create \n assign \n remove \n assignable \n words-blacklisted \n help \n \n **Special** \n whitelist \n blacklist \n whitelist-words \n blacklist-words \n remote-delete \n transparent-all \n create-whitelist \n create-blacklist \n can-create \n \n **Settings** \n set-prefix \n set-role-position \n auto-whitelist \n role-amount \n setup")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(ColorBot.orange)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();

                        result.close();
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error")
                                .setDescription("An Error occurred while retrieving the custom prefix for this guild.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                                .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                                .setColor(Color.RED)
                                .setFooter("Developed by DeinAlbtraum#6224")
                                .build()).queue();
                    }
                    break;
                } catch (SQLException e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("An Error occurred while retrieving the custom prefix for this guild.\n \nPlease try again later.\nIf this error persists please make a bug report in my support-server!")
                            .addField("", "Join my [Support-Server](https://discordapp.com/invite/c56xQW6)\n[Add](https://discordapp.com/oauth2/authorize?client_id=607987823328362543&permissions=268454912&scope=bot) me to your server", false)
                            .setColor(Color.RED)
                            .setFooter("Developed by DeinAlbtraum#6224")
                            .build()).queue();
                    e.printStackTrace();
                }
                break;
        }
    }

    private boolean hasRoleSpace(MessageReceivedEvent event, int amountOfRolesAdded) {
        ResultSet result = SQLHandler.onQuery("SELECT roleid FROM _" + event.getGuild().getIdLong());
        ResultSet limitSet = SQLHandler.onQuery("SELECT amount FROM assignAmount WHERE guildid=" + event.getGuild().getIdLong());
        long limit = 0;
        long rolesFromWhitelistFound = 0;
        ArrayList<Role> roles = new ArrayList<>();

        try {
            if (!Objects.requireNonNull(result).next() || result.isClosed()) {

                try {
                    result.close();
                    Objects.requireNonNull(limitSet).close();
                } catch (SQLException e) {
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                    System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying to close a resultSet");
                    System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                }

            } else {
                do {
                    roles.add(event.getGuild().getRoleById(result.getString("roleid")));
                } while (result.next());
            }
            if (limitSet != null && !limitSet.isClosed() && limitSet.next()) {
                limit = limitSet.getLong("amount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
        }

        for (Role r : Objects.requireNonNull(event.getMessage().getMember()).getRoles()) {
            if (roles.contains(r)) {
                rolesFromWhitelistFound++;
            }
        }

        rolesFromWhitelistFound += amountOfRolesAdded;

        if (event.getMessage().getMember().hasPermission(Permission.MANAGE_ROLES)) {

            try {
                Objects.requireNonNull(limitSet).close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

            return true;
        }
        if (limit == 0L) {

            try {
                Objects.requireNonNull(limitSet).close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

            return true;
        }
        if (rolesFromWhitelistFound <= limit) {

            try {
                Objects.requireNonNull(limitSet).close();
            } catch (SQLException e) {
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
                System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
                System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            }

            return true;
        }

        try {
            limitSet.close();
        } catch (SQLException e) {
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
            System.out.println("[ColorBot ColorBot-Thread] ERROR - An error occurred while trying close to a resultSet");
            System.out.println("[ColorBot ColorBot-Thread] Info - ---------------------------------");
        }

        return false;

    }
}
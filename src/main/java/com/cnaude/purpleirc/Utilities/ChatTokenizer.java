package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.ChatMessage;
import com.cnaude.purpleirc.ChatMessages.BungeeChatMessage;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.pircbotx.User;

/**
 * Main class containing all message template token expanding methods
 *
 * @author cnaude
 */
public class ChatTokenizer {

    PurpleIRC plugin;

    /**
     * Class initializer
     *
     * @param plugin
     */
    public ChatTokenizer(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     * IRC to game chat tokenizer without a message
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param template
     * @return
     */
    public String chatIRCTokenizer(PurpleBot ircBot, User user, org.pircbotx.Channel channel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", user.getNick())
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * Normal IRC to game chat tokenizer
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param template
     * @param message
     * @return
     */
    public String ircChatToGameTokenizer(PurpleBot ircBot, User user, org.pircbotx.Channel channel, String template, String message) {
        String ircNick = user.getNick();
        String tmpl;
        ProxiedPlayer player = plugin.getPlayer(ircNick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            plugin.logDebug("ircChatToGameTokenizer: null player: " + ircNick);
            tmpl = playerTokenizer(ircNick, template);
        }
        return plugin.colorConverter.ircColorsToGame(ircUserTokenizer(tmpl, user, ircBot)
                .replace("%NAME%", ircNick)
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * IRC kick message to game
     *
     * @param ircBot
     * @param recipient
     * @param kicker
     * @param reason
     * @param channel
     * @param template
     * @return
     */
    public String ircKickTokenizer(PurpleBot ircBot, User recipient, User kicker, String reason, org.pircbotx.Channel channel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", recipient.getNick())
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(kicker, channel))
                .replace("%REASON%", reason)
                .replace("%KICKER%", kicker.getNick())
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * IRC mode change messages
     *
     * @param ircBot
     * @param user
     * @param mode
     * @param channel
     * @param template
     * @return
     */
    public String ircModeTokenizer(PurpleBot ircBot, User user, String mode, org.pircbotx.Channel channel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", user.getNick())
                .replace("%MODE%", mode)
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * IRC notice change messages
     *
     * @param ircBot
     * @param user
     * @param message
     * @param notice
     * @param channel
     * @param template
     * @return
     */
    public String ircNoticeTokenizer(PurpleBot ircBot, User user, String message, String notice, org.pircbotx.Channel channel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", user.getNick())
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%MESSAGE%", message)
                .replace("%NOTICE%", notice)
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * Game chat to IRC
     *
     * @param pName
     * @param template
     *
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(String pName, String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", pName)
                .replace("%MESSAGE%", plugin.colorConverter.gameColorsToIrc(message)));
    }

    /**
     * Game chat to IRC
     *
     * @param player
     * @param template
     *
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(ProxiedPlayer player, String template, String message) {
        if (message == null) {
            message = "";
        }
        String serverName = "";
        if (player.getServer() != null) {
            serverName = player.getServer().getInfo().getName();
        }
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%NAME%", player.getName())
                .replace("%DISPLAYNAME%", player.getDisplayName())
                .replace("%SERVERNAME%", serverName)
                .replace("%MESSAGE%", message));
    }

    /**
     * Game chat to IRC
     *
     * @param sender
     * @param template
     *
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(CommandSender sender, String template, String message) {
        if (message == null) {
            message = "";
        }
        String serverName = "";
        String displayName = sender.getName();
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (player.getServer() != null) {
                serverName = player.getServer().getInfo().getName();
            }
            return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                    .replace("%SERVERNAME%", serverName)
                    .replace("%MESSAGE%", message));
        }
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", sender.getName())
                .replace("%DISPLAYNAME%", displayName)
                .replace("%SERVERNAME%", serverName)
                .replace("%MESSAGE%", message));
    }

    /**
     * Game player AFK to IRC
     *
     * @param player
     * @param template
     *
     * @return
     */
    public String gamePlayerAFKTokenizer(ProxiedPlayer player, String template) {
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template));
    }

    /**
     * Game chat to IRC
     *
     * @param template
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%MESSAGE%", message));
    }

    /**
     * Game kick message to IRC
     *
     * @param player
     * @param template
     * @param reason
     * @param message
     * @return
     */
    public String gameKickTokenizer(ProxiedPlayer player, String template, String message, String reason) {
        return plugin.colorConverter.gameColorsToIrc(
                gameChatToIRCTokenizer(player, template, message)
                        .replace("%MESSAGE%", message)
                        .replace("%REASON%", reason));
    }

    public String playerTokenizer(ProxiedPlayer player, String message) {
        String pName = player.getName();
        plugin.logDebug("Tokenizing " + pName);
        String displayName = player.getDisplayName();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        String worldName = "";
        String worldAlias = "";
        String serverName;
        try {
            serverName = player.getServer().getInfo().getName();
        } catch (Exception ex) {
            plugin.logDebug(ex.getMessage());
            serverName = "";
        }

        if (playerIP == null) {
            playerIP = "";
        }
        if (displayName == null) {
            displayName = "";
        }
        plugin.logDebug("[P]Raw message: " + message);
        return message.replace("%DISPLAYNAME%", displayName)
                .replace("%NAME%", pName)
                .replace("%SERVER%", serverName)
                .replace("%PLAYERIP%", playerIP)
                .replace("%WORLDALIAS%", worldAlias)
                .replace("%WORLD%", worldName);
    }

    private String playerTokenizer(String player, String message) {
        plugin.logDebug("Tokenizing " + player);
        String worldName = plugin.defaultPlayerWorld;
        String displayName = plugin.getDisplayName(player);
        plugin.logDebug("[S]Raw message: " + message);
        return message.replace("%DISPLAYNAME%", displayName)
                .replace("%NAME%", player)
                .replace("%WORLD%", worldName);
    }

    /**
     *
     * @param player
     * @param template
     * @param cmd
     * @param params
     * @return
     */
    public String gameCommandToIRCTokenizer(ProxiedPlayer player, String template, String cmd, String params) {
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%COMMAND%", cmd)
                .replace("%PARAMS%", params));
    }

    public String targetChatResponseTokenizer(String target, String message, String template) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%TARGET%", target)
                .replace("%MESSAGE%", message)
        );
    }

    public String msgChatResponseTokenizer(String target, String message, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%TARGET%", target)
                .replace("%MESSAGE%", message)
        );
    }

    public String logTailerTokenizer(String file, String line, String template) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%FILE%", file)
                .replace("%LINE%", line));
    }

    /**
     * Herochat to IRC
     *
     * @param player
     * @param cm
     * @param template
     * @return
     */
    public String chatHeroTokenizer(ProxiedPlayer player, ChatMessage cm, String template) {
        return gameChatToIRCTokenizer(player, template, cm.getMessage())
                .replace("%GROUP%", cm.getPlayerGroup())
                .replace("%PLAYERPREFIX%", cm.getPlayerPrefix())
                .replace("%PLAYERSUFFIX%", cm.getPlayerSuffix())
                .replace("%GROUPPREFIX%", cm.getGroupPrefix())
                .replace("%GROUPSUFFIX%", cm.getGroupSuffix())
                .replace("%HEROCHANNEL%", cm.getChannel())
                .replace("%HERONICK%", cm.getHeroNick())
                .replace("%HEROCOLOR%", plugin.colorConverter.gameColorsToIrc(cm.getHeroColor()))
                .replace("%CHANNEL%", cm.getChannel());
    }

    public String bungeeChatTokenizer(ProxiedPlayer player, BungeeChatMessage cm, String template) {
        return gameChatToIRCTokenizer(player, template, cm.getMessage())
                .replace("%MVCHANNEL%", cm.getChannel())
                .replace("%CHANNEL%", cm.getChannel());
    }

    public String ircChatToBungeeChatTokenizer(PurpleBot ircBot, User user, org.pircbotx.Channel channel, String template, String message, String bcChannel) {
        String ircNick = user.getNick();
        String tmpl;
        ProxiedPlayer player = this.getPlayer(ircNick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(ircNick, template);
        }
        return plugin.colorConverter.ircColorsToGame(ircUserTokenizer(tmpl, user, ircBot)
                .replace("%BCCHANNEL%", bcChannel)
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channel.getName()));
    }

    /**
     * IRC to Hero chat channel tokenizer
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param template
     * @param message
     * @param hChannel
     * @return
     */
    public String ircChatToHeroChatTokenizer(PurpleBot ircBot, User user, org.pircbotx.Channel channel, String template, String message, String hChannel) {
        String ircNick = user.getNick();
        String tmpl;
        ProxiedPlayer player = this.getPlayer(ircNick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(ircNick, template);
        }
        return plugin.colorConverter.ircColorsToGame(ircUserTokenizer(tmpl, user, ircBot)
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%NICKPREFIX%", ircBot.getNickPrefix(user, channel))
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channel.getName()));
    }

    public String ircUserTokenizer(String template, User user, PurpleBot ircBot) {
        String host = user.getHostmask();
        String server = user.getServer();
        String away = user.getAwayMessage();
        String ircNick = user.getNick();
        String customPrefix = ircBot.defaultCustomPrefix;
        if (host == null) {
            host = "";
        }
        if (server == null) {
            server = "";
        }
        if (away == null) {
            away = "";
        }
        plugin.logDebug("customPrefix before: " + customPrefix);
        if (!ircBot.userPrefixes.isEmpty()) {
            for (String key : ircBot.userPrefixes.keySet()) {
                if (key.equalsIgnoreCase(user.getNick()) || ircBot.checkUserMask(user, key)) {
                    customPrefix = ircBot.userPrefixes.get(key);
                    break;
                }
            }
        }
        plugin.logDebug("customPrefix after: " + customPrefix);
        return template.replace("%HOST%", host)
                .replace("%CUSTOMPREFIX%", customPrefix)
                .replace("%NAME%", ircNick)
                .replace("%SERVER%", server)
                .replace("%AWAY%", away);
    }

    private ProxiedPlayer getPlayer(String name) {
        ProxiedPlayer player = null;
        if (plugin.exactNickMatch) {
            plugin.logDebug("Checking for exact player matching " + name);
            for (ServerInfo server : plugin.getProxy().getServers().values()) {
                for (ProxiedPlayer p : server.getPlayers()) {
                    if (p.getName().equals(name)) {
                        player = p;
                    }
                }
            }
        } else {
            plugin.logDebug("Checking for player matching " + name);
            for (ServerInfo server : plugin.getProxy().getServers().values()) {
                for (ProxiedPlayer p : server.getPlayers()) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        player = p;
                    }
                }
            }
        }
        return player;
    }
}

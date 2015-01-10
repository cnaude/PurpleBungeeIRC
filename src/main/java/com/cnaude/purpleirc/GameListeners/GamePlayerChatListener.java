 package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class GamePlayerChatListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onChatEvent(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            plugin.logDebug("Sender not player");
            return;
        }
        if (event.getMessage().startsWith("/")) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)event.getSender();

        if (player.hasPermission("irc.message.gamechat")) {
            plugin.logDebug("Player " + player.getName() + " has permission irc.message.gamechat");
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.gameChat(player, event.getMessage());
            }
        } else {
            plugin.logDebug("Player " + player.getName() + " does not have irc.message.gamechat permission.");
        }
    }
}

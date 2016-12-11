package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.ChatMessage;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeCordListener implements Listener {

    final private PurpleIRC plugin;

    public BungeeCordListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void receievePluginMessage(String pluginChannel, ProxiedPlayer player, byte[] bytes) throws IOException {
        if (!pluginChannel.equalsIgnoreCase("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        ChatMessage cm = new ChatMessage(in);
        plugin.logDebug("Received BungeeChat event from PluginMessageEvent: " + cm.getSubChannel());
        if (!cm.getSubChannel().equals("PurpleBungeeIRC")) {
            plugin.logDebug("Dropping non PurpleBungeeIRC message.");
        }

        cm.setMessage(ChatColor.translateAlternateColorCodes('&', cm.getMessage()));

        if (player != null) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    plugin.logDebug("Calling heroChat from receievePluginMessage for " + ircBot.botNick);
                    ircBot.heroChat(player, cm);
                }
            }
        }
    }
}

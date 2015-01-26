/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jumanjicraft.BungeeChatServer.BungeeChatServer;
import java.io.IOException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GamePluginMessageListener
        implements Listener {

    final private PurpleIRC plugin;
    final private BungeeChatServer bungeeChatServer;

    public GamePluginMessageListener(PurpleIRC plugin) {
        this.plugin = plugin;
        this.bungeeChatServer = (BungeeChatServer) plugin.getProxy().getPluginManager().getPlugin("BungeeChatBungee");
    }

    @EventHandler
    public void receievePluginMessage(PluginMessageEvent event) throws IOException {
        if (!event.getTag().equalsIgnoreCase("BungeeChat")) {
            return;
        }
        if (bungeeChatServer == null) {
            return;
        }
        byte[] bytes = event.getData();
        if (!bungeeChatServer.shouldBroadcast(ByteStreams.newDataInput(event.getData()).readUTF())) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String hChannel = in.readUTF();
        String message = in.readUTF();
        String playerName = in.readUTF();
        String hColor = in.readUTF();
        String hNick = in.readUTF();       
        plugin.logDebug("message: " + message);
        plugin.logDebug("playerName: " + playerName);
        plugin.logDebug("hColor: " + hColor);
        plugin.logDebug("hNick: " + hNick);
        ProxiedPlayer player = null;
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (ServerInfo server : plugin.getProxy().getServers().values()) {
            for (ProxiedPlayer p : server.getPlayers()) {
                if (p.getName().equals(playerName)) {
                    player = p;
                }
            }
        }
        if (player != null) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.heroChat(player, hChannel, message, hColor, hNick);
                }
            }
        }
    }
}

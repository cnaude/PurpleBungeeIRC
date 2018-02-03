/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import dev.aura.bungeechat.event.BungeeChatLeaveEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class BungeeChatLeaveListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public BungeeChatLeaveListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onBungeeChatLeaveEvent(final BungeeChatLeaveEvent event) {
        plugin.logDebug("BungeeChatLeaveEvent: " + event.getPlayer().getName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Player "
                    + event.getPlayer().getName()
                    + " was in the recently kicked list. Not sending leave message.");
            return;
        }
        if (event.getPlayer().getServer() != null) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.gameQuit(event.getPlayer(), "quit server");
            }
        }
    }
}

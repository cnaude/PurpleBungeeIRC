/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import dev.aura.bungeechat.event.BungeeChatJoinEvent;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class BungeeChatJoinListener implements Listener {
    
    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public BungeeChatJoinListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onBungeeChatJoinEvent(final BungeeChatJoinEvent event) {
        plugin.logDebug("BungeeChatJoinEvent: " + event.getPlayer().getDisplayName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Removing player "
                    + event.getPlayer().getName()
                    + " from the recently kicked list.");
        }

        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isConnected()) {
                    return;
                }
                ServerInfo si;
                try {
                    si = event.getPlayer().getServer().getInfo();
                } catch (Exception ex) {
                    plugin.logDebug("onBungeeChatJoinEvent: " + ex.getMessage());
                    return;
                }
                if (si != null) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.bungeeChatJoin(event.getPlayer(), "joined");
                        plugin.updateServerCache(event.getPlayer().getServer().getInfo());
                    }
                }
            }
        }, 2, TimeUnit.SECONDS);
    }
}

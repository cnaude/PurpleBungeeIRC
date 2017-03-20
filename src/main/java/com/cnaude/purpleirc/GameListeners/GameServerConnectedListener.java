package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class GameServerConnectedListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GameServerConnectedListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onServerConnected(final ServerConnectedEvent event) {
        plugin.logDebug("ServerConnectedEvent: " + event.getPlayer().getDisplayName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Removing player "
                    + event.getPlayer().getName()
                    + " from the recently kicked list.");
        }

        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                String serverName = event.getServer().getInfo().getName();
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameServerConnected(event.getPlayer(), "connected");
                    if (event.getPlayer().getServer() != null) {
                        plugin.updateServerCache(event.getPlayer().getServer().getInfo());
                    }

                }
            }
        }, 2, TimeUnit.SECONDS);
    }
}

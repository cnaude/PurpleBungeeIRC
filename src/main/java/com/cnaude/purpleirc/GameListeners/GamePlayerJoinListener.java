package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class GamePlayerJoinListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerJoinListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        plugin.logDebug("PlayerJoinEvent: " + event.getPlayer().getDisplayName());
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
                    plugin.logDebug("onPostLogin: " + ex.getMessage());
                    return;
                }
                if (si != null) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.gameJoin(event.getPlayer(), "joined");
                        plugin.updateServerCache(event.getPlayer().getServer().getInfo());
                    }
                }
            }
        }, 2, TimeUnit.SECONDS);
    }
}

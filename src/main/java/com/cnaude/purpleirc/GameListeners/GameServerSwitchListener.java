package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author cnaude
 */
public class GameServerSwitchListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GameServerSwitchListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onServerSwitchEvent(final ServerSwitchEvent event) {
        plugin.logDebug("onServerSwitchEvent: " + event.getPlayer().getDisplayName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Removing player "
                    + event.getPlayer().getName()
                    + " from the recently kicked list.");
        }

        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameServerSwitch(event.getPlayer(), "switched");
                }
            }
        }, 2, TimeUnit.SECONDS);
    }
    
}

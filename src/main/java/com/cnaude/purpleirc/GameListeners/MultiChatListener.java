package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.bungee.events.PostGlobalChatEvent;
import xyz.olivermartin.multichat.bungee.events.PostStaffChatEvent;

/**
 *
 * @author cnaude
 */
public class MultiChatListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public MultiChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPostGlobalChatEvent(PostGlobalChatEvent event) {
        plugin.ircBots.values().forEach((ircBot) -> {
            // player, message
            ircBot.multiChat(event.getSender(), event.getMessage());
        });

    }
    
        /**
     *
     * @param event
     */
    @EventHandler
    public void onPostBroadcastEvent(PostBroadcastEvent event) {
        plugin.ircBots.values().forEach((ircBot) -> {
            ircBot.multiChat(event.getMessage());
        });

    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPostStaffChatEvent(PostStaffChatEvent event) {
        plugin.ircBots.values().forEach((ircBot) -> {
            ircBot.multiChat(event.getSender(), event.getMessage());
        });

    }
}

/*
 * Copyright (C) 2014 cnaude
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cnaude.purpleirc.Events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 *
 * @author Chris Naude Event listener for plugins that want to catch irc message
 * events from PurpleIRC
 */
public class IRCMessageEvent extends Event {

    private String message;
    private final String channel;
    private final String permission;
    private final ProxiedPlayer player;

    /**
     *
     * @param message
     * @param channel
     * @param permission
     */
    public IRCMessageEvent(String message, String channel, String permission) {
        this.message = message;
        this.channel = channel;
        this.permission = permission;
        this.player = null;
    }

    /**
     *
     * @param message
     * @param channel
     * @param permission
     * @param player
     */
    public IRCMessageEvent(String message, String channel, String permission, ProxiedPlayer player) {
        this.message = message;
        this.channel = channel;
        this.permission = permission;
        this.player = player;
    }

    /**
     *
     * @return
     */
    public ProxiedPlayer getProxiedPlayer() {
        return this.player;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return this.message;
    }

    /**
     *
     * @return
     */
    public String getPermission() {
        return this.permission;
    }

    /**
     *
     * @return
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Change the IRC message being sent to the game
     *
     * @param message the message from IRC
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

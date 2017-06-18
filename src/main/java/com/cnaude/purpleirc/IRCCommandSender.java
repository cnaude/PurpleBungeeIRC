/*
 * Copyright (C) 2014 - 2017 cnaude
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
package com.cnaude.purpleirc;

import com.cnaude.purpleirc.IRCMessage.Type;
import java.util.Collection;
import java.util.Collections;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author Chris Naude We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCCommandSender implements CommandSender {

    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final Type responseType;

    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        plugin.logDebug("sendMessage[single]: " + message);
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(message), responseType));
    }

    /**
     *
     * @param ircBot
     * @param target
     * @param plugin
     * @param responseType
     */
    public IRCCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, Type responseType) {
        this.target = target;
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.responseType = responseType;
        }
    
    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "PurpleBot";
    }

    /**
     *
     * @param perm
     * @return
     */
    @Override
    public boolean hasPermission(final String perm) {
        plugin.logDebug("IRCCommandSender hasPermssion: " + perm);
        return true;
    }

    @Override
    public void sendMessages(String... strings) {
        for (String message : strings) {
            plugin.logDebug("sendMessage[multi]: " + message);
            ircBot.messageQueue.add(new IRCMessage(target,
                    plugin.colorConverter.gameColorsToIrc(message), responseType));
        }
    }

    @Override
    public void sendMessage(BaseComponent... bcs) {
        String message = TextComponent.toLegacyText(bcs);
        plugin.logDebug("sendMessage[bcs]: " + message);
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(message), responseType));

    }

    @Override
    public void sendMessage(BaseComponent bc) {
        plugin.logDebug("sendMessage[bc]: " + bc.toPlainText());
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(bc.toPlainText()), responseType));
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public void addGroups(String... strings) {
        throw new UnsupportedOperationException("Console may not have groups");
    }

    @Override
    public void removeGroups(String... strings) {
        throw new UnsupportedOperationException("Console may not have groups");
    }

    @Override
    public void setPermission(String string, boolean bln) {
        throw new UnsupportedOperationException("Console has all permissions");
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.emptySet();
    }
}

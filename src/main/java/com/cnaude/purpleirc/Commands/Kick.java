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
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author cnaude
 */
public class Kick implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel] [user(s)]";
    private final String desc = "Kick user(s) from IRC channel.";
    private final String name = "kick";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Kick(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 4) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                for (int i = 3; i < args.length; i++) {
                    // #channel, user
                    plugin.ircBots.get(bot).kick(channelName, args[3]);
                    sender.sendMessage(new TextComponent(ChatColor.WHITE + "Kicking " + args[i] + " from " + channelName + "..."));
                }
            } else {
                sender.sendMessage(new TextComponent(plugin.invalidBotName.replace("%BOT%", bot)));
            }
        } else {
            sender.sendMessage(new TextComponent(fullUsage));
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}

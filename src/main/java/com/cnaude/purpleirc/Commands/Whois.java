/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author cnaude
 */
public class Whois implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) [nick]";
    private final String desc = "Get whois info for IRC user.";
    private final String name = "whois";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Whois(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String nick = args[1];
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.sendUserWhois(sender, nick);
            }
        } else if (args.length == 3) {
            String bot = args[1];
            String nick = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                PurpleBot ircBot = plugin.ircBots.get(bot);
                ircBot.sendUserWhois(sender, nick);
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

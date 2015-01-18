/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class Help implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([command])";
    private final String desc = "Display help on a specific command or list all commands.";
    private final String name = "help";

    /**
     *
     * @param plugin
     */
    public Help(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            String s = args[1];
            if (plugin.commandHandlers.commands.containsKey(s)) {
                sender.sendMessage(new TextComponent(helpStringBuilder(
                        plugin.commandHandlers.commands.get(s).name(),
                        plugin.commandHandlers.commands.get(s).desc(),
                        plugin.commandHandlers.commands.get(s).usage())));
                return;
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid sub command: " 
                        + ChatColor.WHITE + s));
                return;
            }
        }
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                "&5-----[  &fPurpleIRC&5 - &f" + plugin.getProxy().getPluginManager()
                .getPlugin("PurpleBungeeIRC").getDescription().getVersion() + "&5 ]-----")));
        for (String s : plugin.commandHandlers.sortedCommands) {
            if (plugin.commandHandlers.commands.containsKey(s)) {
                sender.sendMessage(new TextComponent(helpStringBuilder(
                        plugin.commandHandlers.commands.get(s).name(),
                        plugin.commandHandlers.commands.get(s).desc(),
                        plugin.commandHandlers.commands.get(s).usage())));
            }
        }

    }

    private String helpStringBuilder(String n, String d, String u) {
        return ChatColor.translateAlternateColorCodes('&', "&5/irc "
                + n + " &6" + u + " &f- " + d);
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

package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author cnaude
 */
public class ListVoices implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel]";
    private final String desc = "List IRC user mask in auto-voice list.";
    private final String name = "listvoices";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public ListVoices(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (plugin.ircBots.get(bot).voicesList.containsKey(channelName)) {
                    sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                            + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + "Auto Voice Masks" + ChatColor.DARK_PURPLE + " ]-----"));
                    for (String userMask : plugin.ircBots.get(bot).voicesList.get(channelName)) {
                        sender.sendMessage(new TextComponent(" - " + userMask));
                    }
                } else {
                    sender.sendMessage(new TextComponent(plugin.invalidChannel.replace("%CHANNEL%", channelName)));
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

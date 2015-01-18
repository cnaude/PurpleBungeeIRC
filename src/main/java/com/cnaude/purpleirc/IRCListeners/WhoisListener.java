package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.text.SimpleDateFormat;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.WhoisEvent;

/**
 *
 * @author cnaude
 */
public class WhoisListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public WhoisListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onWhois(WhoisEvent event) {
        if (ircBot.whoisSenders.isEmpty()) {
            return;
        }
        CommandSender sender = ircBot.whoisSenders.remove(0);
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "----[ " + ChatColor.WHITE + "Whois" + ChatColor.DARK_PURPLE + " ]----"));
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Nick: " + ChatColor.WHITE + event.getNick()));        
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Username: " + ChatColor.WHITE + event.getLogin() + "@" + event.getHostname()));
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Real name: " + ChatColor.WHITE + event.getRealname()));
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Server: " + ChatColor.WHITE + event.getServer()));
        User user = null;
        for (Channel channel : ircBot.getBot().getUserBot().getChannels()) {
            for (User u : channel.getUsers()) {
                if (u.getNick().equalsIgnoreCase(event.getNick())) {
                    user = u;
                    break;
                }
            }
        }
        if (user != null) {
            if (user.isAway()) {
                sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Away: " + ChatColor.WHITE + user.getAwayMessage()));
            }
        }
        if (!event.getChannels().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Object channel : (List<String>)event.getChannels()) {                
                sb.append(" ");
                sb.append(channel);
            }
            sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Currently on:" + ChatColor.WHITE + sb.toString()));
        }
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Idle: " + ChatColor.WHITE + secondsToTime(event.getIdleSeconds())));
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "Online since: " + ChatColor.WHITE + secondsToDate(event.getSignOnTime())));        
        sender.sendMessage(new TextComponent(ChatColor.DARK_PURPLE + "----[ " + ChatColor.WHITE + "End Whois" + ChatColor.DARK_PURPLE + " ]----"));
    }
    
    private String secondsToDate(long sec) {                          
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (sec * 1000));
    }
    
    private String secondsToTime(long sec) {
        int idleDays = (int) (sec / 86400L);
        int idleHours = (int) (sec / 3600L % 24L);
        int idleMinutes = (int) (sec / 60L % 60L);
        int idleSeconds = (int) (sec % 60L);
        String msg = "";
        if (idleDays > 0) {
            msg = idleDays + " day";
            if (idleDays > 1) {
                msg = msg + "s";
            }
            msg = msg + " ";
        }
        if (idleHours > 0) {
            msg = idleHours + " hour";
            if (idleHours > 1) {
                msg = msg + "s";
            }
            msg = msg + " ";
        }
        if (idleMinutes > 0 ) {
            msg = msg + idleMinutes + " minute";
            if (idleMinutes > 1) {
                msg = msg + "s";
            }
            msg = msg + " ";
        }
        if (idleSeconds > 0 ) {
            msg = msg + idleSeconds + " second";
            if (idleSeconds > 1) {
                msg = msg + "s";
            }
            msg = msg + " ";
        }
        return msg;
    }
}

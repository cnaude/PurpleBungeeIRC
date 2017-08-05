package com.cnaude.purpleirc.Hooks;

import codecrafter47.bungeetablistplus.api.bungee.BungeeTabListPlusAPI;
import codecrafter47.bungeetablistplus.api.bungee.FakePlayerManager;
import codecrafter47.bungeetablistplus.api.bungee.Icon;
import codecrafter47.bungeetablistplus.api.bungee.tablist.FakePlayer;
import com.cnaude.purpleirc.IRCServerInfo;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.config.ServerInfo;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class BungeeTabListPlusHook {

    PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public BungeeTabListPlusHook(PurpleIRC plugin) {
        this.plugin = plugin;
        plugin.logInfo("Hooking into BungeeTabPlus");
    }

    /**
     *
     * @param name
     * @param ircBot
     * @param channel
     */
    public void addToTabList(String name, PurpleBot ircBot, Channel channel) {
        if (!plugin.customTabList) {
            return;
        }
        String displayName = truncateName(plugin.customTabPrefix + name);
        if (isPlayerOnline(name, ircBot, channel.getName())) {
            return;
        }
        String channelName = channel.getName();
        if (ircBot.tabIgnoreNicks.containsKey(channelName)) {
            for (String s : ircBot.tabIgnoreNicks.get(channelName)) {
                if (s.equalsIgnoreCase(name)) {
                    plugin.logDebug("Not adding " + name + " to tab list.");
                    return;
                }
            }
        }
        FakePlayerManager fakePlayerManager = BungeeTabListPlusAPI.getFakePlayerManager();
        Collection<FakePlayer> onlineFakePlayers = fakePlayerManager.getOnlineFakePlayers();
        for (FakePlayer fp : onlineFakePlayers) {
            if (fp.getName().equalsIgnoreCase(displayName)) {
                plugin.logDebug("[addToTabList] [fp skip]: " + displayName);
                return;
            }
        }
        plugin.logDebug("[addToTabList] [fp]: " + displayName);
        ServerInfo server = new IRCServerInfo(plugin.ircMinecraftServerName);
        FakePlayer fakePlayer = fakePlayerManager.createFakePlayer(displayName, server);
        fakePlayer.setPing(47);
        if (!plugin.customTabIcon.isEmpty()) {            
            fakePlayer.setIcon(Icon.DEFAULT);
        }

    }

    /**
     *
     * @param name
     */
    public void remFromTabList(String name) {
        if (!plugin.customTabList) {
            return;
        }
        String displayName = truncateName(plugin.customTabPrefix + name);
        FakePlayerManager fakePlayerManager = BungeeTabListPlusAPI.getFakePlayerManager();
        Collection<FakePlayer> onlineFakePlayers = fakePlayerManager.getOnlineFakePlayers();
        for (FakePlayer fp : onlineFakePlayers) {
            if (fp.getName().equalsIgnoreCase(displayName)) {
                plugin.logDebug("[remFromTabList]: " + displayName);
                fakePlayerManager.removeFakePlayer(fp);
                return;
            }
        }

    }

    /**
     *
     * @param ircBot
     * @param channel
     */
    public void updateTabList(final PurpleBot ircBot, final Channel channel) {
        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                for (User user : channel.getUsers()) {
                    String nick = user.getNick();
                    addToTabList(nick, ircBot, channel);
                }
            }
        }, 5, TimeUnit.SECONDS);

    }

    /**
     *
     */
    public void updateTabList() {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                for (Channel channel : ircBot.getChannels()) {
                    if (ircBot.isValidChannel(channel.getName())) {
                        updateTabList(ircBot, channel);
                    }
                }
            }
        }
    }

    private boolean isPlayerOnline(String name, PurpleBot ircBot, String channel) {
        /*
        if (ircBot.tabIgnoreDuplicates.containsKey(channel)) {
            if (ircBot.tabIgnoreDuplicates.get(channel)) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (name.equalsIgnoreCase(player.getName())) {
                        plugin.logDebug("Not adidng to tab list due to player with same name.");
                        return true;
                    }
                }
            }
        }
         */
        return false;
    }

    private String truncateName(String name) {
        if (name.length() > 16) {
            return name.substring(0, 15);
        } else {
            return name;
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import dev.aura.bungeechat.api.BungeeChatApi;
import dev.aura.bungeechat.api.account.AccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.account.ConsoleAccount;
import dev.aura.bungeechat.api.placeholder.BungeeChatContext;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class BungeeChatHook {

    PurpleIRC plugin;
    BungeeChatApi instance;
    Plugin bcPlugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public BungeeChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.bcPlugin = plugin.getProxy().getPluginManager().getPlugin("BungeeChat");
        if (bcPlugin != null) {
            this.instance = (BungeeChatApi) bcPlugin;
        }
        plugin.logInfo("Hooking into " + bcPlugin.getDescription().getName() + " " + bcPlugin.getDescription().getVersion());
    }

    public void sendChannelMessage(User user, String channel, String message) {
        BungeeChatContext bcc = new BungeeChatContext();   
        bcc.setSender(new BungeeChatFakeAccount(user.getNick()));
        bcc.setMessage(message);
        bcc.setChannel(channel.toUpperCase());
        plugin.logDebug("Sending message from " + user.getNick() + " to BungeeChat [" + channel + "]: " + message);
        this.instance.sendChannelMessage(bcc);        
    }

}

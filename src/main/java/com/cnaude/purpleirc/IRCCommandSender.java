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

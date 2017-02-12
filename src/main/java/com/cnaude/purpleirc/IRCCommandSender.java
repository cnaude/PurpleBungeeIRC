package com.cnaude.purpleirc;

import java.util.Collection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author Chris Naude We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCCommandSender implements CommandSender {

    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final boolean ctcpResponse;

    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        plugin.logDebug("sendMessage[single]: " + message);
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(message), ctcpResponse));
    }

    /**
     *
     * @param ircBot
     * @param target
     * @param plugin
     * @param ctcpResponse
     */
    public IRCCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, boolean ctcpResponse) {
        this.target = target;
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.ctcpResponse = ctcpResponse;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "CONSOLE";
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean hasPermission(final String arg0) {
        return true;
    }

    @Override
    public void sendMessages(String... strings) {
        for (String message : strings) {
            plugin.logDebug("sendMessage[multi]: " + message);
            ircBot.messageQueue.add(new IRCMessage(target,
                    plugin.colorConverter.gameColorsToIrc(message), ctcpResponse));
        }
    }

    @Override
    public void sendMessage(BaseComponent... bcs) {
        for (BaseComponent bc : bcs) {
            plugin.logDebug("sendMessage[bcs]: " + bc.toPlainText());
            ircBot.messageQueue.add(new IRCMessage(target,
                    plugin.colorConverter.gameColorsToIrc(bc.toPlainText()), ctcpResponse));
        }
    }

    @Override
    public void sendMessage(BaseComponent bc) {
        plugin.logDebug("sendMessage[bc]: " + bc.toPlainText());
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(bc.toPlainText()), ctcpResponse));
    }

    @Override
    public Collection<String> getGroups() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addGroups(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeGroups(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPermission(String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<String> getPermissions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

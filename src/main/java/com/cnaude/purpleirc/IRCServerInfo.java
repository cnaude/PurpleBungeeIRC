package com.cnaude.purpleirc;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by krachynski on 2016-12-14.
 */
public class IRCServerInfo implements ServerInfo {
    private final String ircMinecraftServerName;

    public IRCServerInfo(String ircMinecraftServerName) {
        this.ircMinecraftServerName = ircMinecraftServerName;
    }

    @Override
    public String getName() {
        return ircMinecraftServerName;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        return null;
    }

    @Override
    public String getMotd() {
        return null;
    }

    @Override
    public boolean canAccess(CommandSender commandSender) {
        return false;
    }

    @Override
    public void sendData(String s, byte[] bytes) {

    }

    @Override
    public boolean sendData(String s, byte[] bytes, boolean b) {
        return false;
    }

    @Override
    public void ping(Callback<ServerPing> callback) {

    }
}

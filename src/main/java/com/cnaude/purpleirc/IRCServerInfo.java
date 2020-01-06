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

    @Override
    public boolean isRestricted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPermission() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

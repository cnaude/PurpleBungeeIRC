/*
 * Copyright (C) 2014 cnaude
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {

    private final PurpleIRC plugin;
    private final ScheduledTask bt;
    private final Queue<IRCCommand> queue = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param plugin
     */
    public CommandQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        bt = this.plugin.getProxy().getScheduler().schedule(this.plugin, new Runnable() {
            @Override
            public void run() {
                queueAndSend();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void queueAndSend() {
        IRCCommand ircCommand = queue.poll();
        if (ircCommand != null) {
            try {
                plugin.logDebug("Dispatching command as IRCCommandSender: " + ircCommand.getGameCommand());
                boolean success = plugin.getProxy().getPluginManager().dispatchCommand(
                        ircCommand.getIRCCommandSender(),
                        ircCommand.getGameCommand());
                plugin.logDebug("Command success?: " + success);
            } catch (Exception ce) {
                plugin.logError("Error running command: " + ce.getMessage());
            }
        }
    }

    public void cancel() {
        bt.cancel();
    }

    public String clearQueue() {
        int size = queue.size();
        if (!queue.isEmpty()) {
            queue.clear();
        }
        return "Elements removed from command queue: " + size;
    }

    /**
     *
     * @param command
     */
    public void add(IRCCommand command) {
        plugin.logDebug("Adding command to queue: " + command.getGameCommand());
        queue.offer(command);
    }
}

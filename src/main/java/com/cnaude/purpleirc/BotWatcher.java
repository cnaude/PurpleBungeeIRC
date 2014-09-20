package com.cnaude.purpleirc;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 * This thread checks each bot for connectivity and reconnects when appropriate.
 *
 * @author Chris Naude
 *
 */
public class BotWatcher {

    private final PurpleIRC plugin;
    private final ScheduledTask bt;

    /**
     *
     * @param plugin
     */
    public BotWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        bt = this.plugin.getProxy().getScheduler().schedule(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnectedBlocking()) {
                        plugin.logDebug("[" + ircBot.botNick + "] CONNECTED");
                        ircBot.setConnected(true);
                    } else {
                        ircBot.setConnected(false);
                        if (ircBot.autoConnect) {
                            plugin.logInfo("[" + ircBot.botNick + "] NOT CONNECTED");
                            ircBot.reload();
                        }
                    }
                }
            }
        }, plugin.ircConnCheckInterval, plugin.ircConnCheckInterval, TimeUnit.SECONDS);
    }

    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }

}

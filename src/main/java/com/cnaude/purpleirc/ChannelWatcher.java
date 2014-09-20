package com.cnaude.purpleirc;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author Chris Naude This thread checks each for users and updates the
 * internal lists.
 */
public class ChannelWatcher {

    private final PurpleIRC plugin;
    private final ScheduledTask bt;

    /**
     *
     * @param plugin
     */
    public ChannelWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        
        bt = this.plugin.getProxy().getScheduler().schedule(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.updateNickList();
                }
            }
        }, plugin.ircChannelCheckInterval, plugin.ircChannelCheckInterval, TimeUnit.SECONDS);
    }

    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }

}

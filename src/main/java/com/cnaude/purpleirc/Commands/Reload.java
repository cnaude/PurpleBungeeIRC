package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author cnaude
 */
public class Reload implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "";
    private final String desc = "Reload the plugin.";
    private final String name = "reload";

    /**
     *
     * @param plugin
     */
    public Reload(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("Disabling PurpleIRC..."));
        plugin.getProxy().getPluginManager().getPlugin("PurpleBungeeIRC").onDisable();
        sender.sendMessage(new TextComponent("Enabling PurpleIRC..."));
        plugin.getProxy().getPluginManager().getPlugin("PurpleBungeeIRC").onDisable();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}

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

import com.cnaude.purpleirc.Commands.*;
import com.google.common.base.Joiner;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author cnaude
 */
public class CommandHandlers extends Command {

    public HashMap<String, IRCCommandInterface> commands = new HashMap<>();
    public ArrayList<String> sortedCommands = new ArrayList<>();
    private final PurpleIRC plugin;

    public CommandHandlers(PurpleIRC plugin) {
        super("irc");
        this.plugin = plugin;

        commands.put("addop", new AddOp(plugin));
        commands.put("addvoice", new AddVoice(plugin));
        commands.put("connect", new Connect(plugin));
        commands.put("ctcp", new CTCP(plugin));
        commands.put("deop", new DeOp(plugin));
        commands.put("devoice", new DeVoice(plugin));
        commands.put("debug", new Debug(plugin));
        commands.put("disconnect", new Disconnect(plugin));
        commands.put("join", new Join(plugin));
        commands.put("kick", new Kick(plugin));
        commands.put("leave", new Leave(plugin));
        commands.put("list", new List(plugin));
        commands.put("listbots", new ListBots(plugin));
        commands.put("listops", new ListOps(plugin));
        commands.put("listvoices", new ListVoices(plugin));
        commands.put("login", new Login(plugin));
        commands.put("messagedelay", new MessageDelay(plugin));
        commands.put("msg", new Msg(plugin));
        commands.put("motd", new Motd(plugin));
        commands.put("mute", new Mute(plugin));
        commands.put("mutelist", new MuteList(plugin));
        commands.put("nick", new Nick(plugin));
        commands.put("nickserv", new Nickserv(plugin));
        commands.put("notice", new Notice(plugin));
        commands.put("op", new Op(plugin));
        commands.put("reload", new Reload(plugin));
        commands.put("reloadbot", new ReloadBot(plugin));
        commands.put("reloadbotconfig", new ReloadBotConfig(plugin));
        commands.put("reloadbotconfigs", new ReloadBotConfigs(plugin));
        commands.put("reloadbots", new ReloadBots(plugin));
        commands.put("reloadconfig", new ReloadConfig(plugin));
        commands.put("removeop", new RemoveOp(plugin));
        commands.put("removevoice", new RemoveVoice(plugin));
        commands.put("save", new Save(plugin));
        commands.put("say", new Say(plugin));
        commands.put("send", new Send(plugin));
        commands.put("sendraw", new SendRaw(plugin));
        commands.put("server", new Server(plugin));
        commands.put("topic", new Topic(plugin));
        commands.put("unmute", new UnMute(plugin));
        commands.put("voice", new Voice(plugin));
        commands.put("whois", new Whois(plugin));
        commands.put("help", new Help(plugin));
        commands.put("znc", new Znc(plugin));

        for (String s : commands.keySet()) {
            sortedCommands.add(s);
        }
        Collections.sort(sortedCommands, Collator.getInstance());
        plugin.logDebug("Commands enabled: " + Joiner.on(", ").join(sortedCommands));
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.logDebug("ARGS: " + Joiner.on(" ").join(args));
        if (args.length >= 1) {
            String subCmd = args[0].toLowerCase();
            if (commands.containsKey(subCmd)) {
                if (!sender.hasPermission("irc." + subCmd)) {
                    sender.sendMessage(new TextComponent(plugin.noPermission));
                } else {
                    commands.get(subCmd).dispatch(sender, args);
                }
                return;
            }
        }
        commands.get("help").dispatch(sender, args);
    }

}

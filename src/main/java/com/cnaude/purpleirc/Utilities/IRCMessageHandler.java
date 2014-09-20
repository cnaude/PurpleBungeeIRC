package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import com.google.common.base.Joiner;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.config.ServerInfo;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class IRCMessageHandler {

    PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public IRCMessageHandler(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param message
     * @param privateMessage
     */
    public void processMessage(PurpleBot ircBot, User user, Channel channel, String message, boolean privateMessage) {
        plugin.logDebug("processMessage: " + message);
        String myChannel = channel.getName();
        if (ircBot.muteList.get(myChannel).contains(user.getNick())) {
            plugin.logDebug("User is muted. Ignoring message from " + user.getNick() + ": " + message);
            return;
        }
        if (message.startsWith(ircBot.commandPrefix) && (!message.startsWith(ircBot.commandPrefix + ircBot.commandPrefix))) {
            String command = message.split(" ")[0].substring(ircBot.commandPrefix.length());

            String commandArgs = null;
            if (message.contains(" ")) {
                commandArgs = message.split(" ", 2)[1];
            }
            plugin.logDebug("IRC command detected: " + command);

            plugin.logDebug(message);
            String target = channel.getName();
            if (ircBot.commandMap.get(myChannel).containsKey(command)) {
                boolean privateListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(myChannel).get(command).get("private_listen"));
                boolean channelListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(myChannel).get(command).get("channel_listen"));
                plugin.logDebug("privateListen: " + privateListen);
                plugin.logDebug("channelListen: " + channelListen);
                if (privateMessage && !privateListen) {
                    plugin.logDebug("This is a private message but privateListen is false. Ignoring...");
                    return;
                }
                if (!privateMessage && !channelListen) {
                    plugin.logDebug("This is a channel message but channelListen is false. Ignoring...");
                    return;
                }

                String gameCommand = (String) ircBot.commandMap.get(myChannel).get(command).get("game_command");
                String modes = (String) ircBot.commandMap.get(myChannel).get(command).get("modes");
                String perm = (String) ircBot.commandMap.get(myChannel).get(command).get("perm");
                boolean privateCommand = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("private"));
                boolean ctcpResponse = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("ctcp"));

                plugin.logDebug(gameCommand + ":" + modes + ":" + privateCommand);

                if (privateCommand || privateMessage) {
                    target = user.getNick();
                }

                plugin.logDebug("Target: " + target);

                if (isValidMode(modes, user, channel)) {
                    switch (gameCommand) {
                        case "@list":
                            for (ServerInfo si : plugin.getProxy().getServers().values()) {
                                sendMessage(ircBot, target, plugin.getMCPlayers(si, ircBot, myChannel), ctcpResponse);
                            }
                            
                            break;
                        case "@uptime":
                            sendMessage(ircBot, target, plugin.getMCUptime(), ctcpResponse);
                            break;
                        case "@help":
                            sendMessage(ircBot, target, getCommands(ircBot.commandMap, myChannel), ctcpResponse);
                            break;
                        case "@chat":
                            ircBot.broadcastChat(user, channel, commandArgs, false);
                            break;
                        case "@ochat":
                            ircBot.broadcastChat(user, channel, commandArgs, true);
                            break;
                        case "@msg":
                            ircBot.playerChat(user, channel, target, commandArgs);
                            break;
                        case "@clearqueue":
                            sendMessage(ircBot, target, ircBot.messageQueue.clearQueue(), ctcpResponse);
                            break;
                        case "@query":
                            sendMessage(ircBot, target, plugin.getRemotePlayers(commandArgs), ctcpResponse);
                            break;
                        default:
                            break;
                    }
                } else {
                    plugin.logDebug("User '" + user.getNick() + "' mode not okay.");
                    ircBot.asyncIRCMessage(target, plugin.getMsgTemplate(
                            ircBot.botNick, TemplateName.NO_PERM_FOR_IRC_COMMAND)
                            .replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                }
            } else {
                if (privateMessage || ircBot.invalidCommandPrivate.get(myChannel)) {
                    target = user.getNick();
                }
                plugin.logDebug("Invalid command: " + command);
                String invalidIrcCommand = plugin.getMsgTemplate(
                        ircBot.botNick, TemplateName.INVALID_IRC_COMMAND)
                        .replace("%NICK%", user.getNick())
                        .replace("%CMDPREFIX%", ircBot.commandPrefix);
                if (!invalidIrcCommand.isEmpty()) {
                    if (ircBot.invalidCommandCTCP.get(myChannel)) {
                        ircBot.blockingCTCPMessage(target, invalidIrcCommand);
                    } else {
                        ircBot.asyncIRCMessage(target, invalidIrcCommand);
                    }
                }
                if (ircBot.enabledMessages.get(myChannel).contains(TemplateName.INVALID_IRC_COMMAND)) {
                    plugin.logDebug("Invalid IRC command dispatched for broadcast...");
                    ircBot.broadcastChat(user, channel, message, false);
                }
            }
        } else {
            if (ircBot.ignoreIRCChat.get(myChannel)) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"ignore-irc-chat\" being true ...");
                return;
            }
            if (privateMessage && !ircBot.relayPrivateChat) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"relay-private-chat\" being false and this is a private message ...");
                return;
            }
            plugin.logDebug("Message dispatched for broadcast...");
            ircBot.broadcastChat(user, channel, message, false);
        }
    }

    private boolean isValidMode(String modes, User user, Channel channel) {
        boolean modeOkay = false;
        if (modes.equals("*")) {
            return true;
        }
        if (modes.contains("i") && !modeOkay) {
            modeOkay = user.isIrcop();
        }
        if (modes.contains("o") && !modeOkay) {
            modeOkay = user.getChannelsOpIn().contains(channel);
        }
        if (modes.contains("v") && !modeOkay) {
            modeOkay = user.getChannelsVoiceIn().contains(channel);
        }
        if (modes.contains("h") && !modeOkay) {
            modeOkay = user.getChannelsHalfOpIn().contains(channel);
        }
        if (modes.contains("q") && !modeOkay) {
            modeOkay = user.getChannelsOwnerIn().contains(channel);
        }
        if (modes.contains("s") && !modeOkay) {
            modeOkay = user.getChannelsSuperOpIn().contains(channel);
        }
        return modeOkay;
    }

    private void sendMessage(PurpleBot ircBot, String target, String message, boolean ctcpResponse) {
        if (ctcpResponse) {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncCTCPMessage(target, message);
        } else {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncIRCMessage(target, message);
        }
    }

    private String getCommands(CaseInsensitiveMap<CaseInsensitiveMap<CaseInsensitiveMap<String>>> commandMap, String myChannel) {
        if (commandMap.containsKey(myChannel)) {
            List<String> sortedCommands = new ArrayList<>();
            for (String command : commandMap.get(myChannel).keySet()) {
                sortedCommands.add(command);
            }
            Collections.sort(sortedCommands, Collator.getInstance());

            return "Valid commands: " + Joiner.on(", ").join(sortedCommands);
        }
        return "";
    }

}

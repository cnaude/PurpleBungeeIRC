package com.cnaude.purpleirc;

import com.google.common.io.ByteArrayDataInput;

/**
 *
 * @author cnaude
 */
public class ChatMessage {

    private String message = "";
    private String channel = "";
    private String sender = "";
    private String heroColor = "";
    private String heroNick = "";
    private String playerPrefix = "";
    private String playerSuffix = "";
    private String groupPrefix = "";
    private String groupSuffix = "";
    private String playerGroup = "";

    public ChatMessage(ByteArrayDataInput in) {
        channel = in.readUTF();
        message = in.readUTF();
        sender = in.readUTF();
        try {
            heroColor = in.readUTF();
            heroNick = in.readUTF();

            playerPrefix = in.readUTF();
            playerSuffix = in.readUTF();
            groupPrefix = in.readUTF();
            groupSuffix = in.readUTF();
            playerGroup = in.readUTF();
        } catch (IllegalStateException ex) {

        }
    }

    public ChatMessage() {

    }

    public void setMessage(String s) {
        this.message = s;
    }

    public void setChannelName(String s) {
        this.channel = s;
    }

    public void setSenderName(String s) {
        this.sender = s;
    }

    public void setHeroColor(String s) {
        this.heroColor = s;
    }

    public void setHeroNick(String s) {
        this.heroNick = s;
    }

    public void setPlayerPrefix(String s) {
        this.playerPrefix = s;
    }

    public void setPlayerSuffix(String s) {
        this.playerSuffix = s;
    }

    public void setGroupPrefix(String s) {
        this.groupPrefix = s;
    }

    public void setGroupSuffix(String s) {
        this.groupSuffix = s;
    }

    public void setGroup(String s) {
        this.playerGroup = s;
    }

    public String getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getHeroColor() {
        return heroColor;
    }

    public String getHeroNick() {
        return heroNick;
    }

    public String getPlayerPrefix() {
        return playerPrefix;
    }

    public String getPlayerSuffix() {
        return playerSuffix;
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

    public String getGroupSuffix() {
        return groupSuffix;
    }

    public String getPlayerGroup() {
        return playerGroup;
    }
}

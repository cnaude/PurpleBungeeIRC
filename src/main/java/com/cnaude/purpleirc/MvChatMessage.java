package com.cnaude.purpleirc;

import com.google.common.io.ByteArrayDataInput;

/**
 *
 * @author cnaude
 */
public class MvChatMessage
{

    private String fullMessage = "";
    private String strippedMsg = "";
    private String message = "";
    private String channel = "";
    private String sender = "";
    private String format = "";
    private String json = "";

    public MvChatMessage(ByteArrayDataInput in) {
        channel = readUTF(in);
        message = readUTF(in);
        sender = readUTF(in);
        strippedMsg = readUTF(in);

        format = readUTF(in);
        fullMessage = readUTF(in);
        json = readUTF(in);
    }

    private String readUTF(ByteArrayDataInput in) {
        try {
            return in.readUTF();
        } catch (IllegalStateException ex) {
        }
        return "";
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

    public String getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public String getSender() {
        return sender;
    }

    public String getJson()
    {
        return json;
    }

    public void setJson(String json)
    {
        this.json = json;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getStrippedMsg()
    {
        return strippedMsg;
    }

    public String getFullMessage()
    {
        return fullMessage;
    }
}

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
package com.cnaude.purpleirc.ChatMessages;

import com.google.common.io.ByteArrayDataInput;

/**
 *
 * @author cnaude
 */
public class BungeeChatMessage
{

    private String fullMessage = "";
    private String strippedMsg = "";
    private String message = "";
    private String channel = "";
    private String sender = "";
    private String format = "";
    private String json = "";

    public BungeeChatMessage(ByteArrayDataInput in) {
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
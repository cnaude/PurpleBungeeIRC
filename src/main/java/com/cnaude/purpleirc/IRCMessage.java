package com.cnaude.purpleirc;

/**
 *
 * @author cnaude
 */
public class IRCMessage {

    public String target;
    public String message;
    public boolean ctcpResponse;
    public final long timestamp;

    public IRCMessage(String target, String message, boolean ctcpResponse) {
        this.target = target;
        this.message = message;
        this.ctcpResponse = ctcpResponse;
        this.timestamp = System.currentTimeMillis();
    }
    
    public IRCMessage(String target, String message, boolean ctcpResponse, long timestamp) {
        this.target = target;
        this.message = message;
        this.ctcpResponse = ctcpResponse;
        this.timestamp = timestamp;
    }
}

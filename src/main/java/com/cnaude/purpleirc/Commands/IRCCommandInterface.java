package com.cnaude.purpleirc.Commands;

import net.md_5.bungee.api.CommandSender;

/**
 *
 * @author cnaude
 */
public interface IRCCommandInterface {
    void dispatch(CommandSender sender, String[] args);
    String name();
    String desc();
    String usage();    
}

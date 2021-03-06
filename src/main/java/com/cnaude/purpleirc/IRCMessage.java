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

/**
 *
 * @author Chris Naude
 */
public class IRCMessage {

    public String target;
    public String message;
    public Type type;

    public enum Type {
        MESSAGE,
        CTCP,
        NOTICE
    }

    public IRCMessage(String target, String message, Type type) {
        this.target = target;
        this.message = message;
        this.type = type;
    }
}

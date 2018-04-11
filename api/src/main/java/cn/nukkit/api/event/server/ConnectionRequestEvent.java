/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.event.server;

import cn.nukkit.api.event.Event;

import java.net.InetSocketAddress;

/**
 * Called when RakNet receives a connection request packet.
 * This is the earliest you can deny someone from entering the server
 */
public class ConnectionRequestEvent implements Event {
    private final InetSocketAddress address;
    private Result result;

    public ConnectionRequestEvent(InetSocketAddress address, Result result) {
        this.address = address;
        this.result = result;
    }

    /**
     * Get address of connecting user.
     * @return address
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Get current result of the user connecting.
     * @return result
     */
    public Result getResult() {
        return result;
    }

    /**
     * Set current result of the user connecting.
     * @param result
     */
    public void setResult(Result result) {
        this.result = result;
    }

    public enum Result {
        /**
         * Connecting user will continue to login.
         */
        CONTINUE,
        /**
         * Connecting user will be sent banned packet.
         */
        BANNED,
        /**
         * Connecting user will be sent no free connections packet.
         */
        SERVER_FULL,
        /**
         * Connecting user will be sent incompatible version packet.
         */
        INCOMPATIBLE_VERSION
    }
}

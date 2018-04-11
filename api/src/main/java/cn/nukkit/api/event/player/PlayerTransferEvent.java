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

package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;

/**
 * Just before a player is sent to another server with the transfer packet.
 */
public class PlayerTransferEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final String address;
    private final int port;
    private boolean cancelled = false;

    public PlayerTransferEvent(Player player, String address, int port) {
        this.player = player;
        this.address = address;
        this.port = port;
    }

    /**
     * Player to be transferred.
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if the transfer will be cancelled.
     *
     * @return transfer cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set whether the transfer will be cancelled.
     *
     * @param cancelled true to cancel the transfer.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get the address which the player will be sent to.
     *
     * @return address of target server
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the port which the player will be sent to.
     *
     * @return port of target server
     */
    public int getPort() {
        return port;
    }
}

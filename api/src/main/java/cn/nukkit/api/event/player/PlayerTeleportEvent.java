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
import cn.nukkit.api.util.Location;

public class PlayerTeleportEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private boolean cancelled;
    private TeleportCause cause;
    private final Location from;
    private Location to;

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public TeleportCause getCause() {
        return cause;
    }

    public void setCause(TeleportCause cause) {
        this.cause = cause;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        UNKNOWN        // Unknown cause
    }
}

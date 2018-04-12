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

package cn.nukkit.api;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

/**
 * A player that has previously connected to the server and their player data has been preserved.
 */
public interface OfflinePlayer {

    /**
     * Check whether the player is online or not.
     *
     * @return online
     */
    boolean isOnline();

    /**
     * Get the players name.
     *
     * @return name
     */
    @Nonnull
    String getName();

    /**
     * Get the player's UUID.
     *
     * @return UUID
     */
    @Nonnull
    UUID getUniqueId();

    /**
     * Get the player's XUID.
     *
     * @return XUID
     */
    @Nonnull
    Optional<String> getXuid();

    /**
     * Check if the player has been banned from the server.
     *
     * @return player banned
     */
    boolean isBanned();

    /**
     * Ban the player from the server.
     *
     * @param value
     */
    void setBanned(boolean value);

    /**
     * Check if the player is whitelisted on the server.
     *
     * @return whitelisted
     */
    boolean isWhitelisted();

    /**
     * Set whether the player is whitelisted on the server.
     *
     * @param value
     */
    void setWhitelisted(boolean value);
}

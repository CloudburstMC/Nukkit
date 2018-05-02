package com.nukkitx.api;

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

package cn.nukkit.api;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Interface that represents any kind of Player instance whether they be online or offline.
 */
public interface Stateless {

    /**
     * Check whether the player is online or not.
     * @return online
     */
    boolean isOnline();

    /**
     * Get the players name.
     * @return name
     */
    @Nonnull
    String getName();

    /**
     * Get the player's UUID.
     * @return UUID
     */
    @Nonnull
    UUID getUniqueId();

    /**
     * Check if the player has been banned from the server.
     * @return
     */
    boolean isBanned();

    /**
     * Ban the player from the server.
     * @param value
     */
    void setBanned(boolean value);

    /**
     * Check if the player is whitelisted on the server.
     * @return whitelisted
     */
    boolean isWhitelisted();

    /**
     * Set whether the player is whitelisted on the server.
     * @param value
     */
    void setWhitelisted(boolean value);
}

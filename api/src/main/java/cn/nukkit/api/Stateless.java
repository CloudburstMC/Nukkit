package cn.nukkit.api;

import javax.annotation.Nonnull;

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

    /**
     * The the time at which the player first joined.
     * @return time
     */
    long getFirstPlayed();

    /**
     * The the time at which the player last joined.
     * @return time
     */
    long getLastPlayed();
}

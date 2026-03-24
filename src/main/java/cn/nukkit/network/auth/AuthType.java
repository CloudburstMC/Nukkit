package cn.nukkit.network.auth;

public enum AuthType {
    /**
     * Player auth status is unknown. This is the default value for legacy clients.
     */
    UNKNOWN,
    /**
     * Player is authenticated directly to Mojang auth servers.
     */
    FULL,
    /**
     * Split screen player using the host's authentication token.
     */
    GUEST,
    /**
     * This player is not authenticated with Mojang auth servers.
     */
    SELF_SIGNED
}

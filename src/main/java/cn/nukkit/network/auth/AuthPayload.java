package cn.nukkit.network.auth;

public interface AuthPayload {

    /**
     * Returns the authentication type of the player.
     *
     * @return the authentication type
     */
    AuthType getAuthType();
}

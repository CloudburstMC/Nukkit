package cn.nukkit.server.network.protocol;

/**
 * Created on 15-10-13.
 */
public class PlayStatusPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAY_STATUS_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public Status status;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.status.ordinal());
    }

    public enum Status {
        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        PLAYER_SPAWN,
        LOGIN_FAILED_INVALID_TENANT,
        LOGIN_FAILED_VANILLA_EDU,
        LOGIN_FAILED_EDU_VANILLA
    }
}

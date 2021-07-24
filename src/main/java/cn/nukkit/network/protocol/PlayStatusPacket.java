package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class PlayStatusPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAY_STATUS_PACKET;

    public Status status;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.status = Status.values()[this.getInt()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.status.ordinal());
    }

    public static enum Status {

        SUCCESS,
        FAILED_CLIENT,
        FAILED_SERVER,
        PLAYER_SPAWN,
        FAILED_INVALID_TENANT,
        FAILED_VANILLA_EDU,
        FAILED_EDU_VANILLA,
        FAILED_SERVER_FULL
    }
}

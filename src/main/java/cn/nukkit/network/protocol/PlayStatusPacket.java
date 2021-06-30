package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class PlayStatusPacket extends DataPacket {

    public Status status;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAY_STATUS_PACKET;
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

        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        PLAYER_SPAWN,
        LOGIN_FAILED_INVALID_TENANT,
        LOGIN_FAILED_VANILLA_EDUCATION,
        LOGIN_FAILED_SERVER_FULL
    }
}

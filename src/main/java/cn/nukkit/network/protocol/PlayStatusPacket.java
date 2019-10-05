package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-13.
 */
@ToString
public class PlayStatusPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.PLAY_STATUS_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED_CLIENT = 1;
    public static final int LOGIN_FAILED_SERVER = 2;
    public static final int PLAYER_SPAWN = 3;
    public static final int LOGIN_FAILED_INVALID_TENANT = 4;
    public static final int LOGIN_FAILED_VANILLA_EDU = 5;
    public static final int LOGIN_FAILED_EDU_VANILLA = 6;
    public static final int LOGIN_FAILED_SERVER_FULL = 7;

    public int status;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeInt(this.status);
    }

}

package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class PlayStatusPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.PLAY_STATUS_PACKET;

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED_CLIENT = 1;
    public static final int LOGIN_FAILED_SERVER = 2;
    public static final int PLAYER_SPAWN = 3;

    public int status;
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        ;
    }

    @Override
    public void decode() {
        reset();
        putInt(status);
    }

}

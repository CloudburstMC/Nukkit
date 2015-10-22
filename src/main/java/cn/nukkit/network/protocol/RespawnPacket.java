package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class RespawnPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.RESPAWN_PACKET;

    public float x;
    public float y;
    public float z;

    @Override
    public void decode() {
        x = getFloat();
        y = getFloat();
        z = getFloat();
    }

    @Override
    public void encode() {
        reset();
        putFloat(x);
        putFloat(y);
        putFloat(z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

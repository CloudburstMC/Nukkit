package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.SET_SPAWN_POSITION_PACKET;

    public int y;
    public int z;
    public int x;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putInt(x);
        putInt(y);
        putInt(z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
    
}

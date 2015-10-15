package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class RemoveBlockPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.REMOVE_BLOCK_PACKET;

    public long eid;
    public int x;
    public int y;
    public byte z;

    @Override
    public void decode() {
        eid = getLong();
        x = getInt();
        y = getInt();
        z = getByte();
    }

    @Override
    public void encode() {
        ;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

package cn.nukkit.network.protocol;

/**
 * Created on 15-10-15.
 */
public class InteractPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.INTERACT_PACKET;

    public long eid;
    public int action;
    public long target;

    @Override
    public void decode() {
        action = getByte();
        target = getLong();
    }

    @Override
    public void encode() {
        reset();
        putByte(action);
        putLong(target);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

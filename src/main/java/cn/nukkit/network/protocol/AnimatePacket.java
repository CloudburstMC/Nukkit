package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AnimatePacket extends DataPacket {

    public static final byte NETWORK_ID = Info.ANIMATE_PACKET;

    public long entityId;
    public int action;

    @Override
    public void decode() {
        action = getByte();
        entityId = getLong();
    }

    @Override
    public void encode() {
        reset();
        putByte(action);
        putLong(entityId);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

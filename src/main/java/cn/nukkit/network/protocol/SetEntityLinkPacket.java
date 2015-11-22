package cn.nukkit.network.protocol;

/**
 * Created on 15-10-22.
 */
public class SetEntityLinkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public long from;
    public long to;
    public byte type;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putLong(from);
        putLong(to);
        putByte(type);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

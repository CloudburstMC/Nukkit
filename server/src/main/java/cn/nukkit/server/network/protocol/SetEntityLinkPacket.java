package cn.nukkit.server.network.protocol;

/**
 * Created on 15-10-22.
 */
public class SetEntityLinkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public long rider;
    public long riding;
    public Type type;
    public byte unknownByte;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.rider);
        this.putEntityUniqueId(this.riding);
        this.putByte((byte) this.type.ordinal());
        this.putByte(this.unknownByte);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum Type {
        REMOVE,
        RIDE,
        PASSENGER
    }
}

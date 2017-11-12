package cn.nukkit.network.protocol;

/**
 * Created on 15-10-22.
 */
public class SetEntityLinkPacket extends DataPacket {

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDE = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long rider;
    public long riding;
    public byte type;
    public byte unknownByte;

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityUniqueId(this.rider);
        this.putEntityUniqueId(this.riding);
        this.putByte(this.type);
        this.putByte(this.unknownByte);
    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.SET_ENTITY_LINK_PACKET :
                ProtocolInfo.SET_ENTITY_LINK_PACKET;
    }
}

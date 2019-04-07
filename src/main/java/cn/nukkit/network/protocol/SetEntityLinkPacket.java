package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-22.
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDE = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public byte type;
    public byte immediate;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.vehicleUniqueId);
        this.putEntityUniqueId(this.riderUniqueId);
        this.putByte(this.type);
        this.putByte(this.immediate);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}

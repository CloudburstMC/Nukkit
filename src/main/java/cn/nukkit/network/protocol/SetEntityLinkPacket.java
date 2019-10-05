package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-22.
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDE = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long vehicleUniqueId; //from
    public long riderUniqueId; //to
    public byte type;
    public byte immediate;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.vehicleUniqueId);
        Binary.writeEntityUniqueId(buffer, this.riderUniqueId);
        buffer.writeByte(this.type);
        buffer.writeByte(this.immediate);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}

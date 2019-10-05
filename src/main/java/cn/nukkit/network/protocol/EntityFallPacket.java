package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class EntityFallPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.ENTITY_FALL_PACKET;

    public long eid;
    public float fallDistance;
    public boolean unknown;

    @Override
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        this.fallDistance = buffer.readFloatLE();
        this.unknown = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}

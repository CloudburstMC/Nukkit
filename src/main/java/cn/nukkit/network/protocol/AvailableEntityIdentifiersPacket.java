package cn.nukkit.network.protocol;

import cn.nukkit.registry.EntityRegistry;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString(exclude = {"tag"})
public class AvailableEntityIdentifiersPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;

    public byte[] tag = EntityRegistry.get().getCachedEntityIdentifiers();

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.tag = new byte[buffer.readableBytes()];
        buffer.readBytes(tag);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBytes(this.tag);
    }
}

package cn.nukkit.network.protocol;

import cn.nukkit.registry.BlockRegistry;
import io.netty.buffer.ByteBuf;

public class UpdateBlockPropertiesPacket extends DataPacket {
    private byte[] blockProperties = BlockRegistry.get().getCachedProperties();

    @Override
    public short pid() {
        return ProtocolInfo.UPDATE_BLOCK_PROPERTIES_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBytes(this.blockProperties);
    }
}

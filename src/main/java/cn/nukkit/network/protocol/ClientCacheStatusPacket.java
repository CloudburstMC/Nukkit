package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;

public class ClientCacheStatusPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.CLIENT_CACHE_STATUS_PACKET;

    public boolean supported;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.supported = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.supported);
    }
}

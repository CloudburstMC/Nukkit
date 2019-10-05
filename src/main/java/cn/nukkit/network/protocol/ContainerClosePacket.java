package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ContainerClosePacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public int windowId;

    @Override
    protected void decode(ByteBuf buffer) {
        this.windowId = buffer.readUnsignedByte();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(this.windowId);
    }
}

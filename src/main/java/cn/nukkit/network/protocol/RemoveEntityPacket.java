package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class RemoveEntityPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.REMOVE_ENTITY_PACKET;

    public long entityUniqueId;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.entityUniqueId = Binary.readEntityUniqueId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
    }
}

package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetEntityDataPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public long eid;
    public EntityMetadata metadata;

    @Override
    public void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        this.metadata = Binary.readMetadata(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        Binary.writeMetadata(buffer, this.metadata);
    }
}

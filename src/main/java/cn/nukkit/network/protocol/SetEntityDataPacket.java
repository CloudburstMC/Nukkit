package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityDataMap;
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

    public final EntityDataMap dataMap = new EntityDataMap();
    public long entityRuntimeId;

    @Override
    public void decode(ByteBuf buffer) {
        this.entityRuntimeId = Binary.readEntityRuntimeId(buffer);
        this.dataMap.clear().putAll(Binary.readEntityData(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeEntityData(buffer, this.dataMap);
    }
}

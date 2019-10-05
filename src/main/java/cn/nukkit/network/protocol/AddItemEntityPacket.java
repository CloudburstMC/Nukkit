package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddItemEntityPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.ADD_ITEM_ENTITY_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public Item item;
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public EntityMetadata metadata = new EntityMetadata();
    public boolean isFromFishing = false;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeItem(buffer, this.item);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVector3f(buffer, this.speedX, this.speedY, this.speedZ);
        Binary.writeMetadata(buffer, metadata);
        buffer.writeBoolean(this.isFromFishing);
    }
}

package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddItemEntityPacket extends DataPacket {

    public long entityUniqueId;
    public long entityRuntimeId;
    public Item item;
    public Vector3f position;
    public Vector3f motion;
    public EntityMetadata entityMetadata = new EntityMetadata();
    public boolean isFromFishing = false;

    @Override
    public byte pid() {
        return ProtocolInfo.ADD_ITEM_ENTITY_PACKET;
    }

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.item = this.getSlot();
        this.position = this.getVector3f();
        this.motion = this.getVector3f();
        this.entityMetadata = this.getEntityMetadata();
        this.isFromFishing = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putSlot(this.item);
        this.putVector3f(this.position);
        this.putVector3f(this.motion);
        this.putEntityMetadata(this.entityMetadata);
        this.putBoolean(this.isFromFishing);
    }
}

package cn.nukkit.network.protocol;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AddItemEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ITEM_ENTITY_PACKET;

    @Override
    public byte pid() {
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
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.item = this.getSlot();
        this.x = this.getLFloat();
        this.y = this.getLFloat();
        this.z = this.getLFloat();
        this.speedX = this.getLFloat();
        this.speedY = this.getLFloat();
        this.speedZ = this.getLFloat();
        this.metadata = this.getMetadata();
        this.isFromFishing = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putSlot(this.item);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putMetadata(this.metadata);
        this.putBoolean(this.isFromFishing);
    }
}

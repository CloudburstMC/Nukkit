package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;

/**
 * @author PikyCZ
 */
public class EntitySkeleton extends EntityMob {

    public static final int NETWORK_ID = 34;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySkeleton(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.99f;
    }

    @Override
    public String getName() {
        return "Skeleton";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.BONE, Item.ARROW)};
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        MobEquipmentPacket pk1 = new MobEquipmentPacket();
        pk1.eid = this.getId();
        pk1.item = new ItemBow();
        pk1.hotbarSlot = 10;
        player.dataPacket(pk1);

        super.spawnTo(player);
    }

}

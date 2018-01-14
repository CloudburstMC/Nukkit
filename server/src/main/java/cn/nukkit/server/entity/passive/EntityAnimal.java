package cn.nukkit.server.entity.passive;

import cn.nukkit.server.Player;
import cn.nukkit.server.entity.EntityLiving;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityAnimal extends EntityLiving implements EntityAgeable {
    public EntityAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

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

        super.spawnTo(player);
    }

    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.WHEAT; //default
    }
}

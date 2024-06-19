package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

public class EntityEnderEye extends EntityProjectile {

    public static final int NETWORK_ID = 70;

    public EntityEnderEye(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityEnderEye(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate = super.onUpdate(currentTick);

        if (!this.isAlive()) return hasUpdate;

        if (this.age >= 60) {
            if (ThreadLocalRandom.current().nextFloat() > 0.2) {
                this.level.dropItem(this, Item.get(Item.ENDER_EYE));
            }

            this.close();

            hasUpdate = true;
        } else if (this.age == 40) {
            this.setMotion(new Vector3(0, 0.2, 0));

            hasUpdate = true;
        }

        return hasUpdate;
    }
}

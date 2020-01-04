package cn.nukkit.entity.passive;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.BABY;

public class DeprecatedVillager extends Creature<DeprecatedVillager> implements EntityNPC, EntityAgeable {

    public DeprecatedVillager(EntityType<DeprecatedVillager> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.9f;
        }
        return 1.8f;
    }

    @Override
    public String getName() {
        return "Villager";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    public boolean isBaby() {
        return this.getFlag(BABY);
    }

    public void setBaby(boolean baby) {
        this.setFlag(BABY, baby);
        this.setScale(baby ? 0.5f : 1);
    }
}

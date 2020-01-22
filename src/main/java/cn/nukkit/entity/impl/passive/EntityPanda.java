package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Panda;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityPanda extends Animal implements Panda {

    public EntityPanda(EntityType<Panda> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getLength() {
        return 1.825f;
    }

    @Override
    public float getWidth() {
        return 1.125f;
    }

    @Override
    public float getHeight() {
        return 1.25f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }
}

package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Cod;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntityCod extends Animal implements Cod {

    public EntityCod(EntityType<Cod> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public String getName() {
        return "Cod";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }
}

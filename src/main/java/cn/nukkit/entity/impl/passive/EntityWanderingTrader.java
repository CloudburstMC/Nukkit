package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.EntityCreature;
import cn.nukkit.entity.passive.WanderingTrader;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityWanderingTrader extends EntityCreature implements WanderingTrader {

    public EntityWanderingTrader(EntityType<WanderingTrader> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public String getName() {
        return "Wandering Trader";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }
}

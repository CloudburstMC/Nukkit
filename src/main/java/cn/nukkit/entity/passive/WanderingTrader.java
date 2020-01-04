package cn.nukkit.entity.passive;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class WanderingTrader extends Creature<WanderingTrader> implements EntityNPC {

    public WanderingTrader(EntityType<WanderingTrader> type, Chunk chunk, CompoundTag nbt) {
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

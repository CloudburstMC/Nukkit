package cn.nukkit.entity.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class CommandBlockMinecart extends Vehicle {

    public CommandBlockMinecart(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean mountEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean dismountEntity(Entity entity) {
        return false;
    }
}

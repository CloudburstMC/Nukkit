package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.vehicle.CommandBlockMinecart;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityCommandBlockMinecart extends EntityVehicle implements CommandBlockMinecart {

    public EntityCommandBlockMinecart(EntityType<CommandBlockMinecart> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public boolean mount(Entity entity) {
        return false;
    }

    @Override
    public boolean dismount(Entity vehicle) {
        return false;
    }
}

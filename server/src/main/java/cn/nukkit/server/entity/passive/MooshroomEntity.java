package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Mooshroom;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class MooshroomEntity extends LivingEntity implements Mooshroom {

    public MooshroomEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MOOSHROOM, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF)};
    }*/
}
package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Ocelot;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class OcelotEntity extends LivingEntity implements Ocelot {

    public OcelotEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.OCELOT, position, level, server, 8);
    }
/*
    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.RAW_FISH;
    }*/
}

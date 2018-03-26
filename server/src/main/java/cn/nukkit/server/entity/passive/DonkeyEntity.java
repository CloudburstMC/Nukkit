package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Donkey;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class DonkeyEntity extends LivingEntity implements Donkey {

    public DonkeyEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.DONKEY, position, level, server, 15);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER)};
    }*/
}

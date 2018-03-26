package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.PolarBear;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class PolarBearEntity extends LivingEntity implements PolarBear {

    public static final int NETWORK_ID = 28;

    public PolarBearEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.POLAR_BEAR, position, level, server, 30);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH), Item.get(Item.RAW_SALMON)};
    }*/
}

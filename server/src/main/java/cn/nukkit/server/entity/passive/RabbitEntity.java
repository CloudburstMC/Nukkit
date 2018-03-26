package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Rabbit;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class RabbitEntity extends LivingEntity implements Rabbit {

    public static final int NETWORK_ID = 18;

    public RabbitEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.RABBIT, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_RABBIT), Item.get(Item.RABBIT_HIDE), Item.get(Item.RABBIT_FOOT)};
    }*/
}

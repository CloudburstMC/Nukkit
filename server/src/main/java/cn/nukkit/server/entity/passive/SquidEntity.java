package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Squid;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class SquidEntity extends LivingEntity implements Squid {

    public SquidEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.SQUID, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{new ItemDye(DyeColor.BLACK.getDyeData())};
    }*/
}

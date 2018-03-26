package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Horse;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class HorseEntity extends LivingEntity implements Horse {

    public HorseEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.HORSE, position, level, server, 15);
    }
    /*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER)};
    }*/
}

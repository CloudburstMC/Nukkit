package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Pig;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class PigEntity extends LivingEntity implements Pig {

    public PigEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.PIG, position, level, server, 10);
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_PORKCHOP)};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.CARROT || id == Item.POTATO || id == Item.BEETROOT;
    }*/
}

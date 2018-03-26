package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.component.Ageable;
import cn.nukkit.api.entity.passive.Chicken;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.entity.component.AgeableComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class ChickenEntity extends LivingEntity implements Chicken {

    public ChickenEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.CHICKEN, position, level, server, 4);

        registerComponent(Ageable.class, new AgeableComponent(24000));
    }
/*
    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_CHICKEN), Item.get(Item.FEATHER)};
    }

    @Override
    public boolean isBreedingItem(Item item) {
        int id = item.getId();

        return id == Item.WHEAT_SEEDS || id == Item.MELON_SEEDS || id == Item.PUMPKIN_SEEDS || id == Item.BEETROOT_SEEDS;
    }*/
}

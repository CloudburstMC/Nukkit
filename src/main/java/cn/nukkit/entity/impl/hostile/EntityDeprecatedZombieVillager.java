package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.DeprecatedZombieVillager;
import cn.nukkit.level.Location;

public class EntityDeprecatedZombieVillager extends EntityHostile implements DeprecatedZombieVillager, Smiteable {

    public EntityDeprecatedZombieVillager(EntityType<DeprecatedZombieVillager> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Zombie Villager";
    }
}

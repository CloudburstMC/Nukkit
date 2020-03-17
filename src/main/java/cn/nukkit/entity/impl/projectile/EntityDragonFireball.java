package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.DragonFireball;
import cn.nukkit.level.Location;

public class EntityDragonFireball extends EntityProjectile implements DragonFireball {
    public EntityDragonFireball(EntityType<?> type, Location location) {
        super(type, location);
    }
}

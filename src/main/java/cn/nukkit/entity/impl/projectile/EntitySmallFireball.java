package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.SmallFireball;
import cn.nukkit.level.Location;

public class EntitySmallFireball extends EntityProjectile implements SmallFireball {
    public EntitySmallFireball(EntityType<?> type, Location location) {
        super(type, location);
    }
}

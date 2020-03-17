package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.ShulkerBullet;
import cn.nukkit.level.Location;

public class EntityShulkerBullet extends EntityProjectile implements ShulkerBullet {
    public EntityShulkerBullet(EntityType<?> type, Location location) {
        super(type, location);
    }
}

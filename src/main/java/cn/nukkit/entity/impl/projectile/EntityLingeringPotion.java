package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.LingeringPotion;
import cn.nukkit.level.Location;

public class EntityLingeringPotion extends EntityProjectile implements LingeringPotion {
    public EntityLingeringPotion(EntityType<?> type, Location location) {
        super(type, location);
    }
}

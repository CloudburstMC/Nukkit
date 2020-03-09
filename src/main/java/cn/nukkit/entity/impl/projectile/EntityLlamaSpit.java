package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.LlamaSpit;
import cn.nukkit.level.Location;

public class EntityLlamaSpit extends EntityProjectile implements LlamaSpit {
    public EntityLlamaSpit(EntityType<?> type, Location location) {
        super(type, location);
    }
}

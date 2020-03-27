package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.projectile.EyeOfEnderSignal;
import cn.nukkit.level.Location;

public class EntityEyeOfEnderSignal extends EntityProjectile implements EyeOfEnderSignal {

    public EntityEyeOfEnderSignal(EntityType<?> type, Location location) {
        super(type, location);
    }
}

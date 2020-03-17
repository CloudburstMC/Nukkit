package cn.nukkit.entity.impl;

import cn.nukkit.entity.EntityType;
import cn.nukkit.level.Location;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {

    public EntityCreature(EntityType<?> type, Location location) {
        super(type, location);
    }
}

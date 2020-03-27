package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.LeashKnot;
import cn.nukkit.level.Location;

public class EntityLeashKnot extends BaseEntity implements LeashKnot {
    public EntityLeashKnot(EntityType<?> type, Location location) {
        super(type, location);
    }
}

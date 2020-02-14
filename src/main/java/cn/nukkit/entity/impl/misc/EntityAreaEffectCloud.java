package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.level.Location;

public class EntityAreaEffectCloud extends BaseEntity implements AreaEffectCloud {
    public EntityAreaEffectCloud(EntityType<?> type, Location location) {
        super(type, location);
    }
}

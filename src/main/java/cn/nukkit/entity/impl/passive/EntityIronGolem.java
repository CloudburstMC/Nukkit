package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.passive.IronGolem;
import cn.nukkit.level.Location;

public class EntityIronGolem extends BaseEntity implements IronGolem {

    public EntityIronGolem(EntityType<?> type, Location location) {
        super(type, location);
    }
}

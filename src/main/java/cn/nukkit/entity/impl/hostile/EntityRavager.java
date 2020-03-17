package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.Ravager;
import cn.nukkit.level.Location;

public class EntityRavager extends EntityHostile implements Ravager {

    public EntityRavager(EntityType<Ravager> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(100);
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public float getWidth() {
        return 1.2f;
    }

    @Override
    public String getName() {
        return "Ravager";
    }
}

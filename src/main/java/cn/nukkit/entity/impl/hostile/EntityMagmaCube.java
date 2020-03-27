package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.MagmaCube;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityMagmaCube extends EntityHostile implements MagmaCube {

    public EntityMagmaCube(EntityType<MagmaCube> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @Override
    public String getName() {
        return "Magma Cube";
    }
}

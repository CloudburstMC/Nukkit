package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Wither;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityWither extends EntityHostile implements Wither, Smiteable {

    public EntityWither(EntityType<Wither> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 3.5f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(300);
    }

    @Override
    public String getName() {
        return "Wither";
    }
}

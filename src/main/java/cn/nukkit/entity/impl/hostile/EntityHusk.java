package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.Husk;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityHusk extends EntityHostile implements Husk, Smiteable {

    public EntityHusk(EntityType<Husk> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public String getName() {
        return "Husk";
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}

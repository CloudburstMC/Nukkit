package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Bat;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityBat extends Animal implements Bat {

    public EntityBat(EntityType<Bat> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(6);
    }
}

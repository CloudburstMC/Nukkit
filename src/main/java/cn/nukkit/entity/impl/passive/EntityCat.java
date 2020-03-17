package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Cat;
import cn.nukkit.level.Location;

public class EntityCat extends Animal implements Cat {

    public EntityCat(EntityType<Cat> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.35f;
        }
        return 0.7f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }
}

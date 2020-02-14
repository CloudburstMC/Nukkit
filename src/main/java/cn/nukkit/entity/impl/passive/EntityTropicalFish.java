package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.TropicalFish;
import cn.nukkit.level.Location;

/**
 * Created by PetteriM1
 */
public class EntityTropicalFish extends Animal implements TropicalFish {

    public EntityTropicalFish(EntityType<TropicalFish> type, Location location) {
        super(type, location);
    }

    public String getName() {
        return "Tropical Fish";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }
}

package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Pufferfish;
import cn.nukkit.level.Location;

/**
 * Created by PetteriM1
 */
public class EntityPufferfish extends Animal implements Pufferfish {

    public EntityPufferfish(EntityType<Pufferfish> type, Location location) {
        super(type, location);
    }

    public String getName() {
        return "Pufferfish";
    }

    @Override
    public float getWidth() {
        return 0.35f;
    }

    @Override
    public float getHeight() {
        return 0.35f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }
}

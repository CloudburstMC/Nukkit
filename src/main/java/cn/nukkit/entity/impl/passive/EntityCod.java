package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Cod;
import cn.nukkit.level.Location;

/**
 * Created by PetteriM1
 */
public class EntityCod extends Animal implements Cod {

    public EntityCod(EntityType<Cod> type, Location location) {
        super(type, location);
    }

    public String getName() {
        return "Cod";
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }
}

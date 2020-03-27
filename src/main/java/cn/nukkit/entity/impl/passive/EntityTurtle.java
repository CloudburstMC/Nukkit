package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Turtle;
import cn.nukkit.level.Location;

/**
 * Created by PetteriM1
 */
public class EntityTurtle extends Animal implements Turtle {

    public EntityTurtle(EntityType<Turtle> type, Location location) {
        super(type, location);
    }

    public String getName() {
        return "Turtle";
    }

    @Override
    public float getWidth() {
        return 1.2f;
    }

    @Override
    public float getHeight() {
        return 0.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
    }
}

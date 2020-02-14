package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.hostile.EnderDragon;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityEnderDragon extends EntityHostile implements EnderDragon {

    public EntityEnderDragon(EntityType<EnderDragon> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        return 13f;
    }

    @Override
    public float getHeight() {
        return 4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(200);
    }

    @Override
    public String getName() {
        return "EnderDragon";
    }
}

package cn.nukkit.entity.impl.hostile;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Smiteable;
import cn.nukkit.entity.hostile.WitherSkeleton;
import cn.nukkit.level.Location;

/**
 * @author PikyCZ
 */
public class EntityWitherSkeleton extends EntityHostile implements WitherSkeleton, Smiteable {

    public EntityWitherSkeleton(EntityType<WitherSkeleton> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public String getName() {
        return "WitherSkeleton";
    }

    @Override
    public boolean isUndead() {
        return true;
    }

}

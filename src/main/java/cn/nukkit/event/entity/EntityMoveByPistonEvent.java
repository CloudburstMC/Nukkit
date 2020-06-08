package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.math.Vector3;

public class EntityMoveByPistonEvent extends EntityMotionEvent implements Cancellable {
    public EntityMoveByPistonEvent(Entity entity, Vector3 pos) {
        super(entity, pos);
    }
}

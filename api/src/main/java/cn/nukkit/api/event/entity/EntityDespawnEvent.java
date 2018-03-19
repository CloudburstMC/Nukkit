package cn.nukkit.api.event.entity;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Projectile;
import cn.nukkit.api.entity.component.Rideable;
import cn.nukkit.api.entity.misc.DroppedItem;
import com.flowpowered.math.vector.Vector3f;

public class EntityDespawnEvent implements EntityEvent {
    private final Entity entity;

    public EntityDespawnEvent(Entity entity) {
        this.entity = entity;
    }

    public Vector3f getPosition() {
        return this.entity.getPosition();
    }

    public boolean isCreature() {
        return this.entity.get(Damageable.class).isPresent();
    }

    public boolean isHuman() {
        return this.entity instanceof Player;
    }

    public boolean isProjectile() {
        return this.entity.get(Projectile.class).isPresent();
    }

    public boolean isVehicle() {
        return this.entity.get(Rideable.class).isPresent();
    }

    public boolean isItem() {
        return this.entity instanceof DroppedItem;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}

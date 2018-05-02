package com.nukkitx.api.event.entity;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.Player;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Damageable;
import com.nukkitx.api.entity.component.Projectile;
import com.nukkitx.api.entity.component.Rideable;
import com.nukkitx.api.entity.misc.DroppedItem;

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

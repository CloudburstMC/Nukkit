package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByChildEntityEvent extends EntityDamageByEntityEvent {

    private final Entity childEntity;

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, float damage) {
        this(damager, childEntity, entity, cause, damage, 0.3f);
    }

    public EntityDamageByChildEntityEvent(Entity damager, Entity childEntity, Entity entity, DamageCause cause, float damage, float knockBack) {
        super(damager, entity, cause, damage, knockBack);
        this.childEntity = childEntity;
    }

    public Entity getChild() {
        return childEntity;
    }
}

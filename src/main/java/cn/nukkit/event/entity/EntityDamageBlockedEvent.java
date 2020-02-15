package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Setter;
import lombok.ToString;

@Setter @ToString
public class EntityDamageBlockedEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityDamageEvent damage;
    private int disableTicks;
    private boolean knockBackAttacker;
    private boolean animation;

    public EntityDamageBlockedEvent(
            Entity entity, EntityDamageEvent damage, int disableTicks, boolean knockBack, boolean animation) {
        this.entity = entity;
        this.damage = damage;
        this.knockBackAttacker = knockBack;
        this.animation = animation;
        this.disableTicks = disableTicks;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return damage.getCause();
    }

    public Entity getAttacker() {
        return damage.getEntity();
    }

    public EntityDamageEvent getDamage() {
        return damage;
    }

    public int getDisableTicks() {
        return disableTicks;
    }

    public boolean getKnockBackAttacker() {
        return knockBackAttacker;
    }

    public boolean getAnimation() {
        return animation;
    }
}

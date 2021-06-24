package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class EntityDamageBlockedEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityDamageEvent damage;
    private boolean knockBackAttacker;
    private boolean animation;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityDamageBlockedEvent(Entity entity, EntityDamageEvent damage, boolean knockBack, boolean animation) {
        this.entity = entity;
        this.damage = damage;
        this.knockBackAttacker = knockBack;
        this.animation = animation;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityDamageEvent.DamageCause getCause() {
        return damage.getCause();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Entity getAttacker() {
        return damage.getEntity();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityDamageEvent getDamage() {
        return damage;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getKnockBackAttacker() {
        return knockBackAttacker;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getAnimation() {
        return animation;
    }
}

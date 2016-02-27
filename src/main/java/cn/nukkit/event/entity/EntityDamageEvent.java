package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.EventException;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static final int MODIFIER_BASE = 0;
    public static final int MODIFIER_ARMOR = 1;
    public static final int MODIFIER_STRENGTH = 2;
    public static final int MODIFIER_WEAKNESS = 3;
    public static final int MODIFIER_RESISTANCE = 4;

    public static final int CAUSE_CONTACT = 0;
    public static final int CAUSE_ENTITY_ATTACK = 1;
    public static final int CAUSE_PROJECTILE = 2;
    public static final int CAUSE_SUFFOCATION = 3;
    public static final int CAUSE_FALL = 4;
    public static final int CAUSE_FIRE = 5;
    public static final int CAUSE_FIRE_TICK = 6;
    public static final int CAUSE_LAVA = 7;
    public static final int CAUSE_DROWNING = 8;
    public static final int CAUSE_BLOCK_EXPLOSION = 9;
    public static final int CAUSE_ENTITY_EXPLOSION = 10;
    public static final int CAUSE_VOID = 11;
    public static final int CAUSE_SUICIDE = 12;
    public static final int CAUSE_MAGIC = 13;
    public static final int CAUSE_CUSTOM = 14;
    public static final int CAUSE_LIGHTNING = 15;

    private int cause;

    private Map<Integer, Float> modifiers;
    private Map<Integer, Float> originals;

    public EntityDamageEvent(Entity entity, int cause, float damage) {
        this(entity, cause, new HashMap<Integer, Float>() {
            {
                put(MODIFIER_BASE, damage);
            }
        });
    }

    public EntityDamageEvent(Entity entity, int cause, Map<Integer, Float> modifiers) {
        this.entity = entity;
        this.cause = cause;
        this.modifiers = modifiers;

        this.originals = this.modifiers;

        if (!this.modifiers.containsKey(MODIFIER_BASE)) {
            throw new EventException("BASE Damage modifier missing");
        }

        if (entity.hasEffect(Effect.DAMAGE_RESISTANCE)) {
            this.setDamage((float) -(this.getDamage(MODIFIER_BASE) * 0.20 * (entity.getEffect(Effect.DAMAGE_RESISTANCE).getAmplifier() + 1)), MODIFIER_RESISTANCE);
        }
    }

    public int getCause() {
        return cause;
    }

    public float getOriginalDamage() {
        return this.getOriginalDamage(MODIFIER_BASE);
    }

    public float getOriginalDamage(int type) {
        if (this.originals.containsKey(type)) {
            return this.originals.get(type);
        }

        return 0;
    }

    public float getDamage() {
        return this.getDamage(MODIFIER_BASE);
    }

    public float getDamage(int type) {
        if (this.modifiers.containsKey(type)) {
            return this.modifiers.get(type);
        }

        return 0;
    }

    public void setDamage(float damage) {
        this.setDamage(damage, MODIFIER_BASE);
    }

    public void setDamage(float damage, int type) {
        this.modifiers.put(type, damage);
    }

    public boolean isApplicable(int type) {
        return this.modifiers.containsKey(type);
    }

    public float getFinalDamage() {
        float damage = 0;
        for (Float d : this.modifiers.values()) {
            if (d != null) {
                damage += d;
            }
        }

        return damage;
    }


}

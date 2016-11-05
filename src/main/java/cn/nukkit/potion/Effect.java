package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.network.protocol.MobEffectPacket;
import cn.nukkit.utils.ServerException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Effect implements Cloneable {

    public static final int SPEED = 1;
    public static final int SLOWNESS = 2;
    public static final int HASTE = 3;
    public static final int SWIFTNESS = 3;
    public static final int FATIGUE = 4;
    public static final int MINING_FATIGUE = 4;
    public static final int STRENGTH = 5;
    public static final int HEALING = 6;
    public static final int HARMING = 7;
    public static final int JUMP = 8;
    public static final int NAUSEA = 9;
    public static final int CONFUSION = 9;
    public static final int REGENERATION = 10;
    public static final int DAMAGE_RESISTANCE = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int WATER_BREATHING = 13;
    public static final int INVISIBILITY = 14;
    public static final int BLINDNESS = 15;
    public static final int NIGHT_VISION = 16;
    public static final int HUNGER = 17;
    public static final int WEAKNESS = 18;
    public static final int POISON = 19;
    public static final int WITHER = 20;
    public static final int HEALTH_BOOST = 21;
    public static final int ABSORPTION = 22;
    public static final int SATURATION = 23;

    protected static Effect[] effects;

    public static void init() {
        effects = new Effect[256];

        effects[Effect.SPEED] = new Effect(Effect.SPEED, "%potion.moveSpeed", 124, 175, 198);
        effects[Effect.SLOWNESS] = new Effect(Effect.SLOWNESS, "%potion.moveSlowdown", 90, 108, 129, true);
        effects[Effect.SWIFTNESS] = new Effect(Effect.SWIFTNESS, "%potion.digSpeed", 217, 192, 67);
        effects[Effect.FATIGUE] = new Effect(Effect.FATIGUE, "%potion.digSlowDown", 74, 66, 23, true);
        effects[Effect.STRENGTH] = new Effect(Effect.STRENGTH, "%potion.damageBoost", 147, 36, 35);
        effects[Effect.HEALING] = new InstantEffect(Effect.HEALING, "%potion.heal", 248, 36, 35);
        effects[Effect.HARMING] = new InstantEffect(Effect.HARMING, "%potion.harm", 67, 10, 9, true);
        effects[Effect.JUMP] = new Effect(Effect.JUMP, "%potion.jump", 34, 255, 76);
        effects[Effect.NAUSEA] = new Effect(Effect.NAUSEA, "%potion.confusion", 85, 29, 74, true);
        effects[Effect.REGENERATION] = new Effect(Effect.REGENERATION, "%potion.regeneration", 205, 92, 171);
        effects[Effect.DAMAGE_RESISTANCE] = new Effect(Effect.DAMAGE_RESISTANCE, "%potion.resistance", 153, 69, 58);
        effects[Effect.FIRE_RESISTANCE] = new Effect(Effect.FIRE_RESISTANCE, "%potion.fireResistance", 228, 154, 58);
        effects[Effect.WATER_BREATHING] = new Effect(Effect.WATER_BREATHING, "%potion.waterBreathing", 46, 82, 153);
        effects[Effect.INVISIBILITY] = new Effect(Effect.INVISIBILITY, "%potion.invisibility", 127, 131, 146);

        effects[Effect.BLINDNESS] = new Effect(Effect.BLINDNESS, "%potion.blindness", 191, 192, 192);
        effects[Effect.NIGHT_VISION] = new Effect(Effect.NIGHT_VISION, "%potion.nightVision", 0, 0, 139);
        effects[Effect.HUNGER] = new Effect(Effect.HUNGER, "%potion.hunger", 46, 139, 87);

        effects[Effect.WEAKNESS] = new Effect(Effect.WEAKNESS, "%potion.weakness", 72, 77, 72, true);
        effects[Effect.POISON] = new Effect(Effect.POISON, "%potion.poison", 78, 147, 49, true);
        effects[Effect.WITHER] = new Effect(Effect.WITHER, "%potion.wither", 53, 42, 39, true);
        effects[Effect.HEALTH_BOOST] = new Effect(Effect.HEALTH_BOOST, "%potion.healthBoost", 248, 125, 35);

        effects[Effect.ABSORPTION] = new Effect(Effect.ABSORPTION, "%potion.absorption", 36, 107, 251);
        effects[Effect.SATURATION] = new Effect(Effect.SATURATION, "%potion.saturation", 255, 0, 255);
    }

    public static Effect getEffect(int id) {
        if (id >= 0 && id < effects.length && effects[id] != null) {
            return effects[id].clone();
        } else {
            throw new ServerException("Effect id: " + id + " not found");
        }
    }

    public static Effect getEffectByName(String name) {
        name = name.trim().replace(' ', '_').replace("minecraft:", "");
        try {
            int id = Effect.class.getField(name.toUpperCase()).getInt(null);
            return getEffect(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final int id;

    protected final String name;

    protected int duration;

    protected int amplifier;

    protected int color;

    protected boolean show = true;

    protected boolean ambient = false;

    protected final boolean bad;

    public Effect(int id, String name, int r, int g, int b) {
        this(id, name, r, g, b, false);
    }

    public Effect(int id, String name, int r, int g, int b, boolean isBad) {
        this.id = id;
        this.name = name;
        this.bad = isBad;
        this.setColor(r, g, b);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Effect setDuration(int ticks) {
        this.duration = ticks;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isVisible() {
        return show;
    }

    public Effect setVisible(boolean visible) {
        this.show = visible;
        return this;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public Effect setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public Effect setAmbient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public boolean isBad() {
        return bad;
    }

    public boolean canTick() {
        int interval;
        switch (this.id) {
            case Effect.POISON: //POISON
                if ((interval = (25 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
            case Effect.WITHER: //WITHER
                if ((interval = (50 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
            case Effect.REGENERATION: //REGENERATION
                if ((interval = (40 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == 0;
                }
                return true;
            case Effect.SPEED:
            case Effect.SLOWNESS:
                return (this.duration % 20) == 0;
        }
        return false;
    }

    public void applyEffect(Entity entity) {
        EntityEvent ev;
        switch (this.id) {
            case Effect.POISON: //POISON
                if (entity.getHealth() > 1) {
                    ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 1);
                    entity.attack((EntityDamageEvent) ev);
                }
                break;
            case Effect.WITHER: //WITHER
                ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 1);
                entity.attack((EntityDamageEvent) ev);
                break;
            case Effect.REGENERATION: //REGENERATION
                if (entity.getHealth() < entity.getMaxHealth()) {
                    ev = new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC);
                    entity.heal((EntityRegainHealthEvent) ev);
                }
                break;
            case Effect.SPEED:
                if (entity instanceof Player) {
                    ((Player) entity).setMovementSpeed((float) (((this.amplifier + 1) * 0.2 + 1) * 0.1));
                }
                break;
            case Effect.SLOWNESS:
                if (entity instanceof Player) {
                    ((Player) entity).setMovementSpeed((float) (((this.amplifier + 1) * -0.15 + 1) * 0.1));
                }
                break;
        }
    }

    public int[] getColor() {
        return new int[]{this.color >> 16, (this.color >> 8) & 0xff, this.color & 0xff};
    }

    public void setColor(int r, int g, int b) {
        this.color = ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
    }

    public void add(Entity entity) {
        this.add(entity, false);
    }

    public void add(Entity entity, boolean modify) {
        if (entity instanceof Player) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = 0;
            pk.effectId = this.getId();
            pk.amplifier = this.getAmplifier();
            pk.particles = this.isVisible();
            pk.duration = this.getDuration();
            if (modify) {
                pk.eventId = MobEffectPacket.EVENT_MODIFY;
            } else {
                pk.eventId = MobEffectPacket.EVENT_ADD;
            }

            ((Player) entity).dataPacket(pk);

            if (this.id == Effect.SPEED) {
                ((Player) entity).setMovementSpeed((float) (((this.amplifier + 1) * 0.2 + 1) * 0.1));
            }

            if (this.id == Effect.SLOWNESS) {
                ((Player) entity).setMovementSpeed((float) (((this.amplifier + 1) * -0.15 + 1) * 0.1));
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, true);
            entity.setNameTagVisible(false);
        }
    }

    public void remove(Entity entity) {
        if (entity instanceof Player) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = 0;
            pk.effectId = this.getId();
            pk.eventId = MobEffectPacket.EVENT_REMOVE;

            ((Player) entity).dataPacket(pk);

            if (this.id == Effect.SPEED || this.id == Effect.SLOWNESS) {
                ((Player) entity).setMovementSpeed(0.1f);
            }
        }

        if (this.id == Effect.INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, false);
            entity.setNameTagVisible(true);
        }
    }

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

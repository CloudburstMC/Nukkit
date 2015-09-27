package cn.nukkit.entity;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Effect implements Cloneable {

    protected static Effect[] effects;

    public static void init() {
        effects = new Effect[256];

        effects[EffectInfo.SPEED.getId()] = new Effect(EffectInfo.SPEED.getId(), "%potion.moveSpeed", 124, 175, 198);
        effects[EffectInfo.SLOWNESS.getId()] = new Effect(EffectInfo.SLOWNESS.getId(), "%potion.moveSlowdown", 90, 108, 129, true);
        effects[EffectInfo.SWIFTNESS.getId()] = new Effect(EffectInfo.SWIFTNESS.getId(), "%potion.digSpeed", 217, 192, 67);
        effects[EffectInfo.FATIGUE.getId()] = new Effect(EffectInfo.FATIGUE.getId(), "%potion.digSlowDown", 74, 66, 23, true);
        effects[EffectInfo.STRENGTH.getId()] = new Effect(EffectInfo.STRENGTH.getId(), "%potion.damageBoost", 147, 36, 35);
        //effects[EffectInfo.HEALING.getId()] = new InstantEffect(EffectInfo.HEALING.getId(), "%potion.heal", 248, 36, 35);
        //effects[EffectInfo.HARMING.getId()] = new InstantEffect(EffectInfo.HARMING.getId(), "%potion.harm", 67, 10, 9, true);
        effects[EffectInfo.JUMP.getId()] = new Effect(EffectInfo.JUMP.getId(), "%potion.jump", 34, 255, 76);
        effects[EffectInfo.NAUSEA.getId()] = new Effect(EffectInfo.NAUSEA.getId(), "%potion.confusion", 85, 29, 74, true);
        effects[EffectInfo.REGENERATION.getId()] = new Effect(EffectInfo.REGENERATION.getId(), "%potion.regeneration", 205, 92, 171);
        effects[EffectInfo.DAMAGE_RESISTANCE.getId()] = new Effect(EffectInfo.DAMAGE_RESISTANCE.getId(), "%potion.resistance", 153, 69, 58);
        effects[EffectInfo.FIRE_RESISTANCE.getId()] = new Effect(EffectInfo.FIRE_RESISTANCE.getId(), "%potion.fireResistance", 228, 154, 58);
        effects[EffectInfo.WATER_BREATHING.getId()] = new Effect(EffectInfo.WATER_BREATHING.getId(), "%potion.waterBreathing", 46, 82, 153);
        effects[EffectInfo.INVISIBILITY.getId()] = new Effect(EffectInfo.INVISIBILITY.getId(), "%potion.invisibility", 127, 131, 146);
        //Hunger
        effects[EffectInfo.WEAKNESS.getId()] = new Effect(EffectInfo.WEAKNESS.getId(), "%potion.weakness", 72, 77, 72, true);
        effects[EffectInfo.POISON.getId()] = new Effect(EffectInfo.POISON.getId(), "%potion.poison", 78, 147, 49, true);
        effects[EffectInfo.WITHER.getId()] = new Effect(EffectInfo.WITHER.getId(), "%potion.wither", 53, 42, 39, true);
        effects[EffectInfo.HEALTH_BOOST.getId()] = new Effect(EffectInfo.HEALTH_BOOST.getId(), "%potion.healthBoost", 248, 125, 35);
        //Absorption
        //Saturation
    }

    public static Effect getEffect(int id) {
        if (id >= 0 && id < 256 && effects[id] != null) {
            return effects[id].clone();
        } else {
            return null;
        }
    }

    public static Effect getEffectByName(String name) {
        try {
            int id = EffectInfo.valueOf(name.toUpperCase()).getId();
            return getEffect(id);
        } catch (Exception e) {
            return null;
        }
    }

    protected int id;

    protected String name;

    protected int duration;

    protected int amplifier;

    protected int color;

    protected boolean show = true;

    protected boolean ambient = false;

    protected boolean bad;

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
            case 19: //POISON
                if ((interval = (25 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == = 0;
                }
                return true;
            case 20: //WITHER
                if ((interval = (50 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == = 0;
                }
                return true;
            case 10: //REGENERATION
                if ((interval = (40 >> this.amplifier)) > 0) {
                    return (this.duration % interval) == = 0;
                }
                return true;
        }
    }

    public void applyEffect(Entity entity) {
        EntityEvent ev;
        switch (this.id) {
            case 19: //POISON
                if (entity.getHealth() > 1) {
                    ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 1);
                    entity.attack(((EntityDamageEvent) ev).getFinalDamage(), ev);
                }
                break;
            case 20: //WITHER
                ev = new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 1);
                entity.attack(((EntityDamageEvent) ev).getFinalDamage(), ev);
                break;
            case 10: //REGENERATION
                if (entity.getHealth() < entity.getMaxHealth()) {
                    ev = new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC);
                    entity.heal(((EntityRegainHealthEvent) ev).getAmount(), ev);
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
        //todo
    }

    public void remove(Entity) {
        //todo
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

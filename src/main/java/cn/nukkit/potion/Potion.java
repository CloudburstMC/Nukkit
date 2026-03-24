package cn.nukkit.potion;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.entity.mob.EntitySnowGolem;
import cn.nukkit.entity.passive.EntityStrider;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class Potion implements Cloneable {

    public static final int NO_EFFECTS = 0;
    public static final int WATER = 0;
    public static final int MUNDANE = 1;
    public static final int MUNDANE_II = 2;
    public static final int THICK = 3;
    public static final int AWKWARD = 4;
    public static final int NIGHT_VISION = 5;
    public static final int NIGHT_VISION_LONG = 6;
    public static final int INVISIBLE = 7;
    public static final int INVISIBLE_LONG = 8;
    public static final int LEAPING = 9;
    public static final int LEAPING_LONG = 10;
    public static final int LEAPING_II = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int FIRE_RESISTANCE_LONG = 13;
    public static final int SPEED = 14;
    public static final int SPEED_LONG = 15;
    public static final int SPEED_II = 16;
    public static final int SLOWNESS = 17;
    public static final int SLOWNESS_LONG = 18;
    public static final int WATER_BREATHING = 19;
    public static final int WATER_BREATHING_LONG = 20;
    public static final int INSTANT_HEALTH = 21;
    public static final int INSTANT_HEALTH_II = 22;
    public static final int HARMING = 23;
    public static final int HARMING_II = 24;
    public static final int POISON = 25;
    public static final int POISON_LONG = 26;
    public static final int POISON_II = 27;
    public static final int REGENERATION = 28;
    public static final int REGENERATION_LONG = 29;
    public static final int REGENERATION_II = 30;
    public static final int STRENGTH = 31;
    public static final int STRENGTH_LONG = 32;
    public static final int STRENGTH_II = 33;
    public static final int WEAKNESS = 34;
    public static final int WEAKNESS_LONG = 35;
    public static final int WITHER_II = 36;
    public static final int TURTLE_MASTER = 37;
    public static final int TURTLE_MASTER_LONG = 38;
    public static final int TURTLE_MASTER_II = 39;
    public static final int SLOW_FALLING = 40;
    public static final int SLOW_FALLING_LONG = 41;
    public static final int SLOWNESS_IV = 42;
    public static final int WIND_CHARGED = 43;
    public static final int WEAVING = 44;
    public static final int OOZING = 45;
    public static final int INFESTED = 46;

    protected static Potion[] potions;

    public static void init() {
        potions = new Potion[256];

        potions[Potion.WATER] = new Potion(Potion.WATER);
        potions[Potion.MUNDANE] = new Potion(Potion.MUNDANE);
        potions[Potion.MUNDANE_II] = new Potion(Potion.MUNDANE_II, 2);
        potions[Potion.THICK] = new Potion(Potion.THICK);
        potions[Potion.AWKWARD] = new Potion(Potion.AWKWARD);
        potions[Potion.NIGHT_VISION] = new Potion(Potion.NIGHT_VISION, 1, 180);
        potions[Potion.NIGHT_VISION_LONG] = new Potion(Potion.NIGHT_VISION_LONG, 1, 480);
        potions[Potion.INVISIBLE] = new Potion(Potion.INVISIBLE, 1, 180);
        potions[Potion.INVISIBLE_LONG] = new Potion(Potion.INVISIBLE_LONG, 1, 480);
        potions[Potion.LEAPING] = new Potion(Potion.LEAPING, 1, 180);
        potions[Potion.LEAPING_LONG] = new Potion(Potion.LEAPING_LONG, 1, 480);
        potions[Potion.LEAPING_II] = new Potion(Potion.LEAPING_II, 2, 90);
        potions[Potion.FIRE_RESISTANCE] = new Potion(Potion.FIRE_RESISTANCE, 1, 180);
        potions[Potion.FIRE_RESISTANCE_LONG] = new Potion(Potion.FIRE_RESISTANCE_LONG, 1, 480);
        potions[Potion.SPEED] = new Potion(Potion.SPEED, 1, 180);
        potions[Potion.SPEED_LONG] = new Potion(Potion.SPEED_LONG, 1, 480);
        potions[Potion.SPEED_II] = new Potion(Potion.SPEED_II, 2, 90);
        potions[Potion.SLOWNESS] = new Potion(Potion.SLOWNESS, 1, 90);
        potions[Potion.SLOWNESS_LONG] = new Potion(Potion.SLOWNESS_LONG, 1, 240);
        potions[Potion.WATER_BREATHING] = new Potion(Potion.WATER_BREATHING, 1, 180);
        potions[Potion.WATER_BREATHING_LONG] = new Potion(Potion.WATER_BREATHING_LONG, 1, 480);
        potions[Potion.INSTANT_HEALTH] = new Potion(Potion.INSTANT_HEALTH);
        potions[Potion.INSTANT_HEALTH_II] = new Potion(Potion.INSTANT_HEALTH_II, 2);
        potions[Potion.HARMING] = new Potion(Potion.HARMING);
        potions[Potion.HARMING_II] = new Potion(Potion.HARMING_II, 2);
        potions[Potion.POISON] = new Potion(Potion.POISON, 1, 45);
        potions[Potion.POISON_LONG] = new Potion(Potion.POISON_LONG, 1, 120);
        potions[Potion.POISON_II] = new Potion(Potion.POISON_II, 2, 22);
        potions[Potion.REGENERATION] = new Potion(Potion.REGENERATION, 1, 45);
        potions[Potion.REGENERATION_LONG] = new Potion(Potion.REGENERATION_LONG, 1, 120);
        potions[Potion.REGENERATION_II] = new Potion(Potion.REGENERATION_II, 2, 22);
        potions[Potion.STRENGTH] = new Potion(Potion.STRENGTH, 1, 180);
        potions[Potion.STRENGTH_LONG] = new Potion(Potion.STRENGTH_LONG, 1, 480);
        potions[Potion.STRENGTH_II] = new Potion(Potion.STRENGTH_II, 2, 90);
        potions[Potion.WEAKNESS] = new Potion(Potion.WEAKNESS, 1, 90);
        potions[Potion.WEAKNESS_LONG] = new Potion(Potion.WEAKNESS_LONG, 1, 240);
        potions[Potion.WITHER_II] = new Potion(Potion.WITHER_II, 2, 40);
        potions[Potion.TURTLE_MASTER] = new Potion(Potion.TURTLE_MASTER, 1, 20);
        potions[Potion.TURTLE_MASTER_LONG] = new Potion(Potion.TURTLE_MASTER_LONG, 1, 40);
        potions[Potion.TURTLE_MASTER_II] = new Potion(Potion.TURTLE_MASTER_II, 2, 20);
        potions[Potion.SLOW_FALLING] = new Potion(Potion.SLOW_FALLING, 1, 90);
        potions[Potion.SLOW_FALLING_LONG] = new Potion(Potion.SLOW_FALLING_LONG, 1, 240);
        potions[Potion.SLOWNESS_IV] = new Potion(Potion.SLOWNESS, 4, 20);
        potions[Potion.WIND_CHARGED] = new Potion(Potion.WIND_CHARGED, 1, 180);
        potions[Potion.WEAVING] = new Potion(Potion.WEAVING, 1, 180);
        potions[Potion.OOZING] = new Potion(Potion.OOZING, 1, 180);
        potions[Potion.INFESTED] = new Potion(Potion.INFESTED, 1, 180);
    }

    @Nullable
    public static Potion getPotion(int id) {
        if (id >= 0 && id < potions.length && potions[id] != null) {
            return potions[id].clone();
        } else {
            return null;
        }
    }

    @Nullable
    public static Potion getPotionByName(String name) {
        try {
            byte id = Potion.class.getField(name.toUpperCase(Locale.ROOT)).getByte(null);
            return getPotion(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Getter
    protected final int id;
    @Getter
    protected final int level;
    @Getter
    protected final int duration;
    protected boolean splash;

    public Potion(int id) {
        this(id, 1);
    }

    public Potion(int id, int level) {
        this(id, level, false);
    }

    public Potion(int id, int level, boolean splash) {
        this(id, level, 0, splash);
    }

    public Potion(int id, int level, int durationSeconds) {
        this(id, level, durationSeconds, false);
    }

    public Potion(int id, int level, int durationSeconds, boolean splash) {
        this.id = id;
        this.level = level;
        this.duration = durationSeconds * 20;
        this.splash = splash;
    }

    /**
     * @deprecated use getEffects()
     */
    @Deprecated
    public Effect getEffect() {
        return getEffect(this.getId(), this.isSplash());
    }

    public boolean isSplash() {
        return splash;
    }

    public Potion setSplash(boolean splash) {
        this.splash = splash;
        return this;
    }

    public void applyPotion(Entity entity) {
        applyPotion(entity, 0.5);
    }

    public void applyPotion(Entity entity, double health) {
        if (!(entity instanceof EntityLiving)) {
            return;
        }

        if (this.getId() == WATER && (entity.getNetworkId() == EntityEnderman.NETWORK_ID || entity.getNetworkId() == EntityStrider.NETWORK_ID || entity.getNetworkId() == EntitySnowGolem.NETWORK_ID || entity.getNetworkId() == EntityBlaze.NETWORK_ID)) {
            entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 1f));
            return;
        }

        for (Effect applyEffect : this.getEffects()) {
            PotionApplyEvent event = new PotionApplyEvent(this, applyEffect, entity);

            entity.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                continue;
            }

            applyEffect = event.getApplyEffect();

            switch (this.getId()) {
                case INSTANT_HEALTH:
                case INSTANT_HEALTH_II:
                    if (entity instanceof EntitySmite) {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (6 << (applyEffect.getAmplifier() + 1)))));
                    } else {
                        entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                    }
                    break;
                case HARMING:
                    if (entity instanceof EntitySmite) {
                        entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                    } else {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * 6)));
                    }
                    break;
                case HARMING_II:
                    if (entity instanceof EntitySmite) {
                        entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                    } else {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * 12)));
                    }
                    break;
                default:
                    applyEffect.setDuration((int) ((this.isSplash() ? health : 1) * (double) applyEffect.getDuration() + 0.5));
                    entity.addEffect(applyEffect, this.isSplash() ? EntityPotionEffectEvent.Cause.POTION_SPLASH : EntityPotionEffectEvent.Cause.POTION_DRINK);
            }
        }
    }

    @Override
    public Potion clone() {
        try {
            return (Potion) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Effect[] getEffects() {
        return getEffects(this.getId(), this.isSplash());
    }

    public static Effect[] getEffects(int potionType, boolean isSplash) {
        switch (potionType) {
            case TURTLE_MASTER:
            case TURTLE_MASTER_II:
            case TURTLE_MASTER_LONG:
                Effect slowness = Effect.getEffect(Effect.SLOWNESS);
                slowness.setAmplifier(potionType == TURTLE_MASTER_II ? 5 : 3);
                slowness.setDuration(potionType == TURTLE_MASTER_II ? 400 : 800);
                Effect resistance = Effect.getEffect(Effect.RESISTANCE);
                resistance.setAmplifier(potionType == TURTLE_MASTER_II ? 3 : 2);
                resistance.setDuration(potionType == TURTLE_MASTER_II ? 400 : 800);
                return new Effect[]{slowness, resistance};
        }

        Effect effect = getEffect(potionType, isSplash);
        if (effect == null) {
            return new Effect[0];
        }

        return new Effect[]{effect};
    }

    @Nullable
    public static Effect getEffect(int potionType, boolean isSplash) {
        Effect effect = null;

        switch (potionType) {
            case NIGHT_VISION:
            case NIGHT_VISION_LONG:
                effect = Effect.getEffect(Effect.NIGHT_VISION);
                break;
            case INVISIBLE:
            case INVISIBLE_LONG:
                effect = Effect.getEffect(Effect.INVISIBILITY);
                break;
            case LEAPING:
            case LEAPING_LONG:
            case LEAPING_II:
                effect = Effect.getEffect(Effect.JUMP);
                break;
            case FIRE_RESISTANCE:
            case FIRE_RESISTANCE_LONG:
                effect = Effect.getEffect(Effect.FIRE_RESISTANCE);
                break;
            case SPEED:
            case SPEED_LONG:
            case SPEED_II:
                effect = Effect.getEffect(Effect.SPEED);
                break;
            case SLOWNESS:
            case SLOWNESS_LONG:
            case SLOWNESS_IV:
                effect = Effect.getEffect(Effect.SLOWNESS);
                break;
            case WATER_BREATHING:
            case WATER_BREATHING_LONG:
                effect = Effect.getEffect(Effect.WATER_BREATHING);
                break;
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                effect = Effect.getEffect(Effect.HEALING);
                break;
            case HARMING:
            case HARMING_II:
                effect = Effect.getEffect(Effect.HARMING);
                break;
            case POISON:
            case POISON_LONG:
            case POISON_II:
                effect = Effect.getEffect(Effect.POISON);
                break;
            case REGENERATION:
            case REGENERATION_LONG:
            case REGENERATION_II:
                effect = Effect.getEffect(Effect.REGENERATION);
                break;
            case STRENGTH:
            case STRENGTH_LONG:
            case STRENGTH_II:
                effect = Effect.getEffect(Effect.STRENGTH);
                break;
            case WEAKNESS:
            case WEAKNESS_LONG:
                effect = Effect.getEffect(Effect.WEAKNESS);
                break;
            case WITHER_II:
                effect = Effect.getEffect(Effect.WITHER);
                break;
            case TURTLE_MASTER:
            case TURTLE_MASTER_II:
            case TURTLE_MASTER_LONG:
                //effect = Effect.getEffect(Effect.SLOWNESS);
                effect = Effect.getEffect(Effect.RESISTANCE);
                break;
            case WIND_CHARGED:
                effect = Effect.getEffect(Effect.WIND_CHARGED);
                break;
            case WEAVING:
                effect = Effect.getEffect(Effect.WEAVING);
                break;
            case OOZING:
                effect = Effect.getEffect(Effect.OOZING);
                break;
            case INFESTED:
                effect = Effect.getEffect(Effect.INFESTED);
                break;
        }

        if (effect == null) {
            return null;
        }

        Potion potion = getPotion(potionType);
        if (potion != null) {
            if (potion.getLevel() > 1) {
                effect.setAmplifier(1);
            }

            if (potion.getDuration() > 0) {
                effect.setDuration(potion.getDuration());
            }
        }

        return effect;
    }

    @Deprecated
    public static int getLevel(int potionType) {
        Potion potion = getPotion(potionType);
        if (potion != null) {
            return potion.getLevel();
        }
        return 1;
    }

    @Deprecated
    public static boolean isInstant(int potionType) {
        switch (potionType) {
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
            case HARMING:
            case HARMING_II:
                return true;
            default:
                return false;
        }
    }

    @Deprecated
    public static int getApplySeconds(int potionType, boolean isSplash) {
        Potion potion = getPotion(potionType);
        if (potion != null) {
            return potion.getDuration();
        }
        return 0;
    }
}

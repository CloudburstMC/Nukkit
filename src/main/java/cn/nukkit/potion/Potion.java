package cn.nukkit.potion;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.utils.ServerException;

/**
 * @author MagicDroidX (Nukkit Project)
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

    protected static Potion[] potions;

    public static void init() {
        potions = new Potion[256];

        potions[Potion.WATER] = new Potion(Potion.WATER);
        potions[Potion.MUNDANE] = new Potion(Potion.MUNDANE);
        potions[Potion.MUNDANE_II] = new Potion(Potion.MUNDANE_II, 2);
        potions[Potion.THICK] = new Potion(Potion.THICK);
        potions[Potion.AWKWARD] = new Potion(Potion.AWKWARD);
        potions[Potion.NIGHT_VISION] = new Potion(Potion.NIGHT_VISION);
        potions[Potion.NIGHT_VISION_LONG] = new Potion(Potion.NIGHT_VISION_LONG);
        potions[Potion.INVISIBLE] = new Potion(Potion.INVISIBLE);
        potions[Potion.INVISIBLE_LONG] = new Potion(Potion.INVISIBLE_LONG);
        potions[Potion.LEAPING] = new Potion(Potion.LEAPING);
        potions[Potion.LEAPING_LONG] = new Potion(Potion.LEAPING_LONG);
        potions[Potion.LEAPING_II] = new Potion(Potion.LEAPING_II, 2);
        potions[Potion.FIRE_RESISTANCE] = new Potion(Potion.FIRE_RESISTANCE);
        potions[Potion.FIRE_RESISTANCE_LONG] = new Potion(Potion.FIRE_RESISTANCE_LONG);
        potions[Potion.SPEED] = new Potion(Potion.SPEED);
        potions[Potion.SPEED_LONG] = new Potion(Potion.SPEED_LONG);
        potions[Potion.SPEED_II] = new Potion(Potion.SPEED_II, 2);
        potions[Potion.SLOWNESS] = new Potion(Potion.SLOWNESS);
        potions[Potion.SLOWNESS_LONG] = new Potion(Potion.SLOWNESS_LONG);
        potions[Potion.WATER_BREATHING] = new Potion(Potion.WATER_BREATHING);
        potions[Potion.WATER_BREATHING_LONG] = new Potion(Potion.WATER_BREATHING_LONG);
        potions[Potion.INSTANT_HEALTH] = new Potion(Potion.INSTANT_HEALTH);
        potions[Potion.INSTANT_HEALTH_II] = new Potion(Potion.INSTANT_HEALTH_II, 2);
        potions[Potion.HARMING] = new Potion(Potion.HARMING);
        potions[Potion.HARMING_II] = new Potion(Potion.HARMING_II, 2);
        potions[Potion.POISON] = new Potion(Potion.POISON);
        potions[Potion.POISON_LONG] = new Potion(Potion.POISON_LONG);
        potions[Potion.POISON_II] = new Potion(Potion.POISON_II, 2);
        potions[Potion.REGENERATION] = new Potion(Potion.REGENERATION);
        potions[Potion.REGENERATION_LONG] = new Potion(Potion.REGENERATION_LONG);
        potions[Potion.REGENERATION_II] = new Potion(Potion.REGENERATION_II, 2);
        potions[Potion.STRENGTH] = new Potion(Potion.STRENGTH);
        potions[Potion.STRENGTH_LONG] = new Potion(Potion.STRENGTH_LONG);
        potions[Potion.STRENGTH_II] = new Potion(Potion.STRENGTH_II, 2);
        potions[Potion.WEAKNESS] = new Potion(Potion.WEAKNESS);
        potions[Potion.WEAKNESS_LONG] = new Potion(Potion.WEAKNESS_LONG);
        potions[Potion.WITHER_II] = new Potion(Potion.WITHER_II, 2);
        potions[Potion.TURTLE_MASTER] = new Potion(Potion.TURTLE_MASTER);
        potions[Potion.TURTLE_MASTER_LONG] = new Potion(Potion.TURTLE_MASTER_LONG);
        potions[Potion.TURTLE_MASTER_II] = new Potion(Potion.TURTLE_MASTER_II, 2);
        potions[Potion.SLOW_FALLING] = new Potion(Potion.SLOW_FALLING);
        potions[Potion.SLOW_FALLING_LONG] = new Potion(Potion.SLOW_FALLING_LONG);
    }

    public static Potion getPotion(int id) {
        if (id >= 0 && id < potions.length && potions[id] != null) {
            return potions[id].clone();
        } else {
            throw new ServerException("Effect id: " + id + " not found");
        }
    }

    public static Potion getPotionByName(String name) {
        try {
            byte id = Potion.class.getField(name.toUpperCase()).getByte(null);
            return getPotion(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final int id;

    protected final int level;

    protected boolean splash = false;

    public Potion(int id) {
        this(id, 1);
    }

    public Potion(int id, int level) {
        this(id, level, false);
    }

    public Potion(int id, int level, boolean splash) {
        this.id = id;
        this.level = level;
        this.splash = splash;
    }

    public Effect getEffect() {
        return getEffect(this.getId(), this.isSplash());
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
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

        Effect applyEffect = getEffect(this.getId(), this.isSplash());

        if (applyEffect == null) {
            return;
        }

        if (entity instanceof Player) {
            if (!((Player) entity).isSurvival() && !((Player) entity).isAdventure() && applyEffect.isBad()) {
                return;
            }
        }

        PotionApplyEvent event = new PotionApplyEvent(this, applyEffect, entity);

        entity.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        applyEffect = event.getApplyEffect();

        switch (this.getId()) {
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                if (entity.isUndead())
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
                else
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                break;
            case HARMING:
            case HARMING_II:
                if (entity.isUndead())
                    entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                else
                    entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, (float) (health * (double) (6 << (applyEffect.getAmplifier() + 1)))));
                break;
            default:
                int duration = (int) ((isSplash() ? health : 1) * (double) applyEffect.getDuration() + 0.5);
                applyEffect.setDuration(duration);
                entity.addEffect(applyEffect);
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

    public static Effect getEffect(int potionType, boolean isSplash) {
        Effect effect;
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
                effect = Effect.getEffect(Effect.SLOWNESS);
                break;
            case WATER_BREATHING:
            case WATER_BREATHING_LONG:
                effect = Effect.getEffect(Effect.WATER_BREATHING);
                break;
            case INSTANT_HEALTH:
            case INSTANT_HEALTH_II:
                return Effect.getEffect(Effect.HEALING);
            case HARMING:
            case HARMING_II:
                return Effect.getEffect(Effect.HARMING);
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
            default:
                return null;
        }

        if (getLevel(potionType) > 1) {
            effect.setAmplifier(1);
        }

        if (!isInstant(potionType)) {
            effect.setDuration(20 * getApplySeconds(potionType, isSplash));
        }

        return effect;
    }

    public static int getLevel(int potionType) {
        switch (potionType) {
            case MUNDANE_II:
            case LEAPING_II:
            case SPEED_II:
            case INSTANT_HEALTH_II:
            case HARMING_II:
            case POISON_II:
            case REGENERATION_II:
            case STRENGTH_II:
            case WITHER_II:
            case TURTLE_MASTER_II:
                return 2;
            default:
                return 1;
        }
    }

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

    public static int getApplySeconds(int potionType, boolean isSplash) {
        if (isSplash) {
            switch (potionType) {
                case NIGHT_VISION:
                case STRENGTH:
                case WATER_BREATHING:
                case SPEED:
                case FIRE_RESISTANCE:
                case LEAPING:
                case INVISIBLE:
                    return 135;
                case NIGHT_VISION_LONG:
                case STRENGTH_LONG:
                case WATER_BREATHING_LONG:
                case SPEED_LONG:
                case FIRE_RESISTANCE_LONG:
                case LEAPING_LONG:
                case INVISIBLE_LONG:
                    return 360;
                case LEAPING_II:
                case WEAKNESS:
                case STRENGTH_II:
                case SLOWNESS:
                case SPEED_II:
                    return 67;
                case SLOWNESS_LONG:
                case WEAKNESS_LONG:
                    return 180;
                case POISON:
                case REGENERATION:
                    return 33;
                case POISON_LONG:
                case REGENERATION_LONG:
                    return 90;
                case POISON_II:
                case REGENERATION_II:
                    return 16;
                case WITHER_II:
                    return 30;
                default:
                    return 0;
            }
        } else {
            switch (potionType) {
                case NIGHT_VISION:
                case STRENGTH:
                case WATER_BREATHING:
                case SPEED:
                case FIRE_RESISTANCE:
                case LEAPING:
                case INVISIBLE:
                    return 180;
                case NIGHT_VISION_LONG:
                case STRENGTH_LONG:
                case WATER_BREATHING_LONG:
                case SPEED_II:
                case SPEED_LONG:
                case FIRE_RESISTANCE_LONG:
                case LEAPING_LONG:
                case INVISIBLE_LONG:
                    return 480;
                case LEAPING_II:
                case WEAKNESS:
                case STRENGTH_II:
                case SLOWNESS:
                    return 90;
                case SLOWNESS_LONG:
                case WEAKNESS_LONG:
                    return 240;
                case POISON:
                case REGENERATION:
                    return 45;
                case POISON_LONG:
                case REGENERATION_LONG:
                    return 120;
                case POISON_II:
                case REGENERATION_II:
                    return 22;
                case WITHER_II:
                    return 30;
                default:
                    return 0;
            }
        }
    }
}

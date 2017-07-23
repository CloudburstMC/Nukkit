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
 * author: MagicDroidX
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
            if (!((Player) entity).isSurvival() && applyEffect.isBad()) {
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
                entity.heal(new EntityRegainHealthEvent(entity, (float) (health * (double) (4 << (applyEffect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_EATING));
                break;
            case HARMING:
            case HARMING_II:
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
            case NO_EFFECTS:
            case MUNDANE:
            case MUNDANE_II:
            case THICK:
            case AWKWARD:
                return null;
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
                case NO_EFFECTS:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 135;
                case NIGHT_VISION_LONG:
                    return 360;
                case INVISIBLE:
                    return 135;
                case INVISIBLE_LONG:
                    return 360;
                case LEAPING:
                    return 135;
                case LEAPING_LONG:
                    return 360;
                case LEAPING_II:
                    return 67;
                case FIRE_RESISTANCE:
                    return 135;
                case FIRE_RESISTANCE_LONG:
                    return 360;
                case SPEED:
                    return 135;
                case SPEED_LONG:
                    return 360;
                case SPEED_II:
                    return 67;
                case SLOWNESS:
                    return 67;
                case SLOWNESS_LONG:
                    return 180;
                case WATER_BREATHING:
                    return 135;
                case WATER_BREATHING_LONG:
                    return 360;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 33;
                case POISON_LONG:
                    return 90;
                case POISON_II:
                    return 16;
                case REGENERATION:
                    return 33;
                case REGENERATION_LONG:
                    return 90;
                case REGENERATION_II:
                    return 16;
                case STRENGTH:
                    return 135;
                case STRENGTH_LONG:
                    return 360;
                case STRENGTH_II:
                    return 67;
                case WEAKNESS:
                    return 67;
                case WEAKNESS_LONG:
                    return 180;
                default:
                    return 0;
            }
        } else {
            switch (potionType) {
                case NO_EFFECTS:
                    return 0;
                case MUNDANE:
                    return 0;
                case MUNDANE_II:
                    return 0;
                case THICK:
                    return 0;
                case AWKWARD:
                    return 0;
                case NIGHT_VISION:
                    return 180;
                case NIGHT_VISION_LONG:
                    return 480;
                case INVISIBLE:
                    return 180;
                case INVISIBLE_LONG:
                    return 480;
                case LEAPING:
                    return 180;
                case LEAPING_LONG:
                    return 480;
                case LEAPING_II:
                    return 90;
                case FIRE_RESISTANCE:
                    return 180;
                case FIRE_RESISTANCE_LONG:
                    return 480;
                case SPEED:
                    return 180;
                case SPEED_LONG:
                    return 480;
                case SPEED_II:
                    return 480;
                case SLOWNESS:
                    return 90;
                case SLOWNESS_LONG:
                    return 240;
                case WATER_BREATHING:
                    return 180;
                case WATER_BREATHING_LONG:
                    return 480;
                case INSTANT_HEALTH:
                    return 0;
                case INSTANT_HEALTH_II:
                    return 0;
                case HARMING:
                    return 0;
                case HARMING_II:
                    return 0;
                case POISON:
                    return 45;
                case POISON_LONG:
                    return 120;
                case POISON_II:
                    return 22;
                case REGENERATION:
                    return 45;
                case REGENERATION_LONG:
                    return 120;
                case REGENERATION_II:
                    return 22;
                case STRENGTH:
                    return 180;
                case STRENGTH_LONG:
                    return 480;
                case STRENGTH_II:
                    return 90;
                case WEAKNESS:
                    return 90;
                case WEAKNESS_LONG:
                    return 240;
                default:
                    return 0;
            }
        }
    }
}

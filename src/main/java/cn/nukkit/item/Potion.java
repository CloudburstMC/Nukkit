package cn.nukkit.item;

import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

public class Potion extends Item {

    public static final int NO_EFFECTS = 0;
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

    public Potion() {
        this(0, 1);
    }

    public Potion(int meta) {
        this(meta, 1);
    }

    public Potion(int meta, int count) {
        super(POTION, meta, count, "Potion");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public void applyPotion(Entity entity) {
        switch (this.getDamage()) {
            case 5: //night vision
                entity.addEffect(Effect.getEffect(Effect.NIGHT_VISION).setAmplifier(0).setDuration(180 * 20));
                break;
            case 6: //night vision
                entity.addEffect(Effect.getEffect(Effect.NIGHT_VISION).setAmplifier(0).setDuration(8 * 60 * 20));
                break;
            case 7: //invisible
                entity.addEffect(Effect.getEffect(Effect.INVISIBILITY).setAmplifier(0).setDuration(180 * 20));
                break;
            case 8: //invisible
                entity.addEffect(Effect.getEffect(Effect.INVISIBILITY).setAmplifier(0).setDuration(8 * 60 * 20));
                break;
            case 9: //leaping
                break;
            case 10: //leaping
                break;
            case 11: //leaping
                break;
            case 12: //fire resistance
                entity.addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setAmplifier(0).setDuration(180 * 20));
                break;
            case 13: //fire resistance
                entity.addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setAmplifier(0).setDuration(8 * 60 * 20));
                break;
            case 14: //speed I
                entity.addEffect(Effect.getEffect(Effect.SPEED).setAmplifier(0).setDuration(180 * 20));
                break;
            case 15: //speed I
                entity.addEffect(Effect.getEffect(Effect.SPEED).setAmplifier(0).setDuration(8 * 60 * 20));
                break;
            case 16: //speed II
                entity.addEffect(Effect.getEffect(Effect.SPEED).setAmplifier(1).setDuration(90 * 20));
                break;
            case 17: //slownes
                entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setAmplifier(0).setDuration(90 * 20));
                break;
            case 18: //slownes
                entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setAmplifier(0).setDuration(4 * 60 * 20));
                break;
            case 19: //water breathing
                entity.addEffect(Effect.getEffect(Effect.WATER_BREATHING).setAmplifier(0).setDuration(180 * 20));
                break;
            case 20: //water breathing
                entity.addEffect(Effect.getEffect(Effect.WATER_BREATHING).setAmplifier(0).setDuration(8 * 60 * 20));
                break;
            case 21:
                entity.heal(4, new EntityRegainHealthEvent(entity, 4, EntityRegainHealthEvent.CAUSE_EATING));
                break;
            case 22:
                entity.heal(8, new EntityRegainHealthEvent(entity, 8, EntityRegainHealthEvent.CAUSE_EATING));
                break;
            case 23: //harming
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 6));
                break;
            case 24: //harming II
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.CAUSE_MAGIC, 12));
                break;
            case 25: //poison
                entity.addEffect(Effect.getEffect(Effect.POISON).setAmplifier(0).setDuration(45 * 20));
                break;
            case 26: //poison
                entity.addEffect(Effect.getEffect(Effect.POISON).setAmplifier(0).setDuration(90 * 20));
                break;
            case 27: //poison II
                entity.addEffect(Effect.getEffect(Effect.POISON).setAmplifier(1).setDuration(22 * 20));
                break;
            case 28: //regen I
                entity.addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(0).setDuration(45 * 20));
                break;
            case 29: //regen I
                entity.addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(0).setDuration(120 * 20));
                break;
            case 30: //regen II
                entity.addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(22 * 20));
                break;
            case 31: //strenght I
                entity.addEffect(Effect.getEffect(Effect.STRENGTH).setAmplifier(0).setDuration(180 * 20));
                break;
            case 32: //strenght I
                entity.addEffect(Effect.getEffect(Effect.STRENGTH).setAmplifier(0).setDuration(480 * 20));
                break;
            case 33: //strenght II
                entity.addEffect(Effect.getEffect(Effect.STRENGTH).setAmplifier(1).setDuration(90 * 20));
                break;
            case 34: //weakness
                entity.addEffect(Effect.getEffect(Effect.WEAKNESS).setAmplifier(0).setDuration(90 * 20));
                break;
            case 35: //weakness
                entity.addEffect(Effect.getEffect(Effect.WEAKNESS).setAmplifier(0).setDuration(4 * 60 * 20));
                break;
        }
    }
}
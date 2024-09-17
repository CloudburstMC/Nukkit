package cn.nukkit.item;

import cn.nukkit.potion.Effect;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemArrow extends Item {

    public ItemArrow() {
        this(0, 1);
    }

    public ItemArrow(Integer meta) {
        this(meta, 1);
    }

    public ItemArrow(Integer meta, int count) {
        super(ARROW, meta, count, "Arrow");
    }

    public static Effect getEffect(int meta) {
        switch (meta) {
            case 6:
                return Effect.getEffect(Effect.NIGHT_VISION).setDuration(440);
            case 7:
                return Effect.getEffect(Effect.NIGHT_VISION).setDuration(1200);
            case 8:
                return Effect.getEffect(Effect.INVISIBILITY).setDuration(440);
            case 9:
                return Effect.getEffect(Effect.INVISIBILITY).setDuration(1200);
            case 10:
                return Effect.getEffect(Effect.JUMP).setDuration(440);
            case 11:
                return Effect.getEffect(Effect.JUMP).setDuration(1200);
            case 12:
                return Effect.getEffect(Effect.JUMP).setAmplifier(1).setDuration(220);
            case 13:
                return Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(440);
            case 14:
                return Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(1200);
            case 15:
                return Effect.getEffect(Effect.SPEED).setDuration(440);
            case 16:
                return Effect.getEffect(Effect.SPEED).setDuration(1200);
            case 17:
                return Effect.getEffect(Effect.SPEED).setAmplifier(1).setDuration(220);
            case 18:
                return Effect.getEffect(Effect.SLOWNESS).setDuration(220);
            case 19:
                return Effect.getEffect(Effect.SLOWNESS).setDuration(600);
            case 20:
                return Effect.getEffect(Effect.WATER_BREATHING).setDuration(440);
            case 21:
                return Effect.getEffect(Effect.WATER_BREATHING).setDuration(1200);
            case 22:
                return Effect.getEffect(Effect.HEALING).setDuration(1);
            case 23:
                return Effect.getEffect(Effect.HEALING).setAmplifier(1).setDuration(1);
            case 24:
                return Effect.getEffect(Effect.HARMING).setDuration(1);
            case 25:
                return Effect.getEffect(Effect.HARMING).setAmplifier(1).setDuration(1);
            case 26:
                return Effect.getEffect(Effect.POISON).setDuration(100);
            case 27:
                return Effect.getEffect(Effect.POISON).setDuration(300);
            case 28:
                return Effect.getEffect(Effect.POISON).setAmplifier(1).setDuration(40);
            case 29:
                return Effect.getEffect(Effect.REGENERATION).setDuration(100);
            case 30:
                return Effect.getEffect(Effect.REGENERATION).setDuration(300);
            case 31:
                return Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(40);
            case 32:
                return Effect.getEffect(Effect.STRENGTH).setDuration(440);
            case 33:
                return Effect.getEffect(Effect.STRENGTH).setDuration(1200);
            case 34:
                return Effect.getEffect(Effect.STRENGTH).setAmplifier(1).setDuration(220);
            case 35:
                return Effect.getEffect(Effect.WEAKNESS).setDuration(220);
            case 36:
                return Effect.getEffect(Effect.WEAKNESS).setDuration(600);
            case 37:
                return Effect.getEffect(Effect.WITHER).setAmplifier(1).setDuration(100);
            case 38:
                return Effect.getEffect(Effect.SLOWNESS).setAmplifier(3).setDuration(40);
            //return Effect.getEffect(Effect.DAMAGE_RESISTANCE).setAmplifier(2).setDuration(40);
            case 39:
                return Effect.getEffect(Effect.SLOWNESS).setAmplifier(3).setDuration(100);
            //return Effect.getEffect(Effect.DAMAGE_RESISTANCE).setAmplifier(2).setDuration(100);
            case 40:
                return Effect.getEffect(Effect.SLOWNESS).setAmplifier(5).setDuration(40);
            //return Effect.getEffect(Effect.DAMAGE_RESISTANCE).setAmplifier(3).setDuration(40);
            case 41:
                return Effect.getEffect(Effect.SLOW_FALLING).setDuration(220);
            case 42:
                return Effect.getEffect(Effect.SLOW_FALLING).setDuration(600);
            case 43:
                return Effect.getEffect(Effect.SLOWNESS).setAmplifier(3).setDuration(40);
            default:
                return null;
        }
    }

    @Override
    public boolean allowOffhand() {
        return true;
    }
}

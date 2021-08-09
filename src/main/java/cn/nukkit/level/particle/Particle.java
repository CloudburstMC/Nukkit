package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.lang.reflect.Field;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Particle extends Vector3 {

    public static final int TYPE_BUBBLE = 1;
    public static final int TYPE_BUBBLE_MANUAL = 2;
    public static final int TYPE_CRITICAL = 3;
    public static final int TYPE_BLOCK_FORCE_FIELD = 4;
    public static final int TYPE_SMOKE = 5;
    public static final int TYPE_EXPLODE = 6;
    public static final int TYPE_EVAPORATION = 7;
    public static final int TYPE_FLAME = 8;
    public static final int TYPE_CANDLE_FLAME = 9;
    public static final int TYPE_LAVA = 10;
    public static final int TYPE_LARGE_SMOKE = 11;
    public static final int TYPE_REDSTONE = 12;
    public static final int TYPE_RISING_RED_DUST = 13;
    public static final int TYPE_ITEM_BREAK = 14;
    public static final int TYPE_SNOWBALL_POOF = 15;
    public static final int TYPE_HUGE_EXPLODE = 16;
    public static final int TYPE_HUGE_EXPLODE_SEED = 17;
    public static final int TYPE_MOB_FLAME = 18;
    public static final int TYPE_HEART = 19;
    public static final int TYPE_TERRAIN = 20;
    public static final int TYPE_SUSPENDED_TOWN = 21, TYPE_TOWN_AURA = 21;
    public static final int TYPE_PORTAL = 22;
    // 22 same as 21
    public static final int TYPE_SPLASH = 24, TYPE_WATER_SPLASH = 24;
    public static final int TYPE_WATER_SPLASH_MANUAL = 25;
    public static final int TYPE_WATER_WAKE = 26;
    public static final int TYPE_DRIP_WATER = 27;
    public static final int TYPE_DRIP_LAVA = 28;
    public static final int TYPE_DRIP_HONEY = 29;
    public static final int TYPE_STALACTITE_DRIP_WATER = 30;
    public static final int TYPE_STALACTITE_DRIP_LAVA = 31;
    public static final int TYPE_FALLING_DUST = 32, TYPE_DUST = 32;
    public static final int TYPE_MOB_SPELL = 33;
    public static final int TYPE_MOB_SPELL_AMBIENT = 34;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 35;
    public static final int TYPE_INK = 36;
    public static final int TYPE_SLIME = 37;
    public static final int TYPE_RAIN_SPLASH = 38;
    public static final int TYPE_VILLAGER_ANGRY = 39;
    public static final int TYPE_VILLAGER_HAPPY = 40;
    public static final int TYPE_ENCHANTMENT_TABLE = 41;
    public static final int TYPE_TRACKING_EMITTER = 42;
    public static final int TYPE_NOTE = 43;
    public static final int TYPE_WITCH_SPELL = 44;
    public static final int TYPE_CARROT = 45;
    public static final int TYPE_MOB_APPEARANCE  = 46;
    public static final int TYPE_END_ROD = 47;
    public static final int TYPE_RISING_DRAGONS_BREATH = 48;
    public static final int TYPE_SPIT = 49;
    public static final int TYPE_TOTEM = 50;
    public static final int TYPE_FOOD = 51;
    public static final int TYPE_FIREWORKS_STARTER = 52;
    public static final int TYPE_FIREWORKS_SPARK = 53;
    public static final int TYPE_FIREWORKS_OVERLAY = 54;
    public static final int TYPE_BALLOON_GAS = 55;
    public static final int TYPE_COLORED_FLAME = 56;
    public static final int TYPE_SPARKLER = 57;
    public static final int TYPE_CONDUIT = 58;
    public static final int TYPE_BUBBLE_COLUMN_UP = 59;
    public static final int TYPE_BUBBLE_COLUMN_DOWN = 60;
    public static final int TYPE_SNEEZE = 61;
    public static final int TYPE_SHULKER_BULLET = 62;
    public static final int TYPE_BLEACH = 63;
    public static final int TYPE_LARGE_EXPLOSION = 64;
    public static final int TYPE_MYCELIUM_DUST = 65;
    public static final int TYPE_FALLING_RED_DUST = 66;
    public static final int TYPE_CAMPFIRE_SMOKE = 67;
    public static final int TYPE_TALL_CAMPFIRE_SMOKE = 68;
    public static final int TYPE_FALLING_DRAGONS_BREATH = 69;
    public static final int TYPE_DRAGONS_BREATH = 70;
    public static final int TYPE_BLUE_FLAME = 71;
    public static final int TYPE_SOUL = 72;
    public static final int TYPE_OBSIDIAN_TEAR = 73;
    public static final int TYPE_PORTAL_REVERSE = 74;
    public static final int TYPE_SNOWFLAKE = 75;
    public static final int TYPE_VIBRATION_SIGNAL = 76;
    public static final int TYPE_SCULK_SENSOR_REDSTONE = 77;
    public static final int TYPE_SPORE_BLOSSOM_SHOWER = 78;
    public static final int TYPE_SPORE_BLOSSOM_AMBIENT = 79;
    public static final int TYPE_WAX = 80;
    public static final int TYPE_ELECTRIC_SPARK = 81;

    public static final Integer getParticleIdByName(String name) {
        name = name.toUpperCase();

        try {
            Field field = Particle.class.getField((name.startsWith("TYPE_") == true ? name : ("TYPE_" + name)));

            Class<?> type = field.getType();

            if(type==int.class) {
                return field.getInt(null);
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    public static final boolean particleExists(String name) {
        return getParticleIdByName(name) != null;   
    }

    public Particle() {
        super(0, 0, 0);
    }

    public Particle(double x) {
        super(x, 0, 0);
    }

    public Particle(double x, double y) {
        super(x, y, 0);
    }

    public Particle(double x, double y, double z) {
        super(x, y, z);
    }

    abstract public DataPacket[] encode();
}

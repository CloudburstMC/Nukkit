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
    public static final int TYPE_LAVA = 9;
    public static final int TYPE_LARGE_SMOKE = 10;
    public static final int TYPE_REDSTONE = 11;
    public static final int TYPE_RISING_RED_DUST = 12;
    public static final int TYPE_ITEM_BREAK = 13;
    public static final int TYPE_SNOWBALL_POOF = 14;
    public static final int TYPE_HUGE_EXPLODE = 15;
    public static final int TYPE_HUGE_EXPLODE_SEED = 16;
    public static final int TYPE_MOB_FLAME = 17;
    public static final int TYPE_HEART = 18;
    public static final int TYPE_TERRAIN = 19;
    public static final int TYPE_SUSPENDED_TOWN = 20, TYPE_TOWN_AURA = 20;
    public static final int TYPE_PORTAL = 21;
    // 22 same as 21
    public static final int TYPE_SPLASH = 23, TYPE_WATER_SPLASH = 23;
    public static final int TYPE_WATER_SPLASH_MANUAL = 24;
    public static final int TYPE_WATER_WAKE = 25;
    public static final int TYPE_DRIP_WATER = 26;
    public static final int TYPE_DRIP_LAVA = 27;
    public static final int TYPE_DRIP_HONEY = 28;
    public static final int TYPE_STALACTITE_DRIP_WATER = 29;
    public static final int TYPE_STALACTITE_DRIP_LAVA = 30;
    public static final int TYPE_FALLING_DUST = 31, TYPE_DUST = 31;
    public static final int TYPE_MOB_SPELL = 32;
    public static final int TYPE_MOB_SPELL_AMBIENT = 33;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 34;
    public static final int TYPE_INK = 35;
    public static final int TYPE_SLIME = 36;
    public static final int TYPE_RAIN_SPLASH = 37;
    public static final int TYPE_VILLAGER_ANGRY = 38;
    public static final int TYPE_VILLAGER_HAPPY = 39;
    public static final int TYPE_ENCHANTMENT_TABLE = 40;
    public static final int TYPE_TRACKING_EMITTER = 41;
    public static final int TYPE_NOTE = 42;
    public static final int TYPE_WITCH_SPELL = 43;
    public static final int TYPE_CARROT = 44;
    public static final int TYPE_MOB_APPEARANCE  = 45;
    public static final int TYPE_END_ROD = 46;
    public static final int TYPE_RISING_DRAGONS_BREATH = 47;
    public static final int TYPE_SPIT = 48;
    public static final int TYPE_TOTEM = 49;
    public static final int TYPE_FOOD = 50;
    public static final int TYPE_FIREWORKS_STARTER = 51;
    public static final int TYPE_FIREWORKS_SPARK = 52;
    public static final int TYPE_FIREWORKS_OVERLAY = 53;
    public static final int TYPE_BALLOON_GAS = 54;
    public static final int TYPE_COLORED_FLAME = 55;
    public static final int TYPE_SPARKLER = 56;
    public static final int TYPE_CONDUIT = 57;
    public static final int TYPE_BUBBLE_COLUMN_UP = 58;
    public static final int TYPE_BUBBLE_COLUMN_DOWN = 59;
    public static final int TYPE_SNEEZE = 60;
    public static final int TYPE_SHULKER_BULLET = 61;
    public static final int TYPE_BLEACH = 62;
    public static final int TYPE_LARGE_EXPLOSION = 63;
    public static final int TYPE_MYCELIUM_DUST = 64;
    public static final int TYPE_FALLING_RED_DUST = 65;
    public static final int TYPE_CAMPFIRE_SMOKE = 66;
    public static final int TYPE_TALL_CAMPFIRE_SMOKE = 67;
    public static final int TYPE_FALLING_DRAGONS_BREATH = 68;
    public static final int TYPE_DRAGONS_BREATH = 69;
    public static final int TYPE_BLUE_FLAME = 70;
    public static final int TYPE_SOUL = 71;
    public static final int TYPE_OBSIDIAN_TEAR = 72;
    public static final int TYPE_PORTAL_REVERSE = 73;
    public static final int TYPE_SNOWFLAKE = 74;
    public static final int TYPE_VIBRATION_SIGNAL = 75;
    public static final int TYPE_SCULK_SENSOR_REDSTONE = 76;
    public static final int TYPE_SPORE_BLOSSOM_SHOWER = 77;
    public static final int TYPE_SPORE_BLOSSOM_AMBIENT = 78;
    public static final int TYPE_WAX = 79;
    public static final int TYPE_ELECTRIC_SPARK = 80;

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

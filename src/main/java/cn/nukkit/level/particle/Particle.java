package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author MagicDroidX
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
    public static final int BREEZE_WIND_EXPLOSION = 18;
    public static final int TYPE_MOB_FLAME = 19;
    public static final int TYPE_HEART = 20;
    public static final int TYPE_TERRAIN = 21;
    public static final int TYPE_SUSPENDED_TOWN = 22, TYPE_TOWN_AURA = 22;
    public static final int TYPE_PORTAL = 23;
    public static final int TYPE_SPLASH = 25, TYPE_WATER_SPLASH = 25;
    public static final int TYPE_WATER_SPLASH_MANUAL = 26;
    public static final int TYPE_WATER_WAKE = 27;
    public static final int TYPE_DRIP_WATER = 28;
    public static final int TYPE_DRIP_LAVA = 29;
    public static final int TYPE_DRIP_HONEY = 30;
    public static final int TYPE_STALACTITE_DRIP_WATER = 31;
    public static final int TYPE_STALACTITE_DRIP_LAVA = 32;
    public static final int TYPE_FALLING_DUST = 33, TYPE_DUST = 33;
    public static final int TYPE_MOB_SPELL = 34;
    public static final int TYPE_MOB_SPELL_AMBIENT = 35;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 36;
    public static final int TYPE_INK = 37;
    public static final int TYPE_SLIME = 38;
    public static final int TYPE_RAIN_SPLASH = 39;
    public static final int TYPE_VILLAGER_ANGRY = 40;
    public static final int TYPE_VILLAGER_HAPPY = 41;
    public static final int TYPE_ENCHANTMENT_TABLE = 42;
    public static final int TYPE_TRACKING_EMITTER = 43;
    public static final int TYPE_NOTE = 44;
    public static final int TYPE_WITCH_SPELL = 45;
    public static final int TYPE_CARROT = 46;
    public static final int TYPE_MOB_APPEARANCE = 47;
    public static final int TYPE_END_ROD = 48;
    public static final int TYPE_DRAGON_BREATH = 49;
    public static final int TYPE_SPIT = 50;
    public static final int TYPE_TOTEM = 51;
    public static final int TYPE_FOOD = 52;
    public static final int TYPE_FIREWORKS_STARTER = 53;
    public static final int TYPE_FIREWORKS_SPARK = 54;
    public static final int TYPE_FIREWORKS_OVERLAY = 55;
    public static final int TYPE_BALLOON_GAS = 56;
    public static final int TYPE_COLORED_FLAME = 57;
    public static final int TYPE_SPARKLER = 58;
    public static final int TYPE_CONDUIT = 59;
    public static final int TYPE_BUBBLE_COLUMN_UP = 60;
    public static final int TYPE_BUBBLE_COLUMN_DOWN = 61;
    public static final int TYPE_SNEEZE = 62;
    public static final int TYPE_SHULKER_BULLET = 63;
    public static final int TYPE_BLEACH = 64;
    public static final int TYPE_DRAGON_DESTROY_BLOCK = 65;
    public static final int TYPE_MYCELIUM_DUST = 66;
    public static final int TYPE_FALLING_RED_DUST = 67;
    public static final int TYPE_CAMPFIRE_SMOKE = 68;
    public static final int TYPE_TALL_CAMPFIRE_SMOKE = 69;
    public static final int TYPE_DRAGON_BREATH_FIRE = 70;
    public static final int TYPE_DRAGON_BREATH_TRAIL = 71;
    public static final int TYPE_BLUE_FLAME = 72;
    public static final int TYPE_SOUL = 73;
    public static final int TYPE_OBSIDIAN_TEAR = 74;
    public static final int TYPE_PORTAL_REVERSE = 75;
    public static final int TYPE_SNOWFLAKE = 76;
    public static final int TYPE_VIBRATION_SIGNAL = 77;
    public static final int TYPE_SCULK_SENSOR_REDSTONE = 78;
    public static final int TYPE_SPORE_BLOSSOM_SHOWER = 79;
    public static final int TYPE_SPORE_BLOSSOM_AMBIENT = 80;
    public static final int TYPE_WAX = 81;
    public static final int TYPE_ELECTRIC_SPARK = 82;
    public static final int TYPE_SHRIEK = 83;
    public static final int TYPE_SCULK_SOUL = 84;
    public static final int TYPE_SONIC_EXPLOSION = 85;
    public static final int TYPE_BRUSH_DUST = 86;
    public static final int TYPE_CHERRY_LEAVES = 87;
    public static final int TYPE_DUST_PLUME = 88;
    public static final int TYPE_WHITE_SMOKE = 89;
    public static final int TYPE_VAULT_CONNECTION = 90;
    public static final int TYPE_WIND_EXPLOSION = 91;
    public static final int TYPE_WOLF_ARMOR_BREAK = 92;
    public static final int TYPE_OMINOUS_ITEM_SPAWNER = 93;
    public static final int TYPE_CREAKING_CRUMBLE = 94;
    public static final int TYPE_PALE_OAK_LEAVES = 95;
    public static final int TYPE_EYEBLOSSOM_OPEN = 96;
    public static final int TYPE_EYEBLOSSOM_CLOSE = 97;

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

    public abstract DataPacket[] encode();

    public static Integer getParticleIdByName(String name) {
        name = name.toUpperCase(Locale.ROOT);

        try {
            Field field = Particle.class.getField((name.startsWith("TYPE_") ? name : ("TYPE_" + name)));

            Class<?> type = field.getType();

            if (type == int.class) {
                return field.getInt(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        return null;
    }

    public static boolean particleExists(String name) {
        return getParticleIdByName(name) != null;
    }
}

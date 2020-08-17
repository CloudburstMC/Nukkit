package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Particle extends Vector3 {

    public static final int TYPE_BUBBLE = 1;
    // 2 same as 1
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
    // 61 same as 20
    public static final int TYPE_PORTAL = 21;
    // 22 same as 21
    public static final int TYPE_SPLASH = 23, TYPE_WATER_SPLASH = 23;
    // 24 same as 23
    public static final int TYPE_WATER_WAKE = 25;
    public static final int TYPE_DRIP_WATER = 26;
    public static final int TYPE_DRIP_LAVA = 27;
    public static final int TYPE_DRIP_HONEY = 28;
    public static final int TYPE_FALLING_DUST = 29, TYPE_DUST = 29;
    public static final int TYPE_MOB_SPELL = 30;
    public static final int TYPE_MOB_SPELL_AMBIENT = 31;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 32;
    public static final int TYPE_NOTE_AND_DUST = 33;

    public static final int TYPE_SLIME = 34;
    public static final int TYPE_RAIN_SPLASH = 35;
    public static final int TYPE_VILLAGER_ANGRY = 36;
    // 60 same as 36
    public static final int TYPE_VILLAGER_HAPPY = 37;
    public static final int TYPE_ENCHANTMENT_TABLE = 38;
    public static final int TYPE_TRACKING_EMITTER = 39;
    public static final int TYPE_NOTE = 40;
    public static final int TYPE_WITCH_SPELL = 41;
    public static final int TYPE_CARROT = 42;
    // 43 unknown
    public static final int TYPE_END_ROD = 44;
    // 59 same as 44
    public static final int TYPE_RISING_DRAGONS_BREATH = 45;
    public static final int TYPE_SPIT = 46;
    public static final int TYPE_TOTEM = 47;
    public static final int TYPE_FOOD = 48;
    public static final int TYPE_FIREWORKS_STARTER = 49;
    public static final int TYPE_FIREWORKS_SPARK = 50;
    public static final int TYPE_FIREWORKS_OVERLAY = 51;
    public static final int TYPE_BALLOON_GAS = 52;
    public static final int TYPE_COLORED_FLAME = 53;
    public static final int TYPE_SPARKLER = 54;
    public static final int TYPE_CONDUIT = 55;
    public static final int TYPE_BUBBLE_COLUMN_UP = 56;
    public static final int TYPE_BUBBLE_COLUMN_DOWN = 57;
    public static final int TYPE_SNEEZE = 58;

    public static final int TYPE_LARGE_EXPLOSION = 61;
    public static final int TYPE_INK = 62;
    public static final int TYPE_FALLING_RED_DUST = 63;
    public static final int TYPE_CAMPFIRE_SMOKE = 64;
    //65 same as 64
    public static final int TYPE_FALLING_DRAGONS_BREATH = 66;
    public static final int TYPE_DRAGONS_BREATH = 67;

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

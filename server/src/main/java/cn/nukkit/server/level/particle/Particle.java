package cn.nukkit.server.level.particle;

import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.network.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Particle extends Vector3 {
    public static final int TYPE_BUBBLE = 1;
    public static final int TYPE_CRITICAL = 2;
    public static final int TYPE_BLOCK_FORCE_FIELD = 3;
    public static final int TYPE_SMOKE = 4;
    public static final int TYPE_EXPLODE = 5;
    public static final int TYPE_EVAPORATION = 6;
    public static final int TYPE_FLAME = 7;
    public static final int TYPE_LAVA = 8;
    public static final int TYPE_LARGE_SMOKE = 9;
    public static final int TYPE_REDSTONE = 10;
    public static final int TYPE_RISING_RED_DUST = 11;
    public static final int TYPE_ITEM_BREAK = 12;
    public static final int TYPE_SNOWBALL_POOF = 13;
    public static final int TYPE_HUGE_EXPLODE = 14;
    public static final int TYPE_HUGE_EXPLODE_SEED = 15;
    public static final int TYPE_MOB_FLAME = 16;
    public static final int TYPE_HEART = 17;
    public static final int TYPE_TERRAIN = 18;
    public static final int TYPE_SUSPENDED_TOWN = 19, TYPE_TOWN_AURA = 19;
    public static final int TYPE_PORTAL = 20;
    public static final int TYPE_SPLASH = 21, TYPE_WATER_SPLASH = 21;
    public static final int TYPE_WATER_WAKE = 22;
    public static final int TYPE_DRIP_WATER = 23;
    public static final int TYPE_DRIP_LAVA = 24;
    public static final int TYPE_FALLING_DUST = 25, TYPE_DUST = 25;
    public static final int TYPE_MOB_SPELL = 26;
    public static final int TYPE_MOB_SPELL_AMBIENT = 27;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 28;
    public static final int TYPE_INK = 29;
    public static final int TYPE_SLIME = 30;
    public static final int TYPE_RAIN_SPLASH = 31;
    public static final int TYPE_VILLAGER_ANGRY = 32;
    public static final int TYPE_VILLAGER_HAPPY = 33;
    public static final int TYPE_ENCHANTMENT_TABLE = 34;
    public static final int TYPE_TRACKING_EMITTER = 35;
    public static final int TYPE_NOTE = 36;
    public static final int TYPE_WITCH_SPELL = 37;
    public static final int TYPE_CARROT = 38;
    //39 unknown
    public static final int TYPE_END_ROD = 40;
    public static final int TYPE_DRAGONS_BREATH = 41;

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

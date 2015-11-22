package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Particle extends Vector3 {
    public static final int TYPE_BUBBLE = 1;
    public static final int TYPE_CRITICAL = 2;
    public static final int TYPE_SMOKE = 3;
    public static final int TYPE_EXPLODE = 4;
    public static final int TYPE_WHITE_SMOKE = 5;
    public static final int TYPE_FLAME = 6;
    public static final int TYPE_LAVA = 7;
    public static final int TYPE_LARGE_SMOKE = 8;
    public static final int TYPE_REDSTONE = 9;
    public static final int TYPE_ITEM_BREAK = 10;
    public static final int TYPE_SNOWBALL_POOF = 11;
    public static final int TYPE_LARGE_EXPLODE = 12;
    public static final int TYPE_HUGE_EXPLODE = 13;
    public static final int TYPE_MOB_FLAME = 14;
    public static final int TYPE_HEART = 15;
    public static final int TYPE_TERRAIN = 16;
    public static final int TYPE_TOWN_AURA = 17;
    public static final int TYPE_PORTAL = 18;
    public static final int TYPE_WATER_SPLASH = 19;
    public static final int TYPE_WATER_WAKE = 20;
    public static final int TYPE_DRIP_WATER = 21;
    public static final int TYPE_DRIP_LAVA = 22;
    public static final int TYPE_DUST = 23;
    public static final int TYPE_MOB_SPELL = 24;
    public static final int TYPE_MOB_SPELL_AMBIENT = 25;
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = 26;
    public static final int TYPE_INK = 27;
    public static final int TYPE_SLIME = 28;
    public static final int TYPE_RAIN_SPLASH = 29;
    public static final int TYPE_VILLAGER_ANGRY = 30;
    public static final int TYPE_VILLAGER_HAPPY = 31;
    public static final int TYPE_ENCHANTMENT_TABLE = 32;

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

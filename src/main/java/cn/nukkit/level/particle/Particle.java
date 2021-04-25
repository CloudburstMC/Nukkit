package cn.nukkit.level.particle;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.lang.reflect.Field;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Particle extends Vector3 {

    public static final int TYPE_BUBBLE = dynamic(1);
    @Since("1.4.0.0-PN") public static final int TYPE_BUBBLE_MANUAL = dynamic(2);
    public static final int TYPE_CRITICAL = dynamic(3);
    public static final int TYPE_BLOCK_FORCE_FIELD = dynamic(4);
    public static final int TYPE_SMOKE = dynamic(5);
    public static final int TYPE_EXPLODE = dynamic(6);
    public static final int TYPE_EVAPORATION = dynamic(7);
    public static final int TYPE_FLAME = dynamic(8);
    public static final int TYPE_LAVA = dynamic(9);
    public static final int TYPE_LARGE_SMOKE = dynamic(10);
    public static final int TYPE_REDSTONE = dynamic(11);
    public static final int TYPE_RISING_RED_DUST = dynamic(12);
    public static final int TYPE_ITEM_BREAK = dynamic(13);
    public static final int TYPE_SNOWBALL_POOF = dynamic(14);
    public static final int TYPE_HUGE_EXPLODE = dynamic(15);
    public static final int TYPE_HUGE_EXPLODE_SEED = dynamic(16);
    public static final int TYPE_MOB_FLAME = dynamic(17);
    public static final int TYPE_HEART = dynamic(18);
    public static final int TYPE_TERRAIN = dynamic(19);
    public static final int TYPE_SUSPENDED_TOWN = dynamic(20), TYPE_TOWN_AURA = TYPE_SUSPENDED_TOWN;
    public static final int TYPE_PORTAL = dynamic(21);
    // 22 same as 21
    public static final int TYPE_SPLASH = dynamic(23), TYPE_WATER_SPLASH = TYPE_SPLASH;
    @Since("1.4.0.0-PN") public static final int TYPE_WATER_SPLASH_MANUAL = dynamic(24);
    public static final int TYPE_WATER_WAKE = dynamic(25);
    public static final int TYPE_DRIP_WATER = dynamic(26);
    public static final int TYPE_DRIP_LAVA = dynamic(27);
    public static final int TYPE_DRIP_HONEY = dynamic(28);
    @Since("1.4.0.0-PN") public static final int TYPE_STALACTITE_DRIP_WATER = dynamic(29);
    @Since("1.4.0.0-PN") public static final int TYPE_STALACTITE_DRIP_LAVA = dynamic(30);
    public static final int TYPE_FALLING_DUST = dynamic(31), TYPE_DUST = TYPE_FALLING_DUST;
    public static final int TYPE_MOB_SPELL = dynamic(32);
    public static final int TYPE_MOB_SPELL_AMBIENT = dynamic(33);
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = dynamic(34);
    public static final int TYPE_INK = dynamic(35);
    public static final int TYPE_SLIME = dynamic(36);
    public static final int TYPE_RAIN_SPLASH = dynamic(37);
    public static final int TYPE_VILLAGER_ANGRY = dynamic(38);
    public static final int TYPE_VILLAGER_HAPPY = dynamic(39);
    public static final int TYPE_ENCHANTMENT_TABLE = dynamic(40);
    public static final int TYPE_TRACKING_EMITTER = dynamic(41);
    public static final int TYPE_NOTE = dynamic(42);
    
    @PowerNukkitOnly("Backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "NukkitX", reason = "Removed from Nukkit")
    public static final int TYPE_NOTE_AND_DUST = TYPE_NOTE;
    
    public static final int TYPE_WITCH_SPELL = dynamic(43);
    public static final int TYPE_CARROT = dynamic(44);
    @Since("1.4.0.0-PN") public static final int TYPE_MOB_APPEARANCE  = dynamic(45);
    public static final int TYPE_END_ROD = dynamic(46);
    public static final int TYPE_RISING_DRAGONS_BREATH = dynamic(47);
    public static final int TYPE_SPIT = dynamic(48);
    public static final int TYPE_TOTEM = dynamic(49);
    public static final int TYPE_FOOD = dynamic(50);
    public static final int TYPE_FIREWORKS_STARTER = dynamic(51);
    public static final int TYPE_FIREWORKS_SPARK = dynamic(52);
    public static final int TYPE_FIREWORKS_OVERLAY = dynamic(53);
    public static final int TYPE_BALLOON_GAS = dynamic(54);
    public static final int TYPE_COLORED_FLAME = dynamic(55);
    public static final int TYPE_SPARKLER = dynamic(56);
    public static final int TYPE_CONDUIT = dynamic(57);
    public static final int TYPE_BUBBLE_COLUMN_UP = dynamic(58);
    public static final int TYPE_BUBBLE_COLUMN_DOWN = dynamic(59);
    public static final int TYPE_SNEEZE = dynamic(60);
    @Since("1.4.0.0-PN") public static final int TYPE_SHULKER_BULLET = dynamic(61);
    @Since("1.4.0.0-PN") public static final int TYPE_BLEACH = dynamic(62);
    public static final int TYPE_LARGE_EXPLOSION = dynamic(63);
    @Since("1.4.0.0-PN") public static final int TYPE_MYCELIUM_DUST = dynamic(64);
    public static final int TYPE_FALLING_RED_DUST = dynamic(65);
    public static final int TYPE_CAMPFIRE_SMOKE = dynamic(66);
    @Since("1.4.0.0-PN") public static final int TYPE_TALL_CAMPFIRE_SMOKE = dynamic(67);
    public static final int TYPE_FALLING_DRAGONS_BREATH = dynamic(68);
    public static final int TYPE_DRAGONS_BREATH = dynamic(69);
    @Since("1.4.0.0-PN") public static final int TYPE_BLUE_FLAME = dynamic(70);
    @Since("1.4.0.0-PN") public static final int TYPE_SOUL = dynamic(71);
    @Since("1.4.0.0-PN") public static final int TYPE_OBSIDIAN_TEAR = dynamic(72);
    @Since("1.4.0.0-PN") public static final int TYPE_PORTAL_REVERSE = dynamic(73);
    @Since("1.4.0.0-PN") public static final int TYPE_SNOWFLAKE = dynamic(74);
    @Since("1.4.0.0-PN") public static final int TYPE_VIBRATION_SIGNAL = dynamic(75);
    @Since("1.4.0.0-PN") public static final int TYPE_SCULK_SENSOR_REDSTONE = dynamic(76);
    @Since("1.4.0.0-PN") public static final int TYPE_SPORE_BLOSSOM_SHOWER = dynamic(77);
    @Since("1.4.0.0-PN") public static final int TYPE_SPORE_BLOSSOM_AMBIENT = dynamic(78);
    @Since("1.4.0.0-PN") public static final int TYPE_WAX = dynamic(79);
    @Since("1.4.0.0-PN") public static final int TYPE_ELECTRIC_SPARK = dynamic(80);

    public static Integer getParticleIdByName(String name) {
        name = name.toUpperCase();

        try {
            Field field = Particle.class.getDeclaredField(name.startsWith("TYPE_") ? name : "TYPE_" + name);

            Class<?> type = field.getType();

            if(type==int.class) {
                return field.getInt(null);
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    @Since("1.3.2.0-PN")
    public static boolean particleExists(String name) {
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

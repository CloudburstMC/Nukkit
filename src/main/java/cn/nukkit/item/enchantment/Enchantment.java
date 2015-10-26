package cn.nukkit.item.enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Enchantment implements Cloneable {

    public static final int TYPE_INVALID = -1;

    public static final int TYPE_ARMOR_PROTECTION = 0;
    public static final int TYPE_ARMOR_FIRE_PROTECTION = 1;
    public static final int TYPE_ARMOR_FALL_PROTECTION = 2;
    public static final int TYPE_ARMOR_EXPLOSION_PROTECTION = 3;
    public static final int TYPE_ARMOR_PROJECTILE_PROTECTION = 4;
    public static final int TYPE_ARMOR_THORNS = 5;
    public static final int TYPE_WATER_BREATHING = 6;
    public static final int TYPE_WATER_SPEED = 7;
    public static final int TYPE_WATER_AFFINITY = 8;
    public static final int TYPE_WEAPON_SHARPNESS = 9;
    public static final int TYPE_WEAPON_SMITE = 10;
    public static final int TYPE_WEAPON_ARTHROPODS = 11;
    public static final int TYPE_WEAPON_KNOCKBACK = 12;
    public static final int TYPE_WEAPON_FIRE_ASPECT = 13;
    public static final int TYPE_WEAPON_LOOTING = 14;
    public static final int TYPE_MINING_EFFICIENCY = 15;
    public static final int TYPE_MINING_SILK_TOUCH = 16;
    public static final int TYPE_MINING_DURABILITY = 17;
    public static final int TYPE_MINING_FORTUNE = 18;
    public static final int TYPE_BOW_POWER = 19;
    public static final int TYPE_BOW_KNOCKBACK = 20;
    public static final int TYPE_BOW_FLAME = 21;
    public static final int TYPE_BOW_INFINITY = 22;
    public static final int TYPE_FISHING_FORTUNE = 23;
    public static final int TYPE_FISHING_LURE = 24;

    public static final int RARITY_COMMON = 0;
    public static final int RARITY_UNCOMMON = 1;
    public static final int RARITY_RARE = 2;
    public static final int RARITY_MYTHIC = 3;

    public static final int ACTIVATION_EQUIP = 0;
    public static final int ACTIVATION_HELD = 1;
    public static final int ACTIVATION_SELF = 2;

    public static final int SLOT_NONE = 0;
    public static final int SLOT_ALL = 0b11111111111111;
    public static final int SLOT_ARMOR = 0b1111;
    public static final int SLOT_HEAD = 0b1;
    public static final int SLOT_TORSO = 0b10;
    public static final int SLOT_LEGS = 0b100;
    public static final int SLOT_FEET = 0b1000;
    public static final int SLOT_SWORD = 0b10000;
    public static final int SLOT_BOW = 0b100000;
    public static final int SLOT_TOOL = 0b111000000;
    public static final int SLOT_HOE = 0b1000000;
    public static final int SLOT_SHEARS = 0b10000000;
    public static final int SLOT_FLINT_AND_STEEL = 0b10000000;
    public static final int SLOT_DIG = 0b111000000000;
    public static final int SLOT_AXE = 0b1000000000;
    public static final int SLOT_PICKAXE = 0b10000000000;
    public static final int SLOT_SHOVEL = 0b10000000000;
    public static final int SLOT_FISHING_ROD = 0b100000000000;
    public static final int SLOT_CARROT_STICK = 0b1000000000000;

    protected static Enchantment[] enchantments;

    public static void init() {
        enchantments = new Enchantment[256];
        enchantments[TYPE_ARMOR_PROTECTION] = new Enchantment(TYPE_ARMOR_PROTECTION, "%enchantment.protect.all", RARITY_COMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_FIRE_PROTECTION] = new Enchantment(TYPE_ARMOR_FIRE_PROTECTION, "%enchantment.protect.fire", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_FALL_PROTECTION] = new Enchantment(TYPE_ARMOR_FALL_PROTECTION, "%enchantment.protect.fall", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_FEET);

    }

    public static Enchantment getEnchantment(int id) {
        try {
            if (enchantments[id] != null) {
                return enchantments[id].clone();
            }
        } catch (Exception e) {
            return new Enchantment(TYPE_INVALID, "unknown", 0, 0, 0);
        }
        return new Enchantment(TYPE_INVALID, "unknown", 0, 0, 0);
    }

    public static Enchantment getEnchantmentByName(String name) {
        try {
            short id = Enchantment.class.getField("TYPE_" + name.toUpperCase()).getByte(null);
            return getEnchantment(id);
        } catch (Exception e) {
            return null;
        }
    }

    private int id;
    private int level = 1;
    private String name;
    private int rarity;
    private int activationType;
    private int slot;

    private Enchantment(int id, String name, int rarity, int activationType, int slot) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.activationType = activationType;
        this.slot = slot;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRarity() {
        return rarity;
    }

    public int getActivationType() {
        return activationType;
    }

    public int getSlot() {
        return slot;
    }

    public boolean hasSlot(int slot) {
        return (this.slot & slot) > 0;
    }

    public int getLevel() {
        return level;
    }

    public Enchantment setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

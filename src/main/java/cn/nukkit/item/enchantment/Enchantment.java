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
        enchantments[TYPE_ARMOR_PROTECTION] = new Enchantment(TYPE_ARMOR_PROTECTION, "%enchantment.protect.all", RARITY_COMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_FIRE_PROTECTION] = new Enchantment(TYPE_ARMOR_FIRE_PROTECTION, "%enchantment.protect.fire", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_FALL_PROTECTION] = new Enchantment(TYPE_ARMOR_FALL_PROTECTION, "%enchantment.protect.fall", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_FEET);
        enchantments[TYPE_ARMOR_EXPLOSION_PROTECTION] = new Enchantment(TYPE_ARMOR_EXPLOSION_PROTECTION, "%enchantment.protect.explosion", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_PROJECTILE_PROTECTION] = new Enchantment(TYPE_ARMOR_PROJECTILE_PROTECTION, "%enchantment.protect.projectile", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_ARMOR_THORNS] = new Enchantment(TYPE_ARMOR_THORNS, "%enchantment.thorns", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_ARMOR);
        enchantments[TYPE_WATER_BREATHING] = new Enchantment(TYPE_WATER_BREATHING, "%enchantment.water.breathing", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_HEAD);
        enchantments[TYPE_WATER_SPEED] = new Enchantment(TYPE_WATER_SPEED, "%enchantment.water.speed", RARITY_UNCOMMON, ACTIVATION_EQUIP, SLOT_FEET);
        enchantments[TYPE_WATER_AFFINITY] = new Enchantment(TYPE_WATER_AFFINITY, "%enchantment.water.affinity", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_TOOL);
        enchantments[TYPE_WEAPON_SHARPNESS] = new Enchantment(TYPE_WEAPON_SHARPNESS, "%enchantment.weapon.sharpness", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_WEAPON_SMITE] = new Enchantment(TYPE_WEAPON_SMITE, "%enchantment.weapon.smite", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_WEAPON_ARTHROPODS] = new Enchantment(TYPE_WEAPON_ARTHROPODS, "%enchantment.weapon.arthropods", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_WEAPON_KNOCKBACK] = new Enchantment(TYPE_WEAPON_KNOCKBACK, "%enchantment.weapon.knockback", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_WEAPON_FIRE_ASPECT] = new Enchantment(TYPE_WEAPON_FIRE_ASPECT, "%enchantment.weapon.fire", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_WEAPON_LOOTING] = new Enchantment(TYPE_WEAPON_LOOTING, "%enchantment.weapon.looting", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_SWORD);
        enchantments[TYPE_MINING_EFFICIENCY] = new Enchantment(TYPE_MINING_EFFICIENCY, "%enchantment.mining.efficiency", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_PICKAXE);
        enchantments[TYPE_MINING_SILK_TOUCH] = new Enchantment(TYPE_MINING_SILK_TOUCH, "%enchantment.mining.silktouch", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_PICKAXE);
        enchantments[TYPE_MINING_DURABILITY] = new Enchantment(TYPE_MINING_DURABILITY, "%enchantment.mining.durability", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_TOOL);
        enchantments[TYPE_MINING_FORTUNE] = new Enchantment(TYPE_MINING_FORTUNE, "%enchantment.mining.fortune", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_PICKAXE);
        enchantments[TYPE_BOW_POWER] = new Enchantment(TYPE_BOW_POWER, "%enchantment.bow.power", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_BOW);
        enchantments[TYPE_BOW_KNOCKBACK] = new Enchantment(TYPE_BOW_KNOCKBACK, "%enchantment.bow.knockback", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_BOW);
        enchantments[TYPE_BOW_FLAME] = new Enchantment(TYPE_BOW_FLAME, "%enchantment.bow.flame", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_BOW);
        enchantments[TYPE_BOW_INFINITY] = new Enchantment(TYPE_BOW_INFINITY, "%enchantment.bow.infinity", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_BOW);
        enchantments[TYPE_FISHING_FORTUNE] = new Enchantment(TYPE_FISHING_FORTUNE, "%enchantment.fishing.fortune", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_FISHING_ROD);
        enchantments[TYPE_FISHING_LURE] = new Enchantment(TYPE_FISHING_LURE, "%enchantment.fishing.lure", RARITY_UNCOMMON, ACTIVATION_HELD, SLOT_FISHING_ROD);
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

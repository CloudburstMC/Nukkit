package cn.nukkit.item.enchantment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private static Map<Integer, Enchantment> registry = new LinkedHashMap<>();

    public static final Enchantment armor_protection = registerEnchantment(
            new Enchantment(TYPE_ARMOR_PROTECTION, "%enchantment.protect.all").setRarity(RARITY_COMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_ARMOR));
    public static final Enchantment armor_fire_protection = registerEnchantment(
            new Enchantment(TYPE_ARMOR_FIRE_PROTECTION, "%enchantment.protect.fire").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_ARMOR));
    public static final Enchantment armor_fall_protection = registerEnchantment(
            new Enchantment(TYPE_ARMOR_FALL_PROTECTION, "%enchantment.protect.fall").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_FEET));
    public static final Enchantment armor_explosion_protection = registerEnchantment(
            new Enchantment(TYPE_ARMOR_EXPLOSION_PROTECTION, "%enchantment.protect.explosion").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_ARMOR));
    public static final Enchantment armor_projectile_protection = registerEnchantment(
            new Enchantment(TYPE_ARMOR_PROJECTILE_PROTECTION, "%enchantment.protect.projectile").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_ARMOR));
    public static final Enchantment armor_thorns = registerEnchantment(
            new Enchantment(TYPE_ARMOR_THORNS, "%enchantment.thorns").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_ARMOR));
    public static final Enchantment water_breathing = registerEnchantment(
            new Enchantment(TYPE_WATER_BREATHING, "%enchantment.water.breathing").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_HEAD));
    public static final Enchantment water_speed = registerEnchantment(
            new Enchantment(TYPE_WATER_SPEED, "%enchantment.water.speed").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_EQUIP).addSlot(SLOT_FEET));
    public static final Enchantment water_affinity = registerEnchantment(
            new Enchantment(TYPE_WATER_AFFINITY, "%enchantment.water.affinity").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_TOOL));
    public static final Enchantment weapon_sharpness = registerEnchantment(
            new Enchantment(TYPE_WEAPON_SHARPNESS, "%enchantment.weapon.sharpness").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment weapon_smite = registerEnchantment(
            new Enchantment(TYPE_WEAPON_SMITE, "%enchantment.weapon.smite").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment weapon_arthropods = registerEnchantment(
            new Enchantment(TYPE_WEAPON_ARTHROPODS, "%enchantment.weapon.arthropods").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment weapon_knockback = registerEnchantment(
            new Enchantment(TYPE_WEAPON_KNOCKBACK, "%enchantment.weapon.knockback").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment weapon_fire_aspect = registerEnchantment(
            new Enchantment(TYPE_WEAPON_FIRE_ASPECT, "%enchantment.weapon.fire").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment weapon_looting = registerEnchantment(
            new Enchantment(TYPE_WEAPON_LOOTING, "%enchantment.weapon.looting").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_SWORD));
    public static final Enchantment mining_efficiency = registerEnchantment(
            new Enchantment(TYPE_MINING_EFFICIENCY, "%enchantment.mining.efficiency").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_PICKAXE));
    public static final Enchantment mining_silk_touch = registerEnchantment(
            new Enchantment(TYPE_MINING_SILK_TOUCH, "%enchantment.mining.silktouch").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_PICKAXE));
    public static final Enchantment mining_durability = registerEnchantment(
            new Enchantment(TYPE_MINING_DURABILITY, "%enchantment.mining.durability").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_TOOL));
    public static final Enchantment mining_fortune = registerEnchantment(
            new Enchantment(TYPE_MINING_FORTUNE, "%enchantment.mining.fortune").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_PICKAXE));
    public static final Enchantment bow_power = registerEnchantment(
            new Enchantment(TYPE_BOW_POWER, "%enchantment.bow.power").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_BOW));
    public static final Enchantment bow_knockback = registerEnchantment(
            new Enchantment(TYPE_BOW_KNOCKBACK, "%enchantment.bow.knockback").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_BOW));
    public static final Enchantment bow_flame = registerEnchantment(
            new Enchantment(TYPE_BOW_FLAME, "%enchantment.bow.flame").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_BOW));
    public static final Enchantment bow_infinity = registerEnchantment(
            new Enchantment(TYPE_BOW_INFINITY, "%enchantment.bow.infinity").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_BOW));
    public static final Enchantment fishing_fortune = registerEnchantment(
            new Enchantment(TYPE_FISHING_FORTUNE, "%enchantment.fishing.fortune").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_FISHING_ROD));
    public static final Enchantment fishing_lure = registerEnchantment(
            new Enchantment(TYPE_FISHING_LURE, "%enchantment.fishing.lure").setRarity(RARITY_UNCOMMON).setActivationType(ACTIVATION_HELD).addSlot(SLOT_FISHING_ROD));
    
    public static void init() {
        //does nothing, but JVM will init all constant values
    }

    public static Enchantment registerEnchantment(Enchantment enchantment) {
        registry.put(enchantment.getId(), enchantment);
        return enchantment;
    }

    public static Enchantment getEnchantment(int id) {
        return registry.getOrDefault(id, new Enchantment(TYPE_INVALID, "unknown"));
    }

    private int id;
    private int level = 1;
    private String name;
    private int rarity = RARITY_COMMON;
    private int activationType = ACTIVATION_EQUIP;
    private int slot = SLOT_NONE;

    private Enchantment(int id, String name) {
        this.id = id;
        this.name = name;
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

    public Enchantment setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    public int getActivationType() {
        return activationType;
    }

    public Enchantment setActivationType(int activationType) {
        this.activationType = activationType;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Enchantment addSlot(int slot) {
        this.slot |= slot;
        return this;
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

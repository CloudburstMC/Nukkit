package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.bow.EnchantmentBowFlame;
import cn.nukkit.item.enchantment.bow.EnchantmentBowInfinity;
import cn.nukkit.item.enchantment.bow.EnchantmentBowKnockback;
import cn.nukkit.item.enchantment.bow.EnchantmentBowPower;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageAll;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageArthropods;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageSmite;
import cn.nukkit.item.enchantment.loot.EnchantmentLootDigging;
import cn.nukkit.item.enchantment.loot.EnchantmentLootFishing;
import cn.nukkit.item.enchantment.loot.EnchantmentLootWeapon;
import cn.nukkit.item.enchantment.protection.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Enchantment implements Cloneable {

    protected static Enchantment[] enchantments;

    public static final int ID_PROTECTION_ALL = 0;
    public static final int ID_PROTECTION_FIRE = 1;
    public static final int ID_PROTECTION_FALL = 2;
    public static final int ID_PROTECTION_EXPLOSION = 3;
    public static final int ID_PROTECTION_PROJECTILE = 4;
    public static final int ID_THORNS = 5;
    public static final int ID_WATER_BREATHING = 6;
    public static final int UD_WATER_WORKER = 7;
    public static final int ID_WATER_WALKER = 8;
    public static final int ID_DAMAGE_ALL = 9;
    public static final int ID_DAMAGE_SMITE = 10;
    public static final int ID_DAMAGE_ARTHROPODS = 11;
    public static final int ID_KNOCKBACK = 12;
    public static final int ID_FIRE_ASPECT = 13;
    public static final int ID_LOOTING = 14;
    public static final int ID_EFFICIENCY = 15;
    public static final int ID_SILK_TOUCH = 16;
    public static final int ID_DURABILITY = 17;
    public static final int ID_FORTUNE_DIGGING = 18;
    public static final int ID_BOW_POWER = 19;
    public static final int ID_BOW_KNOCKBACK = 20;
    public static final int ID_BOW_FLAME = 21;
    public static final int ID_BOW_INFINITY = 22;
    public static final int ID_FORTUNE_FISHING = 23;
    public static final int ID_LURE = 24;

    public static void init() {
        enchantments = new Enchantment[256];

        enchantments[ID_PROTECTION_ALL] = new EnchantmentProtectionAll();
        enchantments[ID_PROTECTION_FIRE] = new EnchantmentProtectionFire();
        enchantments[ID_PROTECTION_FALL] = new EnchantmentProtectionFall();
        enchantments[ID_PROTECTION_EXPLOSION] = new EnchantmentProtectionExplosion();
        enchantments[ID_PROTECTION_PROJECTILE] = new EnchantmentProtectionProjectile();
        enchantments[ID_THORNS] = new EnchantmentThorns();
        enchantments[ID_WATER_BREATHING] = new EnchantmentWaterBreath();
        enchantments[UD_WATER_WORKER] = new EnchantmentWaterWorker();
        enchantments[ID_WATER_WALKER] = new EnchantmentWaterWalker();
        enchantments[ID_DAMAGE_ALL] = new EnchantmentDamageAll();
        enchantments[ID_DAMAGE_SMITE] = new EnchantmentDamageSmite();
        enchantments[ID_DAMAGE_ARTHROPODS] = new EnchantmentDamageArthropods();
        enchantments[ID_KNOCKBACK] = new EnchantmentKnockback();
        enchantments[ID_FIRE_ASPECT] = new EnchantmentFireAspect();
        enchantments[ID_LOOTING] = new EnchantmentLootWeapon();
        enchantments[ID_EFFICIENCY] = new EnchantmentEfficiency();
        enchantments[ID_SILK_TOUCH] = new EnchantmentSilkTouch();
        enchantments[ID_DURABILITY] = new EnchantmentDurability();
        enchantments[ID_FORTUNE_DIGGING] = new EnchantmentLootDigging();
        enchantments[ID_BOW_POWER] = new EnchantmentBowPower();
        enchantments[ID_BOW_KNOCKBACK] = new EnchantmentBowKnockback();
        enchantments[ID_BOW_FLAME] = new EnchantmentBowFlame();
        enchantments[ID_BOW_INFINITY] = new EnchantmentBowInfinity();
        enchantments[ID_FORTUNE_FISHING] = new EnchantmentLootFishing();
        enchantments[ID_LURE] = new EnchantmentLure();
    }

    public static Enchantment get(int id) {
        return id >= 0 && id < enchantments.length ? enchantments[id] : null;
    }

    public static Enchantment getEnchantment(int id) {
        return get(id).clone();
    }

    public static Enchantment[] getEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            if (enchantment == null) {
                break;
            }

            list.add(enchantment);
        }

        return list.stream().toArray(Enchantment[]::new);
    }

    public final int id;
    private final int weight;
    public EnchantmentType type;

    protected int level;

    protected String name;

    protected Enchantment(int id, String name, int weight, EnchantmentType type) {
        this.id = id;
        this.weight = weight;
        this.type = type;

        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.setLevel(level, true);
    }

    public void setLevel(int level, boolean safe) {
        if (!safe) {
            this.level = level;
            return;
        }
        
        if (level > this.getMaxLevel()) {
            this.level = this.getMaxLevel();
        } else if (level < this.getMinLevel()) {
            this.level = this.getMaxLevel();
        } else {
            this.level = level;
        }
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 5;
    }

    public int getDamageProtection(EntityDamageEvent event) {
        return 0;
    }

    public double getDamageBonus(Entity entity) {
        return 0;
    }

    public void doPostAttack(Entity attacker, Entity entity) {

    }

    public void doPostHurt(Entity attacker, Entity entity) {

    }

    public boolean isCompatibleWith(Enchantment enchantment) {
        return this != enchantment;
    }

    public String getName() {
        return "%enchantment." + this.name;
    }

    public boolean canEnchant(Item item) {
        return this.type.canEnchantItem(item);
    }

    @Override
    protected Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static final String[] words = {"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale"};

    public static String getRandomName() {
        int count = ThreadLocalRandom.current().nextInt(3, 6);
        HashSet<String> set = new HashSet<>();
        while (set.size() < count) {
            set.add(Enchantment.words[ThreadLocalRandom.current().nextInt(0, Enchantment.words.length)]);
        }

        String[] words = set.stream().toArray(String[]::new);
        return String.join(" ", words);
    }
}

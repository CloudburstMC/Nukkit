package cn.nukkit.item.enchantment;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.bow.EnchantmentBowFlame;
import cn.nukkit.item.enchantment.bow.EnchantmentBowInfinity;
import cn.nukkit.item.enchantment.bow.EnchantmentBowKnockback;
import cn.nukkit.item.enchantment.bow.EnchantmentBowPower;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowMultishot;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowPiercing;
import cn.nukkit.item.enchantment.crossbow.EnchantmentCrossbowQuickCharge;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageAll;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageArthropods;
import cn.nukkit.item.enchantment.damage.EnchantmentDamageSmite;
import cn.nukkit.item.enchantment.loot.EnchantmentLootDigging;
import cn.nukkit.item.enchantment.loot.EnchantmentLootFishing;
import cn.nukkit.item.enchantment.loot.EnchantmentLootWeapon;
import cn.nukkit.item.enchantment.protection.*;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentChanneling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentImpaling;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentLoyalty;
import cn.nukkit.item.enchantment.trident.EnchantmentTridentRiptide;
import cn.nukkit.math.NukkitMath;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * An enchantmant that can be to applied to an item.
 * 
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Enchantment implements Cloneable {

    protected static Enchantment[] enchantments;

    //http://minecraft.gamepedia.com/Enchanting#Aqua_Affinity

    public static final int ID_PROTECTION_ALL = 0;
    public static final int ID_PROTECTION_FIRE = 1;
    public static final int ID_PROTECTION_FALL = 2;
    public static final int ID_PROTECTION_EXPLOSION = 3;
    public static final int ID_PROTECTION_PROJECTILE = 4;
    public static final int ID_THORNS = 5;
    public static final int ID_WATER_BREATHING = 6;
    public static final int ID_WATER_WALKER = 7;
    public static final int ID_WATER_WORKER = 8;
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
    public static final int ID_FROST_WALKER = 25;
    public static final int ID_MENDING = 26;
    public static final int ID_BINDING_CURSE = 27;
    public static final int ID_VANISHING_CURSE = 28;
    public static final int ID_TRIDENT_IMPALING = 29;
    public static final int ID_TRIDENT_RIPTIDE = 30;
    public static final int ID_TRIDENT_LOYALTY = 31;
    public static final int ID_TRIDENT_CHANNELING = 32;
    @Since("1.3.2.0-PN") public static final int ID_CROSSBOW_MULTISHOT = 33;
    @Since("1.3.2.0-PN") public static final int ID_CROSSBOW_PIERCING = 34;
    @Since("1.3.2.0-PN") public static final int ID_CROSSBOW_QUICK_CHARGE = 35;
    public static final int ID_SOUL_SPEED = 36;

    public static void init() {
        enchantments = new Enchantment[256];

        enchantments[ID_PROTECTION_ALL] = new EnchantmentProtectionAll();
        enchantments[ID_PROTECTION_FIRE] = new EnchantmentProtectionFire();
        enchantments[ID_PROTECTION_FALL] = new EnchantmentProtectionFall();
        enchantments[ID_PROTECTION_EXPLOSION] = new EnchantmentProtectionExplosion();
        enchantments[ID_PROTECTION_PROJECTILE] = new EnchantmentProtectionProjectile();
        enchantments[ID_THORNS] = new EnchantmentThorns();
        enchantments[ID_WATER_BREATHING] = new EnchantmentWaterBreath();
        enchantments[ID_WATER_WORKER] = new EnchantmentWaterWorker();
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
        enchantments[ID_FROST_WALKER] = new EnchantmentFrostWalker();
        enchantments[ID_MENDING]  = new EnchantmentMending();
        enchantments[ID_BINDING_CURSE]  = new EnchantmentBindingCurse();
        enchantments[ID_VANISHING_CURSE]  = new EnchantmentVanishingCurse();
        enchantments[ID_TRIDENT_IMPALING]  = new EnchantmentTridentImpaling();
        enchantments[ID_TRIDENT_RIPTIDE]  = new EnchantmentTridentRiptide();
        enchantments[ID_TRIDENT_LOYALTY]  = new EnchantmentTridentLoyalty();
        enchantments[ID_TRIDENT_CHANNELING]  = new EnchantmentTridentChanneling();
        enchantments[ID_CROSSBOW_MULTISHOT]  = new EnchantmentCrossbowMultishot();
        enchantments[ID_CROSSBOW_PIERCING]  = new EnchantmentCrossbowPiercing();
        enchantments[ID_CROSSBOW_QUICK_CHARGE]  = new EnchantmentCrossbowQuickCharge();
        enchantments[ID_SOUL_SPEED]  = new EnchantmentSoulSpeed();
    }

    /**
     * Returns the enchantment object registered with this ID, any change to the returned object affects 
     * the creation of new enchantments as the returned object is not a copy.
     * @param id The enchantment id.
     * @return The enchantment, if no enchantment is found with that id, {@link UnknownEnchantment} is returned.
     * The UnknownEnchantment will be always a new instance and changes to it does not affects other calls.
     */
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", reason = "This is very insecure and can break the environment", since = "1.3.2.0-PN",
            replaceWith = "getEnchantment(int)")
    public static Enchantment get(int id) {
        Enchantment enchantment = null;
        if (id >= 0 && id < enchantments.length) {
            enchantment = enchantments[id];
        }
        if (enchantment == null) {
            return new UnknownEnchantment(id);
        }
        return enchantment;
    }

    /**
     * The same as {@link #get(int)} but returns a safe copy of the enchantment.
     * @param id The enchantment id
     * @return A new enchantment object.
     */
    public static Enchantment getEnchantment(int id) {
        return get(id).clone();
    }

    /**
     * Gets an array of all registered enchantments, the objects in the array are linked to the registry,
     * it's not safe to change them. Changing them can cause the same issue as documented in {@link #get(int)}
     * @return An array with the enchantment objects, the array may contain null objects but is very unlikely.
     */
    @Deprecated
    @DeprecationDetails(since = "1.3.2.0-PN", by = "PowerNukkit", 
            reason = "The objects returned by this method are not safe to use and the implementation may skip some enchantments",
            replaceWith = "getRegisteredEnchantments()"
    )
    public static Enchantment[] getEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            if (enchantment == null) {
                break;
            }

            list.add(enchantment);
        }

        return list.toArray(new Enchantment[0]);
    }

    /**
     * Gets a collection with a safe copy of all enchantments that are currently registered.
     * @return The objects can be modified without affecting the registry and the collection will not have null values.
     */
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static Collection<Enchantment> getRegisteredEnchantments() {
        return Arrays.stream(enchantments)
                .filter(Objects::nonNull)
                .map(Enchantment::clone)
                .collect(Collectors.toList());
    }

    /**
     * The internal ID which this enchantment got registered.
     */
    public final int id;
    private final Rarity rarity;

    /**
     * The group of objects that this enchantment can be applied.
     */
    public EnchantmentType type;

    /**
     * The level of this enchantment. Starting from {@code 1}.
     */
    protected int level = 1;

    /**
     * The name visible by the player, this is used in conjunction with {@link #getName()}, 
     * unless modified with an override, the getter will automatically add 
     * "%enchantment." as prefix to grab the translation key 
     */
    protected final String name;

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param weight How rare this enchantment is, from {@code 1} to {@code 10} both inclusive where {@code 1} is the rarest
     * @param type Where the enchantment can be applied
     */
    @PowerNukkitOnly("Was removed from Nukkit in 1.3.2.0-PN, keeping it in PowerNukkit for backward compatibility")
    @Deprecated @DeprecationDetails(by = "Cloudburst Nukkit", since = "1.3.2.0-PN", reason = "Changed the signature without backward compatibility",
            replaceWith = "Enchantment(int, String, Rarity, EnchantmentType)")
    protected Enchantment(int id, String name, int weight, EnchantmentType type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }

    /**
     * Constructs this instance using the given data and with level 1.
     * @param id The enchantment ID
     * @param name The translation key without the "%enchantment." suffix
     * @param rarity How rare this enchantment is
     * @param type Where the enchantment can be applied
     */
    @Since("1.3.2.0-PN")
    protected Enchantment(int id, String name, Rarity rarity, EnchantmentType type) {
        this.id = id;
        this.rarity = rarity;
        this.type = type;

        this.name = name;
    }

    /**
     * The current level of this enchantment. {@code 0} means that the enchantment is not applied.
     * @return The level starting from {@code 1}.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Changes the level of this enchantment.
     * The level is clamped between the values returned in {@link #getMinLevel()} and {@link #getMaxLevel()}.
     * 
     * @param level The level starting from {@code 1}.
     * @return This object so you can do chained calls
     */
    public Enchantment setLevel(int level) {
        return this.setLevel(level, true);
    }

    /**
     * Changes the level of this enchantment.
     * When the {@code safe} param is {@code true}, the level is clamped between the values 
     * returned in {@link #getMinLevel()} and {@link #getMaxLevel()}.
     *
     * @param level The level starting from {@code 1}.
     * @param safe If the level should clamped or applied directly
     * @return This object so you can do chained calls
     */
    public Enchantment setLevel(int level, boolean safe) {
        if (!safe) {
            this.level = level;
            return this;
        }

        this.level = NukkitMath.clamp(level, this.getMinLevel(), this.getMaxLevel());

        return this;
    }

    /**
     * The ID of this enchantment. 
     */
    public int getId() {
        return id;
    }

    /**
     * How rare this enchantment is.
     */
    @Since("1.3.2.0-PN")
    public Rarity getRarity() {
        return this.rarity;
    }

    /**
     * How rare this enchantment is, from {@code 1} to {@code 10} where {@code 1} is the rarest.
     * @deprecated use {@link Rarity#getWeight()} instead
     */
    @DeprecationDetails(since = "1.3.2.0-PN", by = "Cloudburst Nukkit", 
            reason = "Refactored enchantments and now uses a Rarity enum", 
            replaceWith = "getRarity().getWeight()")
    @Deprecated
    public int getWeight() {
        return this.rarity.getWeight();
    }

    /**
     * The minimum safe level which is possible with this enchantment. It is usually {@code 1}.
     */
    public int getMinLevel() {
        return 1;
    }

    /**
     * The maximum safe level which is possible with this enchantment.
     */
    public int getMaxLevel() {
        return 1;
    }

    /**
     * The maximum level that can be obtained using an enchanting table.
     */
    public int getMaxEnchantableLevel() {
        return getMaxLevel();
    }

    public int getMinEnchantAbility(int level) {
        return 1 + level * 10;
    }

    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 5;
    }

    public float getProtectionFactor(EntityDamageEvent event) {
        return 0;
    }

    public double getDamageBonus(Entity entity) {
        return 0;
    }

    public void doPostAttack(Entity attacker, Entity entity) {

    }

    public void doPostHurt(Entity attacker, Entity entity) {

    }

    public final boolean isCompatibleWith(Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    @Since("1.3.2.0-PN")
    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    public String getName() {
        return "%enchantment." + this.name;
    }

    public boolean canEnchant(Item item) {
        return this.type.canEnchantItem(item);
    }

    public boolean isMajor() {
        return false;
    }

    @Override
    protected Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Checks if an item can have this enchantment. It's not strict to the enchantment table.
     */
    @PowerNukkitOnly @Since("1.2.1.0-PN")
    public boolean isItemAcceptable(Item item) {
        return canEnchant(item);
    }

    public static final String[] words = {"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale"};

    public static String getRandomName() {
        int count = ThreadLocalRandom.current().nextInt(3, 6);
        HashSet<String> set = new HashSet<>();
        while (set.size() < count) {
            set.add(Enchantment.words[ThreadLocalRandom.current().nextInt(0, Enchantment.words.length)]);
        }

        String[] words = set.toArray(new String[0]);
        return String.join(" ", words);
    }

    private static class UnknownEnchantment extends Enchantment {

        protected UnknownEnchantment(int id) {
            super(id, "unknown", Rarity.VERY_RARE, EnchantmentType.ALL);
        }
    }

    @Since("1.3.2.0-PN")
    public enum Rarity {
        @Since("1.3.2.0-PN") COMMON(10),
        @Since("1.3.2.0-PN") UNCOMMON(5),
        @Since("1.3.2.0-PN") RARE(2),
        @Since("1.3.2.0-PN") VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        @Since("1.3.2.0-PN")
        public int getWeight() {
            return this.weight;
        }

        @Since("1.3.2.0-PN")
        public static Rarity fromWeight(int weight) {
            if (weight < 2) {
                return VERY_RARE;
            } else if (weight < 5) {
                return RARE;
            } else if (weight < 10) {
                return UNCOMMON;
            }
            return COMMON;
        }
    }
}

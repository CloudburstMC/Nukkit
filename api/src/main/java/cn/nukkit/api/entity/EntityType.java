package cn.nukkit.api.entity;

import cn.nukkit.api.entity.item.*;
import cn.nukkit.api.entity.item.minecart.*;
import cn.nukkit.api.entity.mob.*;
import cn.nukkit.api.entity.passive.*;
import cn.nukkit.api.entity.projectile.ArrowEntity;
import cn.nukkit.api.entity.projectile.EggEntity;
import cn.nukkit.api.entity.projectile.EnderPearlEntity;
import cn.nukkit.api.entity.projectile.SnowballEntity;
import cn.nukkit.api.entity.weather.LightningEntity;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CreeperFace
 */

@Getter
public final class EntityType {

    public static final EntityType LIGHTNING = new EntityType("lightning", 93, LightningEntity.class);
    public static final EntityType ARROW = new EntityType("arrow", 80, ArrowEntity.class);
    public static final EntityType EGG = new EntityType("egg", 82, EggEntity.class);
    public static final EntityType ENDER_PEARL = new EntityType("ender_pearl", 87, EnderPearlEntity.class);
    public static final EntityType SNOWBALL = new EntityType("snowball", 81, SnowballEntity.class);
    public static final EntityType BLAZE = new EntityType("blaze", 43, BlazeEntity.class);
    public static final EntityType CAVE_SPIDER = new EntityType("cave_spider", 40, CaveSpiderEntity.class);
    public static final EntityType CREEPER = new EntityType("creeper", 33, CreeperEntity.class);
    public static final EntityType ELDER_GUARDIAN = new EntityType("elder_guardian", 50, ElderGuardianEntity.class);
    public static final EntityType ENDER_DRAGON = new EntityType("ender_dragon", 53, EnderDragonEntity.class);
    public static final EntityType ENDERMAN = new EntityType("enderman", 38, EndermanEntity.class);
    public static final EntityType ENDERMITE = new EntityType("endermite", 55, EndermiteEntity.class);
    public static final EntityType GHAST = new EntityType("ghast", 41, GhastEntity.class);
    public static final EntityType GUARDIAN = new EntityType("guardian", 49, GuardianEntity.class);
    public static final EntityType HUSK = new EntityType("husk", 47, HuskEntity.class);
    public static final EntityType MAGMA_CUBE = new EntityType("magma_cube", 42, MagmaCubeEntity.class);
    public static final EntityType SHULKER = new EntityType("shulker", 54, ShulkerEntity.class);
    public static final EntityType SILVERFISH = new EntityType("silverfish", 39, SilverfishEntity.class);
    public static final EntityType SKELETON = new EntityType("skeleton", 34, SkeletonEntity.class);
    public static final EntityType SLIME = new EntityType("slime", 37, SlimeEntity.class);
    public static final EntityType SPIDER = new EntityType("spider", 35, SpiderEntity.class);
    public static final EntityType STRAY = new EntityType("stray", 46, StrayEntity.class);
    public static final EntityType WITCH = new EntityType("witch", 45, WitchEntity.class);
    public static final EntityType WITHER = new EntityType("wither", 52, WitherEntity.class);
    public static final EntityType WITHER_SKELETON = new EntityType("wither_skeleton", 48, WitherSkeletonEntity.class);
    public static final EntityType ZOMBIE = new EntityType("zombie", 32, ZombieEntity.class);
    public static final EntityType ZOMBIE_PIGMAN = new EntityType("zombie_pigman", 36, ZombiePigmanEntity.class);
    public static final EntityType ZOMBIE_VILLAGER = new EntityType("zombie_villager", 47, ZombieVillager.class);
    public static final EntityType BAT = new EntityType("bat", 19, BatEntity.class);
    public static final EntityType CHICKEN = new EntityType("chicken", 10, ChickenEntity.class);
    public static final EntityType COW = new EntityType("cow", 11, CowEntity.class);
    public static final EntityType DONKEY = new EntityType("donkey", 24, DonkeyEntity.class);
    public static final EntityType HORSE = new EntityType("horse", 23, HorseEntity.class);
    public static final EntityType LLAMA = new EntityType("llama", 29, LlamaEntity.class);
    public static final EntityType MOOSHROOM = new EntityType("mooshroom", 16, MooshroomEntity.class);
    public static final EntityType MULE = new EntityType("mule", 25, MuleEntity.class);
    public static final EntityType OCELOT = new EntityType("ocelot", 22, OcelotEntity.class);
    public static final EntityType PIG = new EntityType("pig", 12, PigEntity.class);
    public static final EntityType POLAR_BEAR = new EntityType("polar_bear", 28, PolarBeerEntity.class);
    public static final EntityType RABBIT = new EntityType("rabbit", 18, RabbitEntity.class);
    public static final EntityType SHEEP = new EntityType("sheep", 13, SheepEntity.class);
    public static final EntityType SKELETON_HORSE = new EntityType("skeleton_horse", 26, SkeletonHorseEntity.class);
    public static final EntityType SQUID = new EntityType("squid", 17, SquidEntity.class);
    public static final EntityType VILLAGER = new EntityType("villager", 15, VillagerEntity.class);
    public static final EntityType WOLF = new EntityType("wolf", 14, WolfEntity.class);
    public static final EntityType ZOMBIE_HORSE = new EntityType("zombie_horse", 27, ZombieHorseEntity.class);
    public static final EntityType BOAT = new EntityType("boat", 90, BoatEntity.class);
    public static final EntityType EXPERIENCE_BOTTLE = new EntityType("experience_bottle", 68, ExpBottleEntity.class);
    public static final EntityType FALLING_BLOCK = new EntityType("falling_block", 66, FallingBlockEntity.class);
    public static final EntityType ITEM = new EntityType("item", 64, ItemEntity.class);
    public static final EntityType MINECART = new EntityType("minecart", 84, RideableMinecartEntity.class);
    public static final EntityType MINECART_HOPPER = new EntityType("hopper_minecart", 96, HopperMinecartEntity.class);
    public static final EntityType MINECART_CHEST = new EntityType("chest_minecart", 98, StorageMinecartEntity.class);
    public static final EntityType MINECART_COMMAND = new EntityType("command_block_minecart", 100, CommandBlockMinecart.class);
    public static final EntityType MINECART_TNT = new EntityType("tnt_minecart", 97, ExplosiveMinecartEntity.class);
    public static final EntityType PAINTING = new EntityType("painting", 83, PaintingEntity.class);
    public static final EntityType THROWN_POTION = new EntityType("splash_potion", 86, ThrownPotionEntity.class);
    public static final EntityType PRIMED_TNT = new EntityType("tnt", 65, PrimedTNTEntity.class);
    public static final EntityType EXPERIENCE_ORB = new EntityType("xp_orb", 69, ExperienceOrbEntity.class);


    private static final Map<Integer, EntityType> ID_MAP = new HashMap<>();
    private static final Map<String, EntityType> NAME_MAP = new HashMap<>();


    private final String name;
    private final int networkId;
    private final Class<? extends Entity> clazz;

    private boolean living;

    private EntityType(String name, int networkId, Class<? extends Entity> clazz) {
        this.name = name;
        this.networkId = networkId;
        this.clazz = clazz;

        ID_MAP.put(networkId, this);
        NAME_MAP.put(name, this);

        if (LivingEntity.class.isAssignableFrom(clazz)) {
            living = true;
        }
    }

    public static EntityType from(int id) {
        return ID_MAP.get(id);
    }

    public static EntityType from(String name) {
        return NAME_MAP.get(name);
    }
}

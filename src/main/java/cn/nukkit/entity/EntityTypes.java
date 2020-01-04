package cn.nukkit.entity;

import cn.nukkit.entity.hostile.*;
import cn.nukkit.entity.misc.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.vehicle.*;

public class EntityTypes {

    public static final EntityType<Chicken> CHICKEN = EntityType.of("chicken", Chicken.class);

    public static final EntityType<Cow> COW = EntityType.of("cow", Cow.class);

    public static final EntityType<Pig> PIG = EntityType.of("pig", Pig.class);

    public static final EntityType<Sheep> SHEEP = EntityType.of("sheep", Sheep.class);

    public static final EntityType<Wolf> WOLF = EntityType.of("wolf", Wolf.class);

    public static final EntityType<DeprecatedVillager> DEPRECATED_VILLAGER = EntityType.of("villager", DeprecatedVillager.class);

    public static final EntityType<Mooshroom> MOOSHROOM = EntityType.of("mooshroom", Mooshroom.class);

    public static final EntityType<Squid> SQUID = EntityType.of("squid", Squid.class);

    public static final EntityType<Rabbit> RABBIT = EntityType.of("rabbit", Rabbit.class);

    public static final EntityType<Bat> BAT = EntityType.of("bat", Bat.class);

    public static final EntityType<IronGolem> IRON_GOLEM = EntityType.of("iron_golem", IronGolem.class);

    public static final EntityType<SnowGolem> SNOW_GOLEM = EntityType.of("snow_golem", SnowGolem.class);

    public static final EntityType<Ocelot> OCELOT = EntityType.of("ocelot", Ocelot.class);

    public static final EntityType<Horse> HORSE = EntityType.of("horse", Horse.class);

    public static final EntityType<Donkey> DONKEY = EntityType.of("donkey", Donkey.class);

    public static final EntityType<Mule> MULE = EntityType.of("mule", Mule.class);

    public static final EntityType<SkeletonHorse> SKELETON_HORSE = EntityType.of("skeleton_horse", SkeletonHorse.class);

    public static final EntityType<ZombieHorse> ZOMBIE_HORSE = EntityType.of("zombie_horse", ZombieHorse.class);

    public static final EntityType<PolarBear> POLAR_BEAR = EntityType.of("polar_bear", PolarBear.class);

    public static final EntityType<Llama> LLAMA = EntityType.of("llama", Llama.class);

    public static final EntityType<Parrot> PARROT = EntityType.of("parrot", Parrot.class);

    public static final EntityType<Dolphin> DOLPHIN = EntityType.of("dolphin", Dolphin.class);

    public static final EntityType<Zombie> ZOMBIE = EntityType.of("zombie", Zombie.class);

    public static final EntityType<Creeper> CREEPER = EntityType.of("creeper", Creeper.class);

    public static final EntityType<Skeleton> SKELETON = EntityType.of("skeleton", Skeleton.class);

    public static final EntityType<Spider> SPIDER = EntityType.of("spider", Spider.class);

    public static final EntityType<ZombiePigman> ZOMBIE_PIGMAN = EntityType.of("zombie_pigman", ZombiePigman.class);

    public static final EntityType<Slime> SLIME = EntityType.of("slime", Slime.class);

    public static final EntityType<Enderman> ENDERMAN = EntityType.of("enderman", Enderman.class);

    public static final EntityType<Silverfish> SILVERFISH = EntityType.of("silverfish", Silverfish.class);

    public static final EntityType<CaveSpider> CAVE_SPIDER = EntityType.of("cave_spider", CaveSpider.class);

    public static final EntityType<Ghast> GHAST = EntityType.of("ghast", Ghast.class);

    public static final EntityType<MagmaCube> MAGMA_CUBE = EntityType.of("magma_cube", MagmaCube.class);

    public static final EntityType<Blaze> BLAZE = EntityType.of("blaze", Blaze.class);

    public static final EntityType<DeprecatedZombieVillager> DEPRECATED_ZOMBIE_VILLAGER = EntityType.of("zombie_villager", DeprecatedZombieVillager.class);

    public static final EntityType<Witch> WITCH = EntityType.of("witch", Witch.class);

    public static final EntityType<Stray> STRAY = EntityType.of("stray", Stray.class);

    public static final EntityType<Husk> HUSK = EntityType.of("husk", Husk.class);

    public static final EntityType<WitherSkeleton> WITHER_SKELETON = EntityType.of("wither_skeleton", WitherSkeleton.class);

    public static final EntityType<Guardian> GUARDIAN = EntityType.of("guardian", Guardian.class);

    public static final EntityType<ElderGuardian> ELDER_GUARDIAN = EntityType.of("elder_guardian", ElderGuardian.class);

    //public static final EntityType<Npc> NPC = EntityType.of("npc", Npc.class);

    public static final EntityType<Wither> WITHER = EntityType.of("wither", Wither.class);

    public static final EntityType<EnderDragon> ENDER_DRAGON = EntityType.of("ender_dragon", EnderDragon.class);

    public static final EntityType<Shulker> SHULKER = EntityType.of("shulker", Shulker.class);

    public static final EntityType<Endermite> ENDERMITE = EntityType.of("endermite", Endermite.class);

    //public static final EntityType AGENT = EntityType.of("agent");

    public static final EntityType<Vindicator> VINDICATOR = EntityType.of("vindicator", Vindicator.class);

    public static final EntityType<Phantom> PHANTOM = EntityType.of("phantom", Phantom.class);

    public static final EntityType<Ravager> RAVAGER = EntityType.of("ravager", Ravager.class);


    public static final EntityType<ArmorStand> ARMOR_STAND = EntityType.of("armor_stand", ArmorStand.class);

    //public static final EntityType TRIPOD_CAMERA = EntityType.of("tripod_camera");

    public static final EntityType<Human> PLAYER = EntityType.of("player", Human.class);

    public static final EntityType<DroppedItem> ITEM = EntityType.of("item", DroppedItem.class);

    public static final EntityType<Tnt> TNT = EntityType.of("tnt", Tnt.class);

    public static final EntityType<FallingBlock> FALLING_BLOCK = EntityType.of("falling_block", FallingBlock.class);

    //public static final EntityType MOVING_BLOCK = EntityType.of("moving_block");

    public static final EntityType<XpBottle> XP_BOTTLE = EntityType.of("xp_bottle", XpBottle.class);

    public static final EntityType<XpOrb> XP_ORB = EntityType.of("xp_orb", XpOrb.class);

    public static final EntityType<EyeOfEnderSignal> EYE_OF_ENDER_SIGNAL = EntityType.of("eye_of_ender_signal", EyeOfEnderSignal.class);

    public static final EntityType<EnderCrystal> ENDER_CRYSTAL = EntityType.of("ender_crystal", EnderCrystal.class);

    public static final EntityType<FireworksRocket> FIREWORKS_ROCKET = EntityType.of("fireworks_rocket", FireworksRocket.class);

    public static final EntityType<ThrownTrident> THROWN_TRIDENT = EntityType.of("thrown_trident", ThrownTrident.class);

    public static final EntityType<Turtle> TURTLE = EntityType.of("turtle", Turtle.class);

    public static final EntityType<Cat> CAT = EntityType.of("cat", Cat.class);

    public static final EntityType<ShulkerBullet> SHULKER_BULLET = EntityType.of("shulker_bullet", ShulkerBullet.class);

    public static final EntityType<FishingHook> FISHING_HOOK = EntityType.of("fishing_hook", FishingHook.class);

    //public static final EntityType CHALKBOARD = EntityType.of("chalkboard");

    public static final EntityType<DragonFireball> DRAGON_FIREBALL = EntityType.of("dragon_fireball", DragonFireball.class);

    public static final EntityType<Arrow> ARROW = EntityType.of("arrow", Arrow.class);

    public static final EntityType<Snowball> SNOWBALL = EntityType.of("snowball", Snowball.class);

    public static final EntityType<Egg> EGG = EntityType.of("egg", Egg.class);

    public static final EntityType<Painting> PAINTING = EntityType.of("painting", Painting.class);

    public static final EntityType<Minecart> MINECART = EntityType.of("minecart", Minecart.class);

    public static final EntityType<Fireball> FIREBALL = EntityType.of("fireball", Fireball.class);

    public static final EntityType<SplashPotion> SPLASH_POTION = EntityType.of("splash_potion", SplashPotion.class);

    public static final EntityType<EnderPearl> ENDER_PEARL = EntityType.of("ender_pearl", EnderPearl.class);

    public static final EntityType<LeashKnot> LEASH_KNOT = EntityType.of("leash_knot", LeashKnot.class);

    public static final EntityType<WitherSkull> WITHER_SKULL = EntityType.of("wither_skull", WitherSkull.class);

    public static final EntityType<Boat> BOAT = EntityType.of("boat", Boat.class);

    public static final EntityType<WitherSkull> WITHER_SKULL_DANGEROUS = EntityType.of("wither_skull_dangerous", WitherSkull.class);


    public static final EntityType<LightningBolt> LIGHTNING_BOLT = EntityType.of("lightning_bolt", LightningBolt.class);

    public static final EntityType<SmallFireball> SMALL_FIREBALL = EntityType.of("small_fireball", SmallFireball.class);

    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = EntityType.of("area_effect_cloud", AreaEffectCloud.class);

    public static final EntityType<HopperMinecart> HOPPER_MINECART = EntityType.of("hopper_minecart", HopperMinecart.class);

    public static final EntityType<TntMinecart> TNT_MINECART = EntityType.of("tnt_minecart", TntMinecart.class);

    public static final EntityType<ChestMinecart> CHEST_MINECART = EntityType.of("chest_minecart", ChestMinecart.class);


    public static final EntityType<CommandBlockMinecart> COMMAND_BLOCK_MINECART = EntityType.of("command_block_minecart", CommandBlockMinecart.class);

    public static final EntityType<LingeringPotion> LINGERING_POTION = EntityType.of("lingering_potion", LingeringPotion.class);

    public static final EntityType<LlamaSpit> LLAMA_SPIT = EntityType.of("llama_spit", LlamaSpit.class);

    public static final EntityType<EvocationFang> EVOCATION_FANG = EntityType.of("evocation_fang", EvocationFang.class);

    public static final EntityType<EvocationIllager> EVOCATION_ILLAGER = EntityType.of("evocation_illager", EvocationIllager.class);

    public static final EntityType<Vex> VEX = EntityType.of("vex", Vex.class);

    //public static final EntityType ICE_BOMB = EntityType.of("ice_bomb");

    //public static final EntityType BALLOON = EntityType.of("balloon");

    public static final EntityType<Pufferfish> PUFFERFISH = EntityType.of("pufferfish", Pufferfish.class);

    public static final EntityType<Salmon> SALMON = EntityType.of("salmon", Salmon.class);

    public static final EntityType<Drowned> DROWNED = EntityType.of("drowned", Drowned.class);

    public static final EntityType<TropicalFish> TROPICALFISH = EntityType.of("tropicalfish", TropicalFish.class);

    public static final EntityType<Cod> COD = EntityType.of("cod", Cod.class);

    public static final EntityType<Panda> PANDA = EntityType.of("panda", Panda.class);

    public static final EntityType<Pillager> PILLAGER = EntityType.of("pillager", Pillager.class);

    public static final EntityType<Villager> VILLAGER = EntityType.of("villager_v2", Villager.class);

    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = EntityType.of("zombie_villager_v2", ZombieVillager.class);

    public static final EntityType<WanderingTrader> WANDERING_TRADER = EntityType.of("wandering_trader", WanderingTrader.class);


    private EntityTypes() {
        throw new IllegalStateException();
    }
}

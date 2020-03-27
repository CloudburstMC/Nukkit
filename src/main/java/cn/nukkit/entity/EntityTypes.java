package cn.nukkit.entity;

import cn.nukkit.entity.hostile.*;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.entity.misc.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.vehicle.*;

public class EntityTypes {

    public static final EntityType<Chicken> CHICKEN = EntityType.from("chicken", Chicken.class);

    public static final EntityType<Cow> COW = EntityType.from("cow", Cow.class);

    public static final EntityType<Pig> PIG = EntityType.from("pig", Pig.class);

    public static final EntityType<Sheep> SHEEP = EntityType.from("sheep", Sheep.class);

    public static final EntityType<Wolf> WOLF = EntityType.from("wolf", Wolf.class);

    public static final EntityType<DeprecatedVillager> DEPRECATED_VILLAGER = EntityType.from("villager", DeprecatedVillager.class);

    public static final EntityType<Mooshroom> MOOSHROOM = EntityType.from("mooshroom", Mooshroom.class);

    public static final EntityType<Squid> SQUID = EntityType.from("squid", Squid.class);

    public static final EntityType<Rabbit> RABBIT = EntityType.from("rabbit", Rabbit.class);

    public static final EntityType<Bat> BAT = EntityType.from("bat", Bat.class);

    public static final EntityType<IronGolem> IRON_GOLEM = EntityType.from("iron_golem", IronGolem.class);

    public static final EntityType<SnowGolem> SNOW_GOLEM = EntityType.from("snow_golem", SnowGolem.class);

    public static final EntityType<Ocelot> OCELOT = EntityType.from("ocelot", Ocelot.class);

    public static final EntityType<Horse> HORSE = EntityType.from("horse", Horse.class);

    public static final EntityType<Donkey> DONKEY = EntityType.from("donkey", Donkey.class);

    public static final EntityType<Mule> MULE = EntityType.from("mule", Mule.class);

    public static final EntityType<SkeletonHorse> SKELETON_HORSE = EntityType.from("skeleton_horse", SkeletonHorse.class);

    public static final EntityType<ZombieHorse> ZOMBIE_HORSE = EntityType.from("zombie_horse", ZombieHorse.class);

    public static final EntityType<PolarBear> POLAR_BEAR = EntityType.from("polar_bear", PolarBear.class);

    public static final EntityType<Llama> LLAMA = EntityType.from("llama", Llama.class);

    public static final EntityType<Parrot> PARROT = EntityType.from("parrot", Parrot.class);

    public static final EntityType<Dolphin> DOLPHIN = EntityType.from("dolphin", Dolphin.class);

    public static final EntityType<Zombie> ZOMBIE = EntityType.from("zombie", Zombie.class);

    public static final EntityType<Creeper> CREEPER = EntityType.from("creeper", Creeper.class);

    public static final EntityType<Skeleton> SKELETON = EntityType.from("skeleton", Skeleton.class);

    public static final EntityType<Spider> SPIDER = EntityType.from("spider", Spider.class);

    public static final EntityType<ZombiePigman> ZOMBIE_PIGMAN = EntityType.from("zombie_pigman", ZombiePigman.class);

    public static final EntityType<Slime> SLIME = EntityType.from("slime", Slime.class);

    public static final EntityType<Enderman> ENDERMAN = EntityType.from("enderman", Enderman.class);

    public static final EntityType<Silverfish> SILVERFISH = EntityType.from("silverfish", Silverfish.class);

    public static final EntityType<CaveSpider> CAVE_SPIDER = EntityType.from("cave_spider", CaveSpider.class);

    public static final EntityType<Ghast> GHAST = EntityType.from("ghast", Ghast.class);

    public static final EntityType<MagmaCube> MAGMA_CUBE = EntityType.from("magma_cube", MagmaCube.class);

    public static final EntityType<Blaze> BLAZE = EntityType.from("blaze", Blaze.class);

    public static final EntityType<DeprecatedZombieVillager> DEPRECATED_ZOMBIE_VILLAGER = EntityType.from("zombie_villager", DeprecatedZombieVillager.class);

    public static final EntityType<Witch> WITCH = EntityType.from("witch", Witch.class);

    public static final EntityType<Stray> STRAY = EntityType.from("stray", Stray.class);

    public static final EntityType<Husk> HUSK = EntityType.from("husk", Husk.class);

    public static final EntityType<WitherSkeleton> WITHER_SKELETON = EntityType.from("wither_skeleton", WitherSkeleton.class);

    public static final EntityType<Guardian> GUARDIAN = EntityType.from("guardian", Guardian.class);

    public static final EntityType<ElderGuardian> ELDER_GUARDIAN = EntityType.from("elder_guardian", ElderGuardian.class);

    //public static final EntityType<Npc> NPC = EntityType.from("npc", Npc.class);

    public static final EntityType<Wither> WITHER = EntityType.from("wither", Wither.class);

    public static final EntityType<EnderDragon> ENDER_DRAGON = EntityType.from("ender_dragon", EnderDragon.class);

    public static final EntityType<Shulker> SHULKER = EntityType.from("shulker", Shulker.class);

    public static final EntityType<Endermite> ENDERMITE = EntityType.from("endermite", Endermite.class);

    //public static final EntityType AGENT = EntityType.from("agent");

    public static final EntityType<Vindicator> VINDICATOR = EntityType.from("vindicator", Vindicator.class);

    public static final EntityType<Phantom> PHANTOM = EntityType.from("phantom", Phantom.class);

    public static final EntityType<Ravager> RAVAGER = EntityType.from("ravager", Ravager.class);


    public static final EntityType<ArmorStand> ARMOR_STAND = EntityType.from("armor_stand", ArmorStand.class);

    //public static final EntityType TRIPOD_CAMERA = EntityType.from("tripod_camera");

    public static final EntityType<Human> PLAYER = EntityType.from("player", Human.class);

    public static final EntityType<DroppedItem> ITEM = EntityType.from("item", DroppedItem.class);

    public static final EntityType<PrimedTnt> TNT = EntityType.from("tnt", PrimedTnt.class);

    public static final EntityType<FallingBlock> FALLING_BLOCK = EntityType.from("falling_block", FallingBlock.class);

    //public static final EntityType MOVING_BLOCK = EntityType.from("moving_block");

    public static final EntityType<XpBottle> XP_BOTTLE = EntityType.from("xp_bottle", XpBottle.class);

    public static final EntityType<ExperienceOrb> XP_ORB = EntityType.from("xp_orb", ExperienceOrb.class);

    public static final EntityType<EyeOfEnderSignal> EYE_OF_ENDER_SIGNAL = EntityType.from("eye_of_ender_signal", EyeOfEnderSignal.class);

    public static final EntityType<EnderCrystal> ENDER_CRYSTAL = EntityType.from("ender_crystal", EnderCrystal.class);

    public static final EntityType<FireworksRocket> FIREWORKS_ROCKET = EntityType.from("fireworks_rocket", FireworksRocket.class);

    public static final EntityType<ThrownTrident> THROWN_TRIDENT = EntityType.from("thrown_trident", ThrownTrident.class);

    public static final EntityType<Turtle> TURTLE = EntityType.from("turtle", Turtle.class);

    public static final EntityType<Cat> CAT = EntityType.from("cat", Cat.class);

    public static final EntityType<ShulkerBullet> SHULKER_BULLET = EntityType.from("shulker_bullet", ShulkerBullet.class);

    public static final EntityType<FishingHook> FISHING_HOOK = EntityType.from("fishing_hook", FishingHook.class);

    //public static final EntityType CHALKBOARD = EntityType.from("chalkboard");

    public static final EntityType<DragonFireball> DRAGON_FIREBALL = EntityType.from("dragon_fireball", DragonFireball.class);

    public static final EntityType<Arrow> ARROW = EntityType.from("arrow", Arrow.class);

    public static final EntityType<Snowball> SNOWBALL = EntityType.from("snowball", Snowball.class);

    public static final EntityType<Egg> EGG = EntityType.from("egg", Egg.class);

    public static final EntityType<Painting> PAINTING = EntityType.from("painting", Painting.class);

    public static final EntityType<Minecart> MINECART = EntityType.from("minecart", Minecart.class);

    public static final EntityType<Fireball> FIREBALL = EntityType.from("fireball", Fireball.class);

    public static final EntityType<SplashPotion> SPLASH_POTION = EntityType.from("splash_potion", SplashPotion.class);

    public static final EntityType<EnderPearl> ENDER_PEARL = EntityType.from("ender_pearl", EnderPearl.class);

    public static final EntityType<LeashKnot> LEASH_KNOT = EntityType.from("leash_knot", LeashKnot.class);

    public static final EntityType<WitherSkull> WITHER_SKULL = EntityType.from("wither_skull", WitherSkull.class);

    public static final EntityType<Boat> BOAT = EntityType.from("boat", Boat.class);

    public static final EntityType<WitherSkull> WITHER_SKULL_DANGEROUS = EntityType.from("wither_skull_dangerous", WitherSkull.class);


    public static final EntityType<LightningBolt> LIGHTNING_BOLT = EntityType.from("lightning_bolt", LightningBolt.class);

    public static final EntityType<SmallFireball> SMALL_FIREBALL = EntityType.from("small_fireball", SmallFireball.class);

    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = EntityType.from("area_effect_cloud", AreaEffectCloud.class);

    public static final EntityType<HopperMinecart> HOPPER_MINECART = EntityType.from("hopper_minecart", HopperMinecart.class);

    public static final EntityType<TntMinecart> TNT_MINECART = EntityType.from("tnt_minecart", TntMinecart.class);

    public static final EntityType<ChestMinecart> CHEST_MINECART = EntityType.from("chest_minecart", ChestMinecart.class);


    public static final EntityType<CommandBlockMinecart> COMMAND_BLOCK_MINECART = EntityType.from("command_block_minecart", CommandBlockMinecart.class);

    public static final EntityType<LingeringPotion> LINGERING_POTION = EntityType.from("lingering_potion", LingeringPotion.class);

    public static final EntityType<LlamaSpit> LLAMA_SPIT = EntityType.from("llama_spit", LlamaSpit.class);

    public static final EntityType<EvocationFang> EVOCATION_FANG = EntityType.from("evocation_fang", EvocationFang.class);

    public static final EntityType<EvocationIllager> EVOCATION_ILLAGER = EntityType.from("evocation_illager", EvocationIllager.class);

    public static final EntityType<Vex> VEX = EntityType.from("vex", Vex.class);

    //public static final EntityType ICE_BOMB = EntityType.from("ice_bomb");

    //public static final EntityType BALLOON = EntityType.from("balloon");

    public static final EntityType<Pufferfish> PUFFERFISH = EntityType.from("pufferfish", Pufferfish.class);

    public static final EntityType<Salmon> SALMON = EntityType.from("salmon", Salmon.class);

    public static final EntityType<Drowned> DROWNED = EntityType.from("drowned", Drowned.class);

    public static final EntityType<TropicalFish> TROPICALFISH = EntityType.from("tropicalfish", TropicalFish.class);

    public static final EntityType<Cod> COD = EntityType.from("cod", Cod.class);

    public static final EntityType<Panda> PANDA = EntityType.from("panda", Panda.class);

    public static final EntityType<Pillager> PILLAGER = EntityType.from("pillager", Pillager.class);

    public static final EntityType<Villager> VILLAGER = EntityType.from("villager_v2", Villager.class);

    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = EntityType.from("zombie_villager_v2", ZombieVillager.class);

    public static final EntityType<WanderingTrader> WANDERING_TRADER = EntityType.from("wandering_trader", WanderingTrader.class);


    private EntityTypes() {
        throw new IllegalStateException();
    }
}

package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Fuel;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Binary;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Item implements Cloneable {

    private static CompoundTag parseCompoundTag(byte[] tag) {
        try {
            return NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] writeCompoundTag(CompoundTag tag) {
        try {
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //All Block IDs are here too
    public static final int AIR = 0;
    public static final int STONE = 1;
    public static final int GRASS = 2;
    public static final int DIRT = 3;
    public static final int COBBLESTONE = 4;
    public static final int COBBLE = 4;
    public static final int PLANK = 5;
    public static final int PLANKS = 5;
    public static final int WOODEN_PLANK = 5;
    public static final int WOODEN_PLANKS = 5;
    public static final int SAPLING = 6;
    public static final int SAPLINGS = 6;
    public static final int BEDROCK = 7;
    public static final int WATER = 8;
    public static final int STILL_WATER = 9;
    public static final int LAVA = 10;
    public static final int STILL_LAVA = 11;
    public static final int SAND = 12;
    public static final int GRAVEL = 13;
    public static final int GOLD_ORE = 14;
    public static final int IRON_ORE = 15;
    public static final int COAL_ORE = 16;
    public static final int LOG = 17;
    public static final int WOOD = 17;
    public static final int TRUNK = 17;
    public static final int LEAVES = 18;
    public static final int LEAVE = 18;
    public static final int SPONGE = 19;
    public static final int GLASS = 20;
    public static final int LAPIS_ORE = 21;
    public static final int LAPIS_BLOCK = 22;

    public static final int DISPENSER = 23;

    public static final int SANDSTONE = 24;
    public static final int NOTEBLOCK = 25;

    public static final int BED_BLOCK = 26;
    public static final int POWERED_RAIL = 27;
    public static final int DETECTOR_RAIL = 28;

    public static final int COBWEB = 30;
    public static final int TALL_GRASS = 31;
    public static final int BUSH = 32;
    public static final int DEAD_BUSH = 32;
    public static final int WOOL = 35;
    public static final int DANDELION = 37;
    public static final int POPPY = 38;
    public static final int ROSE = 38;
    public static final int FLOWER = 38;
    public static final int RED_FLOWER = 38;
    public static final int BROWN_MUSHROOM = 39;
    public static final int RED_MUSHROOM = 40;
    public static final int GOLD_BLOCK = 41;
    public static final int IRON_BLOCK = 42;
    public static final int DOUBLE_SLAB = 43;
    public static final int DOUBLE_SLABS = 43;
    public static final int SLAB = 44;
    public static final int SLABS = 44;
    public static final int BRICKS = 45;
    public static final int BRICKS_BLOCK = 45;
    public static final int TNT = 46;
    public static final int BOOKSHELF = 47;
    public static final int MOSS_STONE = 48;
    public static final int MOSSY_STONE = 48;
    public static final int OBSIDIAN = 49;
    public static final int TORCH = 50;
    public static final int FIRE = 51;
    public static final int MONSTER_SPAWNER = 52;
    public static final int WOOD_STAIRS = 53;
    public static final int WOODEN_STAIRS = 53;
    public static final int OAK_WOOD_STAIRS = 53;
    public static final int OAK_WOODEN_STAIRS = 53;
    public static final int CHEST = 54;
    public static final int REDSTONE_WIRE = 55;
    public static final int DIAMOND_ORE = 56;
    public static final int DIAMOND_BLOCK = 57;
    public static final int CRAFTING_TABLE = 58;
    public static final int WORKBENCH = 58;
    public static final int WHEAT_BLOCK = 59;
    public static final int FARMLAND = 60;
    public static final int FURNACE = 61;
    public static final int BURNING_FURNACE = 62;
    public static final int LIT_FURNACE = 62;
    public static final int SIGN_POST = 63;
    public static final int DOOR_BLOCK = 64;
    public static final int WOODEN_DOOR_BLOCK = 64;
    public static final int WOOD_DOOR_BLOCK = 64;
    public static final int LADDER = 65;

    public static final int RAIL = 66;

    public static final int COBBLE_STAIRS = 67;
    public static final int COBBLESTONE_STAIRS = 67;
    public static final int WALL_SIGN = 68;
    public static final int LEVER = 69;
    public static final int STONE_PRESSURE_PLATE = 70;
    public static final int IRON_DOOR_BLOCK = 71;
    public static final int WOODEN_PRESSURE_PLATE = 72;
    public static final int REDSTONE_ORE = 73;
    public static final int GLOWING_REDSTONE_ORE = 74;
    public static final int LIT_REDSTONE_ORE = 74;

    public static final int REDSTONE_TORCH = 76;
    public static final int STONE_BUTTON = 77;

    public static final int SNOW = 78;
    public static final int SNOW_LAYER = 78;
    public static final int ICE = 79;
    public static final int SNOW_BLOCK = 80;
    public static final int CACTUS = 81;
    public static final int CLAY_BLOCK = 82;
    public static final int REEDS = 83;
    public static final int SUGARCANE_BLOCK = 83;

    public static final int FENCE = 85;
    public static final int PUMPKIN = 86;
    public static final int NETHERRACK = 87;
    public static final int SOUL_SAND = 88;
    public static final int GLOWSTONE = 89;
    public static final int GLOWSTONE_BLOCK = 89;
    public static final int NETHER_PORTAL = 90;
    public static final int LIT_PUMPKIN = 91;
    public static final int JACK_O_LANTERN = 91;
    public static final int CAKE_BLOCK = 92;

    public static final int INVISIBLE_BEDROCK = 95;
    public static final int TRAPDOOR = 96;

    public static final int STONE_BRICKS = 98;
    public static final int STONE_BRICK = 98;

    public static final int BROWN_MUSHROOM_BLOCK = 99;
    public static final int RED_MUSHROOM_BLOCK = 100;

    public static final int IRON_BAR = 101;
    public static final int IRON_BARS = 101;
    public static final int GLASS_PANE = 102;
    public static final int GLASS_PANEL = 102;
    public static final int MELON_BLOCK = 103;
    public static final int PUMPKIN_STEM = 104;
    public static final int MELON_STEM = 105;
    public static final int VINE = 106;
    public static final int VINES = 106;
    public static final int FENCE_GATE = 107;
    public static final int BRICK_STAIRS = 108;
    public static final int STONE_BRICK_STAIRS = 109;
    public static final int MYCELIUM = 110;
    public static final int WATER_LILY = 111;
    public static final int LILY_PAD = 111;
    public static final int NETHER_BRICKS = 112;
    public static final int NETHER_BRICK_BLOCK = 112;
    public static final int NETHER_BRICK_FENCE = 113;
    public static final int NETHER_BRICKS_STAIRS = 114;

    public static final int ENCHANTING_TABLE = 116;
    public static final int ENCHANT_TABLE = 116;
    public static final int ENCHANTMENT_TABLE = 116;
    public static final int BREWING_STAND_BLOCK = 117;
    public static final int BREWING_BLOCK = 117;
    public static final int CAULDRON_BLOCK = 118;
    public static final int END_PORTAL = 119;
    public static final int END_PORTAL_FRAME = 120;
    public static final int END_STONE = 121;

    public static final int REDSTONE_LAMP = 123;
    public static final int LIT_REDSTONE_LAMP = 124;

    public static final int ACTIVATOR_RAIL = 126;

    public static final int SANDSTONE_STAIRS = 128;
    public static final int EMERALD_ORE = 129;

    public static final int TRIPWIRE_HOOK = 131;

    public static final int EMERALD_BLOCK = 133;
    public static final int SPRUCE_WOOD_STAIRS = 134;
    public static final int SPRUCE_WOODEN_STAIRS = 134;
    public static final int BIRCH_WOOD_STAIRS = 135;
    public static final int BIRCH_WOODEN_STAIRS = 135;
    public static final int JUNGLE_WOOD_STAIRS = 136;
    public static final int JUNGLE_WOODEN_STAIRS = 136;

    public static final int COBBLE_WALL = 139;
    public static final int STONE_WALL = 139;
    public static final int COBBLESTONE_WALL = 139;
    public static final int FLOWER_POT_BLOCK = 140;
    public static final int CARROT_BLOCK = 141;
    public static final int POTATO_BLOCK = 142;

    public static final int WOODEN_BUTTON = 143;

    public static final int SKULL_BLOCK = 144;
    public static final int ANVIL = 145;
    public static final int TRAPPED_CHEST = 146;
    public static final int LIGHT_WEIGHTED_PRESSURE_PLATE = 147;
    public static final int HEAVY_WEIGHTED_PRESSURE_PLATE = 148;

    public static final int UNPOWERED_COMPARATOR = 149;
    public static final int POWERED_COMPARATOR = 150;
    public static final int DAYLIGHT_DETECTOR = 151;
    public static final int REDSTONE_BLOCK = 152;
    public static final int QUARTZ_ORE = 153;

    public static final int HOPPER_BLOCK = 154;

    public static final int QUARTZ_BLOCK = 155;
    public static final int QUARTZ_STAIRS = 156;
    public static final int DOUBLE_WOOD_SLAB = 157;
    public static final int DOUBLE_WOODEN_SLAB = 157;
    public static final int DOUBLE_WOOD_SLABS = 157;
    public static final int DOUBLE_WOODEN_SLABS = 157;
    public static final int WOOD_SLAB = 158;
    public static final int WOODEN_SLAB = 158;
    public static final int WOOD_SLABS = 158;
    public static final int WOODEN_SLABS = 158;
    public static final int STAINED_CLAY = 159;
    public static final int STAINED_HARDENED_CLAY = 159;

    public static final int LEAVES2 = 161;
    public static final int LEAVE2 = 161;
    public static final int WOOD2 = 162;
    public static final int TRUNK2 = 162;
    public static final int LOG2 = 162;
    public static final int ACACIA_WOOD_STAIRS = 163;
    public static final int ACACIA_WOODEN_STAIRS = 163;
    public static final int DARK_OAK_WOOD_STAIRS = 164;
    public static final int DARK_OAK_WOODEN_STAIRS = 164;

    public static final int SLIME_BLOCK = 165;

    public static final int IRON_TRAPDOOR = 167;
    public static final int HAY_BALE = 170;
    public static final int CARPET = 171;
    public static final int HARDENED_CLAY = 172;
    public static final int COAL_BLOCK = 173;
    public static final int PACKED_ICE = 174;
    public static final int DOUBLE_PLANT = 175;

    public static final int DAYLIGHT_DETECTOR_INVERTED = 178;

    public static final int FENCE_GATE_SPRUCE = 183;
    public static final int FENCE_GATE_BIRCH = 184;
    public static final int FENCE_GATE_JUNGLE = 185;
    public static final int FENCE_GATE_DARK_OAK = 186;
    public static final int FENCE_GATE_ACACIA = 187;

    public static final int SPRUCE_DOOR_BLOCK = 193;
    public static final int BIRCH_DOOR_BLOCK = 194;
    public static final int JUNGLE_DOOR_BLOCK = 195;
    public static final int ACACIA_DOOR_BLOCK = 196;
    public static final int DARK_OAK_DOOR_BLOCK = 197;

    public static final int GRASS_PATH = 198;

    public static final int PODZOL = 243;
    public static final int BEETROOT_BLOCK = 244;
    public static final int STONECUTTER = 245;
    public static final int GLOWING_OBSIDIAN = 246;


    //Normal Item IDs

    public static final int IRON_SHOVEL = 256; //
    public static final int IRON_PICKAXE = 257; //
    public static final int IRON_AXE = 258; //
    public static final int FLINT_STEEL = 259; //
    public static final int FLINT_AND_STEEL = 259; //
    public static final int APPLE = 260; //
    public static final int BOW = 261;
    public static final int ARROW = 262;
    public static final int COAL = 263; //
    public static final int DIAMOND = 264; //
    public static final int IRON_INGOT = 265; //
    public static final int GOLD_INGOT = 266; //
    public static final int IRON_SWORD = 267;
    public static final int WOODEN_SWORD = 268; //
    public static final int WOODEN_SHOVEL = 269; //
    public static final int WOODEN_PICKAXE = 270; //
    public static final int WOODEN_AXE = 271; //
    public static final int STONE_SWORD = 272;
    public static final int STONE_SHOVEL = 273;
    public static final int STONE_PICKAXE = 274;
    public static final int STONE_AXE = 275;
    public static final int DIAMOND_SWORD = 276;
    public static final int DIAMOND_SHOVEL = 277;
    public static final int DIAMOND_PICKAXE = 278;
    public static final int DIAMOND_AXE = 279;
    public static final int STICK = 280; //
    public static final int STICKS = 280;
    public static final int BOWL = 281; //
    public static final int MUSHROOM_STEW = 282;
    public static final int GOLD_SWORD = 283;
    public static final int GOLD_SHOVEL = 284;
    public static final int GOLD_PICKAXE = 285;
    public static final int GOLD_AXE = 286;
    public static final int GOLDEN_SWORD = 283;
    public static final int GOLDEN_SHOVEL = 284;
    public static final int GOLDEN_PICKAXE = 285;
    public static final int GOLDEN_AXE = 286;
    public static final int STRING = 287;
    public static final int FEATHER = 288; //
    public static final int GUNPOWDER = 289;
    public static final int WOODEN_HOE = 290;
    public static final int STONE_HOE = 291;
    public static final int IRON_HOE = 292; //
    public static final int DIAMOND_HOE = 293;
    public static final int GOLD_HOE = 294;
    public static final int GOLDEN_HOE = 294;
    public static final int SEEDS = 295;
    public static final int WHEAT_SEEDS = 295;
    public static final int WHEAT = 296;
    public static final int BREAD = 297;
    public static final int LEATHER_CAP = 298;
    public static final int LEATHER_TUNIC = 299;
    public static final int LEATHER_PANTS = 300;
    public static final int LEATHER_BOOTS = 301;
    public static final int CHAIN_HELMET = 302;
    public static final int CHAIN_CHESTPLATE = 303;
    public static final int CHAIN_LEGGINGS = 304;
    public static final int CHAIN_BOOTS = 305;
    public static final int IRON_HELMET = 306;
    public static final int IRON_CHESTPLATE = 307;
    public static final int IRON_LEGGINGS = 308;
    public static final int IRON_BOOTS = 309;
    public static final int DIAMOND_HELMET = 310;
    public static final int DIAMOND_CHESTPLATE = 311;
    public static final int DIAMOND_LEGGINGS = 312;
    public static final int DIAMOND_BOOTS = 313;
    public static final int GOLD_HELMET = 314;
    public static final int GOLD_CHESTPLATE = 315;
    public static final int GOLD_LEGGINGS = 316;
    public static final int GOLD_BOOTS = 317;
    public static final int FLINT = 318;
    public static final int RAW_PORKCHOP = 319;
    public static final int COOKED_PORKCHOP = 320;
    public static final int PAINTING = 321;
    public static final int GOLDEN_APPLE = 322;
    public static final int SIGN = 323;
    public static final int WOODEN_DOOR = 324;
    public static final int BUCKET = 325;

    public static final int MINECART = 328;

    public static final int IRON_DOOR = 330;
    public static final int REDSTONE = 331;
    public static final int REDSTONE_DUST = 331;
    public static final int SNOWBALL = 332;
    public static final int BOAT = 333;
    public static final int LEATHER = 334;

    public static final int BRICK = 336;
    public static final int CLAY = 337;
    public static final int SUGARCANE = 338;
    public static final int SUGAR_CANE = 338;
    public static final int SUGAR_CANES = 338;
    public static final int PAPER = 339;
    public static final int BOOK = 340;
    public static final int SLIMEBALL = 341;

    public static final int MINECART_WITH_CHEST = 342;

    public static final int EGG = 344;
    public static final int COMPASS = 345;
    public static final int FISHING_ROD = 346;

    public static final int CLOCK = 347;
    public static final int GLOWSTONE_DUST = 348;
    public static final int RAW_FISH = 349;
    public static final int COOKED_FISH = 350;
    public static final int DYE = 351;
    public static final int BONE = 352;
    public static final int SUGAR = 353;
    public static final int CAKE = 354;
    public static final int BED = 355;

    public static final int REPEATER = 356;

    public static final int COOKIE = 357;

    public static final int MAP = 358;

    public static final int SHEARS = 359;
    public static final int MELON = 360;
    public static final int MELON_SLICE = 360;
    public static final int PUMPKIN_SEEDS = 361;
    public static final int MELON_SEEDS = 362;
    public static final int RAW_BEEF = 363;
    public static final int STEAK = 364;
    public static final int COOKED_BEEF = 364;

    public static final int RAW_CHICKEN = 365;
    public static final int COOKED_CHICKEN = 366;
    public static final int ROTTEN_FLESH = 367;

    public static final int BLAZE_ROD = 369;
    public static final int GHAST_TEAR = 370;
    public static final int GOLD_NUGGET = 371;
    public static final int GOLDEN_NUGGET = 371;
    public static final int NETHER_WART = 372;
    public static final int POTION = 373;
    public static final int GLASS_BOTTLE = 374;
    public static final int BOTTLE = 374;
    public static final int SPIDER_EYE = 375;
    public static final int FERMENTED_SPIDER_EYE = 376;
    public static final int BLAZE_POWDER = 377;
    public static final int MAGMA_CREAM = 378;
    public static final int BREWING_STAND = 379;
    public static final int BREWING = 379;

    public static final int CAULDRON = 380;

    public static final int GLISTERING_MELON = 382;
    public static final int SPAWN_EGG = 383;
    public static final int EXPERIENCE_BOTTLE = 384;

    public static final int EMERALD = 388;

    public static final int ITEM_FRAME = 389;

    public static final int FLOWER_POT = 390;
    public static final int CARROT = 391;
    public static final int CARROTS = 391;
    public static final int POTATO = 392;
    public static final int POTATOES = 392;
    public static final int BAKED_POTATO = 393;
    public static final int BAKED_POTATOES = 393;
    public static final int POISONOUS_POTATO = 394;

    public static final int EMPTY_MAP = 395;

    public static final int GOLDEN_CARROT = 396;

    public static final int SKULL = 397;

    public static final int PUMPKIN_PIE = 400;

    public static final int ENCHANTED_BOOK = 403;
    public static final int ENCHANT_BOOK = 403;

    public static final int COMPARATOR = 404;

    public static final int NETHER_BRICK = 405;
    public static final int QUARTZ = 406;
    public static final int NETHER_QUARTZ = 406;

    public static final int MINECART_WITH_TNT = 407;
    public static final int MINECART_WITH_HOPPER = 408;

    public static final int HOPPER = 410;

    public static final int RAW_RABBIT = 411;
    public static final int COOKED_RABBIT = 412;
    public static final int RABBIT_STEW = 413;
    public static final int RABBIT_FOOT = 414;
    public static final int RABBIT_HIDE = 415;

    public static final int SPRUCE_DOOR = 427;
    public static final int BIRCH_DOOR = 428;
    public static final int JUNGLE_DOOR = 429;
    public static final int ACACIA_DOOR = 430;
    public static final int DARK_OAK_DOOR = 431;

    public static final int SPLASH_POTION = 438;

    public static final int SPRUCE_BOAT = 444;
    public static final int BIRCH_BOAT = 445;
    public static final int JUNGLE_BOAT = 446;
    public static final int ACACIA_BOAT = 447;
    public static final int DARK_OAK_BOAT = 448;

    public static final int CAMERA = 456;
    public static final int BEETROOT = 457;
    public static final int BEETROOT_SEEDS = 458;
    public static final int BEETROOT_SEED = 458;
    public static final int BEETROOT_SOUP = 459;
    public static final int RAW_SALMON = 460;
    public static final int CLOWNFISH = 461;
    public static final int PUFFERFISH = 462;
    public static final int COOKED_SALMON = 463;

    public static final int GOLDEN_APPLE_ENCHANTED = 466;

    public static Class[] list = null;

    protected Block block = null;
    protected int id;
    protected int meta;
    protected boolean hasMeta = true;
    private byte[] tags = new byte[0];
    private CompoundTag cachedNBT = null;
    public int count;
    protected int durability = 0;
    protected String name;

    public Item(int id) {
        this(id, 0, 1, "Unknown");
    }

    public Item(int id, Integer meta) {
        this(id, meta, 1, "Unknown");
    }

    public Item(int id, Integer meta, int count) {
        this(id, meta, count, "Unknown");
    }

    public Item(int id, Integer meta, int count, String name) {
        this.id = id & 0xffff;
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = name;
        if (this.block != null && this.id <= 0xff && Block.list[id] != null) {
            this.block = Block.get(this.id, this.meta);
            this.name = this.block.getName();
        }
    }

    public boolean hasMeta() {
        return hasMeta;
    }

    public boolean canBeActivated() {
        return false;
    }

    public static void init() {
        if (list == null) {
            list = new Class[65535];
            list[IRON_SHOVEL] = ItemShovelIron.class;
            list[IRON_PICKAXE] = ItemPickaxeIron.class;
            list[IRON_AXE] = ItemAxeIron.class;
            list[FLINT_STEEL] = ItemFlintSteel.class;
            list[APPLE] = ItemApple.class;
            list[BOW] = ItemBow.class;
            list[ARROW] = ItemArrow.class;
            list[COAL] = ItemCoal.class;
            list[DIAMOND] = ItemDiamond.class;
            list[IRON_INGOT] = ItemIngotIron.class;
            list[GOLD_INGOT] = ItemIngotGold.class;
            list[IRON_SWORD] = ItemSwordIron.class;
            list[WOODEN_SWORD] = ItemSwordWood.class;
            list[WOODEN_SHOVEL] = ItemShovelWood.class;
            list[WOODEN_PICKAXE] = ItemPickaxeWood.class;
            list[WOODEN_AXE] = ItemAxeWood.class;
            list[STONE_SWORD] = ItemSwordStone.class;
            list[STONE_SHOVEL] = ItemShovelStone.class;
            list[STONE_PICKAXE] = ItemPickaxeStone.class;
            list[STONE_AXE] = ItemAxeStone.class;
            list[DIAMOND_SWORD] = ItemSwordDiamond.class;
            list[DIAMOND_SHOVEL] = ItemShovelDiamond.class;
            list[DIAMOND_PICKAXE] = ItemPickaxeDiamond.class;
            list[DIAMOND_AXE] = ItemAxeDiamond.class;
            list[STICK] = ItemStick.class;
            list[BOWL] = ItemBowl.class;
            list[MUSHROOM_STEW] = ItemMushroomStew.class;
            list[GOLD_SWORD] = ItemSwordGold.class;
            list[GOLD_SHOVEL] = ItemShovelGold.class;
            list[GOLD_PICKAXE] = ItemPickaxeGold.class;
            list[GOLD_AXE] = ItemAxeGold.class;
            list[STRING] = ItemString.class;
            list[FEATHER] = ItemFeather.class;
            list[GUNPOWDER] = ItemGunpowder.class;
            list[WOODEN_HOE] = ItemHoeWood.class;
            list[STONE_HOE] = ItemHoeStone.class;
            list[IRON_HOE] = ItemHoeIron.class;
            list[DIAMOND_HOE] = ItemHoeDiamond.class;
            list[GOLD_HOE] = ItemHoeGold.class;
            list[WHEAT_SEEDS] = ItemSeedsWheat.class;
            list[WHEAT] = ItemWheat.class;
            list[BREAD] = ItemBread.class;
            list[LEATHER_CAP] = ItemHelmetLeather.class;
            list[LEATHER_TUNIC] = ItemChestplateLeather.class;
            list[LEATHER_PANTS] = ItemLeggingsLeather.class;
            list[LEATHER_BOOTS] = ItemBootsLeather.class;
            list[CHAIN_HELMET] = ItemHelmetChain.class;
            list[CHAIN_CHESTPLATE] = ItemChestplateChain.class;
            list[CHAIN_LEGGINGS] = ItemLeggingsChain.class;
            list[CHAIN_BOOTS] = ItemBootsChain.class;
            list[IRON_HELMET] = ItemHelmetIron.class;
            list[IRON_CHESTPLATE] = ItemChestplateIron.class;
            list[IRON_LEGGINGS] = ItemLeggingsIron.class;
            list[IRON_BOOTS] = ItemBootsIron.class;
            list[DIAMOND_HELMET] = ItemHelmetDiamond.class;
            list[DIAMOND_CHESTPLATE] = ItemChestplateDiamond.class;
            list[DIAMOND_LEGGINGS] = ItemLeggingsDiamond.class;
            list[DIAMOND_BOOTS] = ItemBootsDiamond.class;
            list[GOLD_HELMET] = ItemHelmetGold.class;
            list[GOLD_CHESTPLATE] = ItemChestplateGold.class;
            list[GOLD_LEGGINGS] = ItemLeggingsGold.class;
            list[GOLD_BOOTS] = ItemBootsGold.class;
            list[FLINT] = ItemFlint.class;
            list[RAW_PORKCHOP] = ItemPorkchopRaw.class;
            list[COOKED_PORKCHOP] = ItemPorkchopCooked.class;
            list[PAINTING] = ItemPainting.class;
            list[GOLDEN_APPLE] = ItemAppleGold.class;
            list[SIGN] = ItemSign.class;
            list[WOODEN_DOOR] = ItemDoorWood.class;
            list[BUCKET] = ItemBucket.class;
            list[MINECART] = ItemMinecart.class;
            list[BOAT] = ItemBoat.class;
            list[IRON_DOOR] = ItemDoorIron.class;
            list[REDSTONE] = ItemRedstone.class;
            list[SNOWBALL] = ItemSnowball.class;
            list[LEATHER] = ItemLeather.class;
            list[BRICK] = ItemBrick.class;
            list[CLAY] = ItemClay.class;
            list[SUGARCANE] = ItemSugarcane.class;
            list[PAPER] = ItemPaper.class;
            list[BOOK] = ItemBook.class;
            list[SLIMEBALL] = ItemSlimeball.class;
            list[EGG] = ItemEgg.class;
            list[COMPASS] = ItemCompass.class;
            list[FISHING_ROD] = ItemFishingRod.class;
            list[CLOCK] = ItemClock.class;
            list[GLOWSTONE_DUST] = ItemGlowstoneDust.class;
            list[RAW_FISH] = ItemFish.class;
            list[COOKED_FISH] = ItemFishCooked.class;
            list[DYE] = ItemDye.class;
            list[BONE] = ItemBone.class;
            list[SUGAR] = ItemSugar.class;
            list[CAKE] = ItemCake.class;
            list[BED] = ItemBed.class;
            list[COOKIE] = ItemCookie.class;
            list[SHEARS] = ItemShears.class;
            list[MELON] = ItemMelon.class;
            list[PUMPKIN_SEEDS] = ItemSeedsPumpkin.class;
            list[MELON_SEEDS] = ItemSeedsMelon.class;
            list[RAW_BEEF] = ItemBeefRaw.class;
            list[STEAK] = ItemSteak.class;
            list[RAW_CHICKEN] = ItemChickenRaw.class;
            list[COOKED_CHICKEN] = ItemChickenCooked.class;
            list[GOLD_NUGGET] = ItemNuggetGold.class;
            list[SPAWN_EGG] = ItemSpawnEgg.class;
            list[EMERALD] = ItemEmerald.class;
            list[CARROT] = ItemCarrot.class;
            list[POTATO] = ItemPotato.class;
            list[BAKED_POTATO] = ItemPotatoBaked.class;
            list[SKULL] = ItemSkull.class;
            list[PUMPKIN_PIE] = ItemPumpkinPie.class;
            list[NETHER_BRICK] = ItemNetherBrick.class;
            list[QUARTZ] = ItemQuartz.class;
            // list[CAMERA] = ItemCamera.class;
            list[BEETROOT] = ItemBeetroot.class;
            list[BEETROOT_SEEDS] = ItemSeedsBeetroot.class;
            list[BEETROOT_SOUP] = ItemBeetrootSoup.class;
            list[REDSTONE_TORCH] = BlockRedstoneTorch.class;
            list[BREWING_STAND] = ItemBrewingStand.class;
            list[GLASS_BOTTLE] = ItemGlassBottle.class;
            list[POTION] = ItemPotion.class;
            list[SPLASH_POTION] = ItemPotionSplash.class;
            list[EXPERIENCE_BOTTLE] = ItemExpBottle.class;
            list[SPRUCE_DOOR] = ItemDoorSpruce.class;
            list[BIRCH_DOOR] = ItemDoorBirch.class;
            list[JUNGLE_DOOR] = ItemDoorJungle.class;
            list[ACACIA_DOOR] = ItemDoorAcacia.class;
            list[DARK_OAK_DOOR] = ItemDoorDarkOak.class;

            list[RAW_SALMON] = ItemSalmon.class;
            list[CLOWNFISH] = ItemClownfish.class;
            list[PUFFERFISH] = ItemPufferfish.class;
            list[COOKED_SALMON] = ItemSalmonCooked.class;
            list[SPIDER_EYE] = ItemSpiderEye.class;
            list[RAW_RABBIT] = ItemRabbitRaw.class;
            list[COOKED_RABBIT] = ItemRabbitCooked.class;
            list[RABBIT_FOOT] = ItemRabbitFoot.class;
            list[GOLDEN_APPLE_ENCHANTED] = ItemAppleGoldEnchanted.class;
            list[RABBIT_STEW] = ItemRabbitStew.class;
            list[POISONOUS_POTATO] = ItemPotatoPoisonous.class;
            list[ROTTEN_FLESH] = ItemRottenFlesh.class;

            list[FLOWER_POT] = ItemFlowerPot.class;

            for (int i = 0; i < 256; ++i) {
                if (Block.list[i] != null) {
                    list[i] = Block.list[i];
                }
            }
        }

        initCreativeItems();
    }

    private static ArrayList<Item> creative = new ArrayList<>();

    private static void initCreativeItems() {
        clearCreativeItems();

        //Building
        addCreativeItem(Item.get(Item.COBBLESTONE, 0));
        addCreativeItem(Item.get(Item.STONE_BRICKS, 0));
        addCreativeItem(Item.get(Item.STONE_BRICKS, 1));
        addCreativeItem(Item.get(Item.STONE_BRICKS, 2));
        addCreativeItem(Item.get(Item.STONE_BRICKS, 3));
        addCreativeItem(Item.get(Item.MOSS_STONE, 0));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 0));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 1));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 2));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 3));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 4));
        addCreativeItem(Item.get(Item.WOODEN_PLANKS, 5));
        addCreativeItem(Item.get(Item.BRICKS, 0));

        addCreativeItem(Item.get(Item.STONE, 0));
        addCreativeItem(Item.get(Item.STONE, 1));
        addCreativeItem(Item.get(Item.STONE, 2));
        addCreativeItem(Item.get(Item.STONE, 3));
        addCreativeItem(Item.get(Item.STONE, 4));
        addCreativeItem(Item.get(Item.STONE, 5));
        addCreativeItem(Item.get(Item.STONE, 6));
        addCreativeItem(Item.get(Item.DIRT, 0));
        addCreativeItem(Item.get(Item.PODZOL, 0));
        addCreativeItem(Item.get(Item.GRASS, 0));
        addCreativeItem(Item.get(Item.MYCELIUM, 0));
        addCreativeItem(Item.get(Item.CLAY_BLOCK, 0));
        addCreativeItem(Item.get(Item.HARDENED_CLAY, 0));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 0));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 1));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 2));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 3));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 4));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 5));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 6));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 7));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 8));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 9));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 10));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 11));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 12));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 13));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 14));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 15));
        addCreativeItem(Item.get(Item.SANDSTONE, 0));
        addCreativeItem(Item.get(Item.SANDSTONE, 1));
        addCreativeItem(Item.get(Item.SANDSTONE, 2));
        addCreativeItem(Item.get(Item.SAND, 0));
        addCreativeItem(Item.get(Item.SAND, 1));
        addCreativeItem(Item.get(Item.GRAVEL, 0));
        addCreativeItem(Item.get(Item.TRUNK, 0));
        addCreativeItem(Item.get(Item.TRUNK, 1));
        addCreativeItem(Item.get(Item.TRUNK, 2));
        addCreativeItem(Item.get(Item.TRUNK, 3));
        addCreativeItem(Item.get(Item.TRUNK2, 0));
        addCreativeItem(Item.get(Item.TRUNK2, 1));
        addCreativeItem(Item.get(Item.NETHER_BRICKS, 0));
        addCreativeItem(Item.get(Item.NETHERRACK, 0));
        addCreativeItem(Item.get(Item.SOUL_SAND, 0));
        addCreativeItem(Item.get(Item.BEDROCK, 0));
        addCreativeItem(Item.get(Item.COBBLESTONE_STAIRS, 0));
        addCreativeItem(Item.get(Item.OAK_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.SPRUCE_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.BIRCH_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.JUNGLE_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.ACACIA_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.DARK_OAK_WOODEN_STAIRS, 0));
        addCreativeItem(Item.get(Item.BRICK_STAIRS, 0));
        addCreativeItem(Item.get(Item.SANDSTONE_STAIRS, 0));
        addCreativeItem(Item.get(Item.STONE_BRICK_STAIRS, 0));
        addCreativeItem(Item.get(Item.NETHER_BRICKS_STAIRS, 0));
        addCreativeItem(Item.get(Item.QUARTZ_STAIRS, 0));
        addCreativeItem(Item.get(Item.SLAB, 0));
        addCreativeItem(Item.get(Item.SLAB, 3));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 0));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 1));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 2));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 3));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 4));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 5));
        addCreativeItem(Item.get(Item.SLAB, 4));
        addCreativeItem(Item.get(Item.SLAB, 1));
        addCreativeItem(Item.get(Item.SLAB, 5));
        addCreativeItem(Item.get(Item.SLAB, 6));
        addCreativeItem(Item.get(Item.SLAB, 7));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 0));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 2));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 1));
        addCreativeItem(Item.get(Item.COAL_ORE, 0));
        addCreativeItem(Item.get(Item.IRON_ORE, 0));
        addCreativeItem(Item.get(Item.GOLD_ORE, 0));
        addCreativeItem(Item.get(Item.DIAMOND_ORE, 0));
        addCreativeItem(Item.get(Item.LAPIS_ORE, 0));
        addCreativeItem(Item.get(Item.REDSTONE_ORE, 0));
        addCreativeItem(Item.get(Item.EMERALD_ORE, 0));
        addCreativeItem(Item.get(Item.QUARTZ_ORE, 0));
        addCreativeItem(Item.get(Item.OBSIDIAN, 0));
        addCreativeItem(Item.get(Item.ICE, 0));
        addCreativeItem(Item.get(Item.PACKED_ICE, 0));
        addCreativeItem(Item.get(Item.SNOW_BLOCK, 0));
        addCreativeItem(Item.get(Item.END_STONE, 0));

        //Decoration
        addCreativeItem(Item.get(Item.COBBLESTONE_WALL, 0));
        addCreativeItem(Item.get(Item.COBBLESTONE_WALL, 1));
        addCreativeItem(Item.get(Item.WATER_LILY, 0));
        addCreativeItem(Item.get(Item.GOLD_BLOCK, 0));
        addCreativeItem(Item.get(Item.IRON_BLOCK, 0));
        addCreativeItem(Item.get(Item.DIAMOND_BLOCK, 0));
        addCreativeItem(Item.get(Item.LAPIS_BLOCK, 0));
        addCreativeItem(Item.get(Item.COAL_BLOCK, 0));
        addCreativeItem(Item.get(Item.EMERALD_BLOCK, 0));
        addCreativeItem(Item.get(Item.REDSTONE_BLOCK, 0));
        addCreativeItem(Item.get(Item.SNOW_LAYER, 0));
        addCreativeItem(Item.get(Item.GLASS, 0));
        addCreativeItem(Item.get(Item.GLOWSTONE_BLOCK, 0));
        addCreativeItem(Item.get(Item.VINES, 0));
        addCreativeItem(Item.get(Item.LADDER, 0));
        addCreativeItem(Item.get(Item.SPONGE, 0));
        addCreativeItem(Item.get(Item.GLASS_PANE, 0));
        addCreativeItem(Item.get(Item.WOODEN_DOOR, 0));
        addCreativeItem(Item.get(Item.SPRUCE_DOOR, 0));
        addCreativeItem(Item.get(Item.BIRCH_DOOR, 0));
        addCreativeItem(Item.get(Item.JUNGLE_DOOR, 0));
        addCreativeItem(Item.get(Item.ACACIA_DOOR, 0));
        addCreativeItem(Item.get(Item.DARK_OAK_DOOR, 0));
        addCreativeItem(Item.get(Item.IRON_DOOR, 0));
        addCreativeItem(Item.get(Item.TRAPDOOR, 0));
        addCreativeItem(Item.get(Item.IRON_TRAPDOOR, 0));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_OAK));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_SPRUCE));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_BIRCH));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_JUNGLE));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_ACACIA));
        addCreativeItem(Item.get(Item.FENCE, BlockFence.FENCE_DARK_OAK));
        addCreativeItem(Item.get(Item.NETHER_BRICK_FENCE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_SPRUCE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_BIRCH, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_JUNGLE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_ACACIA, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_DARK_OAK, 0));
        addCreativeItem(Item.get(Item.IRON_BARS, 0));
        addCreativeItem(Item.get(Item.BED, 0));
        addCreativeItem(Item.get(Item.BOOKSHELF, 0));
        addCreativeItem(Item.get(Item.SIGN, 0));
        addCreativeItem(Item.get(Item.PAINTING, 0));
        addCreativeItem(Item.get(Item.ITEM_FRAME, 0));
        addCreativeItem(Item.get(Item.WORKBENCH, 0));
        addCreativeItem(Item.get(Item.STONECUTTER, 0));
        addCreativeItem(Item.get(Item.CHEST, 0));
        addCreativeItem(Item.get(Item.TRAPPED_CHEST, 0));
        addCreativeItem(Item.get(Item.FURNACE, 0));
        addCreativeItem(Item.get(Item.BREWING_STAND, 0));
        addCreativeItem(Item.get(Item.CAULDRON, 0));
        addCreativeItem(Item.get(Item.NOTEBLOCK, 0));
        addCreativeItem(Item.get(Item.END_PORTAL_FRAME, 0));
        addCreativeItem(Item.get(Item.ANVIL, 0));
        addCreativeItem(Item.get(Item.ANVIL, 4));
        addCreativeItem(Item.get(Item.ANVIL, 8));
        addCreativeItem(Item.get(Item.DANDELION, 0));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_POPPY));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_BLUE_ORCHID));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_ALLIUM));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_AZURE_BLUET));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_RED_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_ORANGE_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_WHITE_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_PINK_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, BlockFlower.TYPE_OXEYE_DAISY));
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 0)); // SUNFLOWER
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 1)); // Lilac
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 2)); // Double Tall Grass
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 3)); // Large fern
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 4)); // Rose bush
        addCreativeItem(Item.get(Item.DOUBLE_PLANT, 5)); // Peony
        addCreativeItem(Item.get(Item.BROWN_MUSHROOM, 0));
        addCreativeItem(Item.get(Item.RED_MUSHROOM, 0));
        addCreativeItem(Item.get(Item.BROWN_MUSHROOM_BLOCK, 14));
        addCreativeItem(Item.get(Item.RED_MUSHROOM_BLOCK, 14));
        addCreativeItem(Item.get(Item.BROWN_MUSHROOM_BLOCK, 0));
        addCreativeItem(Item.get(Item.RED_MUSHROOM_BLOCK, 15));
        addCreativeItem(Item.get(Item.CACTUS, 0));
        addCreativeItem(Item.get(Item.MELON_BLOCK, 0));
        addCreativeItem(Item.get(Item.PUMPKIN, 0));
        addCreativeItem(Item.get(Item.LIT_PUMPKIN, 0));
        addCreativeItem(Item.get(Item.COBWEB, 0));
        addCreativeItem(Item.get(Item.HAY_BALE, 0));
        addCreativeItem(Item.get(Item.TALL_GRASS, 1));
        addCreativeItem(Item.get(Item.TALL_GRASS, 2));
        addCreativeItem(Item.get(Item.DEAD_BUSH, 0));
        addCreativeItem(Item.get(Item.SAPLING, 0));
        addCreativeItem(Item.get(Item.SAPLING, 1));
        addCreativeItem(Item.get(Item.SAPLING, 2));
        addCreativeItem(Item.get(Item.SAPLING, 3));
        addCreativeItem(Item.get(Item.SAPLING, 4));
        addCreativeItem(Item.get(Item.SAPLING, 5));
        addCreativeItem(Item.get(Item.LEAVES, 0));
        addCreativeItem(Item.get(Item.LEAVES, 1));
        addCreativeItem(Item.get(Item.LEAVES, 2));
        addCreativeItem(Item.get(Item.LEAVES, 3));
        addCreativeItem(Item.get(Item.LEAVES2, 0));
        addCreativeItem(Item.get(Item.LEAVES2, 1));
        addCreativeItem(Item.get(Item.CAKE, 0));

        addCreativeItem(Item.get(Item.SKULL, 0)); // Skeleton
        addCreativeItem(Item.get(Item.SKULL, 1)); // Wither Skeleton
        addCreativeItem(Item.get(Item.SKULL, 2)); // Zombie
        addCreativeItem(Item.get(Item.SKULL, 3)); // Head (Steve)
        addCreativeItem(Item.get(Item.SKULL, 4)); // Creeper

        addCreativeItem(Item.get(Item.MONSTER_SPAWNER, 0));
        addCreativeItem(Item.get(Item.FLOWER_POT, 0));
        addCreativeItem(Item.get(Item.ENCHANTMENT_TABLE, 0));
        addCreativeItem(Item.get(Item.SLIME_BLOCK, 0));

        addCreativeItem(Item.get(Item.WOOL, 0));
        addCreativeItem(Item.get(Item.WOOL, 8));
        addCreativeItem(Item.get(Item.WOOL, 7));
        addCreativeItem(Item.get(Item.WOOL, 15));
        addCreativeItem(Item.get(Item.WOOL, 12));
        addCreativeItem(Item.get(Item.WOOL, 14));
        addCreativeItem(Item.get(Item.WOOL, 1));
        addCreativeItem(Item.get(Item.WOOL, 4));
        addCreativeItem(Item.get(Item.WOOL, 5));
        addCreativeItem(Item.get(Item.WOOL, 13));
        addCreativeItem(Item.get(Item.WOOL, 9));
        addCreativeItem(Item.get(Item.WOOL, 3));
        addCreativeItem(Item.get(Item.WOOL, 11));
        addCreativeItem(Item.get(Item.WOOL, 10));
        addCreativeItem(Item.get(Item.WOOL, 2));
        addCreativeItem(Item.get(Item.WOOL, 6));

        addCreativeItem(Item.get(Item.CARPET, 0));
        addCreativeItem(Item.get(Item.CARPET, 8));
        addCreativeItem(Item.get(Item.CARPET, 7));
        addCreativeItem(Item.get(Item.CARPET, 15));
        addCreativeItem(Item.get(Item.CARPET, 12));
        addCreativeItem(Item.get(Item.CARPET, 14));
        addCreativeItem(Item.get(Item.CARPET, 1));
        addCreativeItem(Item.get(Item.CARPET, 4));
        addCreativeItem(Item.get(Item.CARPET, 5));
        addCreativeItem(Item.get(Item.CARPET, 13));
        addCreativeItem(Item.get(Item.CARPET, 9));
        addCreativeItem(Item.get(Item.CARPET, 3));
        addCreativeItem(Item.get(Item.CARPET, 11));
        addCreativeItem(Item.get(Item.CARPET, 10));
        addCreativeItem(Item.get(Item.CARPET, 2));
        addCreativeItem(Item.get(Item.CARPET, 6));


        //Tools
        addCreativeItem(Item.get(Item.RAIL, 0));
        addCreativeItem(Item.get(Item.POWERED_RAIL, 0));
        addCreativeItem(Item.get(Item.DETECTOR_RAIL, 0));
        addCreativeItem(Item.get(Item.ACTIVATOR_RAIL, 0));
        addCreativeItem(Item.get(Item.TORCH, 0));
        addCreativeItem(Item.get(Item.BUCKET, 0));
        addCreativeItem(Item.get(Item.BUCKET, 1)); // milk
        addCreativeItem(Item.get(Item.BUCKET, 8)); // water
        addCreativeItem(Item.get(Item.BUCKET, 10)); // lava
        addCreativeItem(Item.get(Item.TNT, 0));
        addCreativeItem(Item.get(Item.REDSTONE, 0));
        addCreativeItem(Item.get(Item.BOW, 0));
        addCreativeItem(Item.get(Item.FISHING_ROD, 0));
        addCreativeItem(Item.get(Item.FLINT_AND_STEEL, 0));
        addCreativeItem(Item.get(Item.SHEARS, 0));
        addCreativeItem(Item.get(Item.CLOCK, 0));
        addCreativeItem(Item.get(Item.COMPASS, 0));
        addCreativeItem(Item.get(Item.MINECART, 0));
        addCreativeItem(Item.get(Item.MINECART_WITH_CHEST, 0));
        addCreativeItem(Item.get(Item.MINECART_WITH_HOPPER, 0));
        addCreativeItem(Item.get(Item.MINECART_WITH_TNT, 0));
        addCreativeItem(Item.get(Item.BOAT, 0)); // Oak
        addCreativeItem(Item.get(Item.BOAT, 1)); // Spruce
        addCreativeItem(Item.get(Item.BOAT, 2)); // Birch
        addCreativeItem(Item.get(Item.BOAT, 3)); // Jungle
        addCreativeItem(Item.get(Item.BOAT, 4)); // Acacia
        addCreativeItem(Item.get(Item.BOAT, 5)); // Dark Oak
        addCreativeItem(Item.get(Item.SPAWN_EGG, 10)); //Chicken
        addCreativeItem(Item.get(Item.SPAWN_EGG, 11)); //Cow
        addCreativeItem(Item.get(Item.SPAWN_EGG, 12)); //Pig
        addCreativeItem(Item.get(Item.SPAWN_EGG, 13)); //Sheep
        addCreativeItem(Item.get(Item.SPAWN_EGG, 14)); //Wolf
        addCreativeItem(Item.get(Item.SPAWN_EGG, 18)); //Rabbit
        addCreativeItem(Item.get(Item.SPAWN_EGG, 15)); //Villager
        /*
        addCreativeItem(Item.get(Item.SPAWN_EGG, 16)); //Mooshroom
        addCreativeItem(Item.get(Item.SPAWN_EGG, 19)); //Bat
        */
        addCreativeItem(Item.get(Item.SPAWN_EGG, 22)); //Ocelot
        addCreativeItem(Item.get(Item.SPAWN_EGG, 33)); //Creeper
        /*
        addCreativeItem(Item.get(Item.SPAWN_EGG, 38)); //Enderman
        addCreativeItem(Item.get(Item.SPAWN_EGG, 39)); //Silverfish
        addCreativeItem(Item.get(Item.SPAWN_EGG, 34)); //Skeleton
        addCreativeItem(Item.get(Item.SPAWN_EGG, 37)); //Slime
        addCreativeItem(Item.get(Item.SPAWN_EGG, 35)); //Spider
        addCreativeItem(Item.get(Item.SPAWN_EGG, 32)); //Zombie
        addCreativeItem(Item.get(Item.SPAWN_EGG, 36)); //Zombie Pigman
        addCreativeItem(Item.get(Item.SPAWN_EGG, 17)); //Squid
        addCreativeItem(Item.get(Item.SPAWN_EGG, 40)); //Cave spider
        addCreativeItem(Item.get(Item.SPAWN_EGG, 42)); //Magma Cube
        addCreativeItem(Item.get(Item.SPAWN_EGG, 41)); //Ghast
        addCreativeItem(Item.get(Item.SPAWN_EGG, 43)); //Blaze
        addCreativeItem(Item.get(Item.SPAWN_EGG, 20)); //Iron Golem
        addCreativeItem(Item.get(Item.SPAWN_EGG, 21)); //Snow Golem
        addCreativeItem(Item.get(Item.SPAWN_EGG, 44)); //Zombie Villager
        */
        addCreativeItem(Item.get(Item.WOODEN_SWORD));
        addCreativeItem(Item.get(Item.WOODEN_HOE));
        addCreativeItem(Item.get(Item.WOODEN_SHOVEL));
        addCreativeItem(Item.get(Item.WOODEN_PICKAXE));
        addCreativeItem(Item.get(Item.WOODEN_AXE));
        addCreativeItem(Item.get(Item.STONE_SWORD));
        addCreativeItem(Item.get(Item.STONE_HOE));
        addCreativeItem(Item.get(Item.STONE_SHOVEL));
        addCreativeItem(Item.get(Item.STONE_PICKAXE));
        addCreativeItem(Item.get(Item.STONE_AXE));
        addCreativeItem(Item.get(Item.IRON_SWORD));
        addCreativeItem(Item.get(Item.IRON_HOE));
        addCreativeItem(Item.get(Item.IRON_SHOVEL));
        addCreativeItem(Item.get(Item.IRON_PICKAXE));
        addCreativeItem(Item.get(Item.IRON_AXE));
        addCreativeItem(Item.get(Item.DIAMOND_SWORD));
        addCreativeItem(Item.get(Item.DIAMOND_HOE));
        addCreativeItem(Item.get(Item.DIAMOND_SHOVEL));
        addCreativeItem(Item.get(Item.DIAMOND_PICKAXE));
        addCreativeItem(Item.get(Item.DIAMOND_AXE));
        addCreativeItem(Item.get(Item.GOLD_SWORD));
        addCreativeItem(Item.get(Item.GOLD_HOE));
        addCreativeItem(Item.get(Item.GOLD_SHOVEL));
        addCreativeItem(Item.get(Item.GOLD_PICKAXE));
        addCreativeItem(Item.get(Item.GOLD_AXE));
        addCreativeItem(Item.get(Item.LEATHER_CAP));
        addCreativeItem(Item.get(Item.LEATHER_TUNIC));
        addCreativeItem(Item.get(Item.LEATHER_PANTS));
        addCreativeItem(Item.get(Item.LEATHER_BOOTS));
        addCreativeItem(Item.get(Item.CHAIN_HELMET));
        addCreativeItem(Item.get(Item.CHAIN_CHESTPLATE));
        addCreativeItem(Item.get(Item.CHAIN_LEGGINGS));
        addCreativeItem(Item.get(Item.CHAIN_BOOTS));
        addCreativeItem(Item.get(Item.IRON_HELMET));
        addCreativeItem(Item.get(Item.IRON_CHESTPLATE));
        addCreativeItem(Item.get(Item.IRON_LEGGINGS));
        addCreativeItem(Item.get(Item.IRON_BOOTS));
        addCreativeItem(Item.get(Item.DIAMOND_HELMET));
        addCreativeItem(Item.get(Item.DIAMOND_CHESTPLATE));
        addCreativeItem(Item.get(Item.DIAMOND_LEGGINGS));
        addCreativeItem(Item.get(Item.DIAMOND_BOOTS));
        addCreativeItem(Item.get(Item.GOLD_HELMET));
        addCreativeItem(Item.get(Item.GOLD_CHESTPLATE));
        addCreativeItem(Item.get(Item.GOLD_LEGGINGS));
        addCreativeItem(Item.get(Item.GOLD_BOOTS));
        addCreativeItem(Item.get(Item.LEVER));
        addCreativeItem(Item.get(Item.REDSTONE_LAMP));
        addCreativeItem(Item.get(Item.REDSTONE_TORCH));
        addCreativeItem(Item.get(Item.WOODEN_PRESSURE_PLATE));
        addCreativeItem(Item.get(Item.STONE_PRESSURE_PLATE));
        addCreativeItem(Item.get(Item.LIGHT_WEIGHTED_PRESSURE_PLATE));
        addCreativeItem(Item.get(Item.HEAVY_WEIGHTED_PRESSURE_PLATE));
        addCreativeItem(Item.get(Item.WOODEN_BUTTON, 5));
        addCreativeItem(Item.get(Item.STONE_BUTTON, 5));
        addCreativeItem(Item.get(Item.DAYLIGHT_DETECTOR));
        addCreativeItem(Item.get(Item.TRIPWIRE_HOOK));
        addCreativeItem(Item.get(Item.REPEATER));
        addCreativeItem(Item.get(Item.COMPARATOR));
        addCreativeItem(Item.get(Item.DISPENSER, 3));
        addCreativeItem(Item.get(Item.HOPPER));
        addCreativeItem(Item.get(Item.SNOWBALL));

        //Seeds
        addCreativeItem(Item.get(Item.COAL, 0));
        addCreativeItem(Item.get(Item.COAL, 1));
        addCreativeItem(Item.get(Item.DIAMOND, 0));
        addCreativeItem(Item.get(Item.IRON_INGOT, 0));
        addCreativeItem(Item.get(Item.GOLD_INGOT, 0));
        addCreativeItem(Item.get(Item.EMERALD, 0));
        addCreativeItem(Item.get(Item.STICK, 0));
        addCreativeItem(Item.get(Item.BOWL, 0));
        addCreativeItem(Item.get(Item.STRING, 0));
        addCreativeItem(Item.get(Item.FEATHER, 0));
        addCreativeItem(Item.get(Item.FLINT, 0));
        addCreativeItem(Item.get(Item.LEATHER, 0));
        addCreativeItem(Item.get(Item.RABBIT_HIDE, 0));
        addCreativeItem(Item.get(Item.CLAY, 0));
        addCreativeItem(Item.get(Item.SUGAR, 0));
        addCreativeItem(Item.get(Item.NETHER_QUARTZ, 0));
        addCreativeItem(Item.get(Item.PAPER, 0));
        addCreativeItem(Item.get(Item.BOOK, 0));
        addCreativeItem(Item.get(Item.ARROW, 0));
        addCreativeItem(Item.get(Item.BONE, 0));
        addCreativeItem(Item.get(Item.EMPTY_MAP, 0));
        addCreativeItem(Item.get(Item.SUGARCANE, 0));
        addCreativeItem(Item.get(Item.WHEAT, 0));
        addCreativeItem(Item.get(Item.SEEDS, 0));
        addCreativeItem(Item.get(Item.PUMPKIN_SEEDS, 0));
        addCreativeItem(Item.get(Item.MELON_SEEDS, 0));
        addCreativeItem(Item.get(Item.BEETROOT_SEEDS, 0));
        addCreativeItem(Item.get(Item.EGG, 0));
        addCreativeItem(Item.get(Item.APPLE, 0));
        addCreativeItem(Item.get(Item.GOLDEN_APPLE, 0));
        addCreativeItem(Item.get(Item.GOLDEN_APPLE_ENCHANTED, 0));
        addCreativeItem(Item.get(Item.RAW_FISH, 0));
        addCreativeItem(Item.get(Item.RAW_SALMON, 0));
        addCreativeItem(Item.get(Item.CLOWNFISH, 0));
        addCreativeItem(Item.get(Item.PUFFERFISH, 0));
        addCreativeItem(Item.get(Item.COOKED_FISH, 0));
        addCreativeItem(Item.get(Item.COOKED_SALMON, 0));
        addCreativeItem(Item.get(Item.ROTTEN_FLESH, 0));
        addCreativeItem(Item.get(Item.MUSHROOM_STEW, 0));
        addCreativeItem(Item.get(Item.BREAD, 0));
        addCreativeItem(Item.get(Item.RAW_PORKCHOP, 0));
        addCreativeItem(Item.get(Item.COOKED_PORKCHOP, 0));
        addCreativeItem(Item.get(Item.RAW_CHICKEN, 0));
        addCreativeItem(Item.get(Item.COOKED_CHICKEN, 0));
        addCreativeItem(Item.get(Item.RAW_BEEF, 0));
        addCreativeItem(Item.get(Item.STEAK, 0));
        addCreativeItem(Item.get(Item.MELON, 0));
        addCreativeItem(Item.get(Item.CARROT, 0));
        addCreativeItem(Item.get(Item.POTATO, 0));
        addCreativeItem(Item.get(Item.BAKED_POTATO, 0));
        addCreativeItem(Item.get(Item.POISONOUS_POTATO, 0));
        addCreativeItem(Item.get(Item.BEETROOT, 0));
        addCreativeItem(Item.get(Item.COOKIE, 0));
        addCreativeItem(Item.get(Item.PUMPKIN_PIE, 0));
        addCreativeItem(Item.get(Item.RAW_RABBIT, 0));
        addCreativeItem(Item.get(Item.COOKED_RABBIT, 0));
        addCreativeItem(Item.get(Item.RABBIT_STEW, 0));
        addCreativeItem(Item.get(Item.MAGMA_CREAM, 0));
        addCreativeItem(Item.get(Item.BLAZE_ROD, 0));
        addCreativeItem(Item.get(Item.GOLD_NUGGET, 0));
        addCreativeItem(Item.get(Item.GOLDEN_CARROT, 0));
        addCreativeItem(Item.get(Item.GLISTERING_MELON, 0));
        addCreativeItem(Item.get(Item.RABBIT_FOOT, 0));
        addCreativeItem(Item.get(Item.GHAST_TEAR, 0));
        addCreativeItem(Item.get(Item.SLIMEBALL, 0));
        addCreativeItem(Item.get(Item.BLAZE_POWDER, 0));
        addCreativeItem(Item.get(Item.NETHER_WART, 0));
        addCreativeItem(Item.get(Item.GUNPOWDER, 0));
        addCreativeItem(Item.get(Item.GLOWSTONE_DUST, 0));
        addCreativeItem(Item.get(Item.SPIDER_EYE, 0));
        addCreativeItem(Item.get(Item.FERMENTED_SPIDER_EYE, 0));
        addCreativeItem(Item.get(Item.EXPERIENCE_BOTTLE));
        addCreativeItem(Item.get(Item.DYE, 0));
        addCreativeItem(Item.get(Item.DYE, 8));
        addCreativeItem(Item.get(Item.DYE, 7));
        addCreativeItem(Item.get(Item.DYE, 15));
        addCreativeItem(Item.get(Item.DYE, 12));
        addCreativeItem(Item.get(Item.DYE, 14));
        addCreativeItem(Item.get(Item.DYE, 1));
        addCreativeItem(Item.get(Item.DYE, 4));
        addCreativeItem(Item.get(Item.DYE, 5));
        addCreativeItem(Item.get(Item.DYE, 13));
        addCreativeItem(Item.get(Item.DYE, 9));
        addCreativeItem(Item.get(Item.DYE, 3));
        addCreativeItem(Item.get(Item.DYE, 11));
        addCreativeItem(Item.get(Item.DYE, 10));
        addCreativeItem(Item.get(Item.DYE, 2));
        addCreativeItem(Item.get(Item.DYE, 6));

        //Potion
        addCreativeItem(Item.get(Item.GLASS_BOTTLE, 0));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.NO_EFFECTS));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.MUNDANE));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.MUNDANE_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.THICK));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.AWKWARD));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.NIGHT_VISION));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.NIGHT_VISION_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.INVISIBLE));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.INVISIBLE_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.LEAPING));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.LEAPING_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.LEAPING_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.SPEED));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.SPEED_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.SPEED_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.SLOWNESS));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.SLOWNESS_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.WATER_BREATHING));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.WATER_BREATHING_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.HARMING));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.HARMING_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.POISON));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.POISON_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.POISON_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.REGENERATION));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.REGENERATION_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.REGENERATION_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.STRENGTH));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.STRENGTH_LONG));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.STRENGTH_II));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.WEAKNESS));
        addCreativeItem(Item.get(Item.POTION, ItemPotion.WEAKNESS_LONG));

        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.NO_EFFECTS));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.MUNDANE));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.MUNDANE_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.THICK));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.AWKWARD));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.NIGHT_VISION));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.NIGHT_VISION_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.INVISIBLE));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.INVISIBLE_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.LEAPING));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.LEAPING_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.LEAPING_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.FIRE_RESISTANCE));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.FIRE_RESISTANCE_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.SPEED));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.SPEED_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.SPEED_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.SLOWNESS));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.SLOWNESS_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.WATER_BREATHING));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.WATER_BREATHING_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.INSTANT_HEALTH));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.INSTANT_HEALTH_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.HARMING));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.HARMING_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.POISON));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.POISON_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.POISON_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.REGENERATION));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.REGENERATION_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.REGENERATION_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.STRENGTH));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.STRENGTH_LONG));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.STRENGTH_II));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.WEAKNESS));
        addCreativeItem(Item.get(Item.SPLASH_POTION, ItemPotion.WEAKNESS_LONG));
    }

    public static void clearCreativeItems() {
        Item.creative.clear();
    }

    public static ArrayList<Item> getCreativeItems() {
        return new ArrayList<>(Item.creative);
    }

    public static void addCreativeItem(Item item) {
        Item.creative.add(Item.get(item.getId(), item.hasMeta ? item.getDamage() : null));
    }

    public static void removeCreativeItem(Item item) {
        int index = getCreativeItemIndex(item);
        if (index != -1) {
            Item.creative.remove(index);
        }
    }

    public static boolean isCreativeItem(Item item) {
        for (Item aCreative : Item.creative) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        return false;
    }

    public static Item getCreativeItem(int index) {
        return (index >= 0 && index < Item.creative.size()) ? Item.creative.get(index) : null;
    }

    public static int getCreativeItemIndex(Item item) {
        for (int i = 0; i < Item.creative.size(); i++) {
            if (item.equals(Item.creative.get(i), !item.isTool())) {
                return i;
            }
        }
        return -1;
    }

    public static Item get(int id) {
        return get(id, 0);
    }

    public static Item get(int id, Integer meta) {
        return get(id, meta, 1);
    }

    public static Item get(int id, Integer meta, int count) {
        return get(id, meta, count, new byte[0]);
    }

    public static Item get(int id, Integer meta, int count, byte[] tags) {
        try {
            Class c = list[id];
            if (c == null) {
                return new Item(id, meta, count).setCompoundTag(tags);
            } else if (id < 256) {
                return new ItemBlock((Block) c.getConstructor(int.class).newInstance(meta), meta, count).setCompoundTag(tags);
            } else {
                return ((Item) c.getConstructor(Integer.class, int.class).newInstance(meta, count)).setCompoundTag(tags);
            }
        } catch (Exception e) {
            return new Item(id, meta, count).setCompoundTag(tags);
        }
    }

    public static Item fromString(String str) {
        String[] b = str.trim().replace(' ', '_').replace("minecraft:", "").split(":");

        int id = 0;
        int meta = 0;

        Pattern integerPattern = Pattern.compile("^[1-9]\\d*$");
        if (integerPattern.matcher(b[0]).matches()) {
            id = Integer.valueOf(b[0]);
        } else {
            try {
                id = Item.class.getField(b[0].toUpperCase()).getInt(null);
            } catch (Exception ignore) {
            }
        }

        id = id & 0xFFFF;
        if (b.length != 1) meta = Integer.valueOf(b[1]) & 0xFFFF;

        return get(id, meta);
    }

    public static Item[] fromStringMultiple(String str) {
        String[] b = str.split(",");
        Item[] items = new Item[b.length - 1];
        for (int i = 0; i < b.length; i++) {
            items[i] = fromString(b[i]);
        }
        return items;
    }

    public Item setCompoundTag(CompoundTag tag) {
        this.setNamedTag(tag);
        return this;
    }

    public Item setCompoundTag(byte[] tags) {
        this.tags = tags;
        this.cachedNBT = null;
        return this;
    }

    public byte[] getCompoundTag() {
        return tags;
    }

    public boolean hasCompoundTag() {
        return this.tags != null && this.tags.length > 0;
    }

    public boolean hasCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        return tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag;

    }

    public Item clearCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return this;
        }
        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag) {
            tag.remove("BlockEntityTag");
            this.setNamedTag(tag);
        }

        return this;
    }

    public Item setCustomBlockData(CompoundTag compoundTag) {
        CompoundTag tags = compoundTag.copy();
        tags.setName("BlockEntityTag");

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        tag.putCompound("BlockEntityTag", tags);
        this.setNamedTag(tag);

        return this;
    }

    public CompoundTag getCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag")) {
            Tag bet = tag.get("BlockEntityTag");
            if (bet instanceof CompoundTag) {
                return (CompoundTag) bet;
            }
        }

        return null;
    }

    public boolean hasEnchantments() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("ench")) {
            Tag enchTag = tag.get("ench");
            if (enchTag instanceof ListTag) {
                return true;
            }
        }

        return false;
    }

    public Enchantment getEnchantment(int id) {
        return getEnchantment((short) (id & 0xffff));
    }

    public Enchantment getEnchantment(short id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAll()) {
            if (entry.getShort("id") == id) {
                Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
                e.setLevel(entry.getShort("lvl"));
                return e;
            }
        }

        return null;
    }

    public Item addEnchantment(Enchantment... enchantments) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        ListTag<CompoundTag> ench;
        if (!tag.contains("ench")) {
            ench = new ListTag<>("ench");
            tag.putList(ench);
        }

        for (Enchantment enchantment : enchantments) {
            boolean found = false;

            ench = tag.getList("ench", CompoundTag.class);
            for (int k = 0; k < ench.size(); k++) {
                CompoundTag entry = ench.get(k);
                if (entry.getShort("id") == enchantment.getId()) {
                    ench.add(k, new CompoundTag()
                            .putShort("id", enchantment.getId())
                            .putShort("lvl", enchantment.getLevel())
                    );
                    found = true;
                    break;
                }
            }

            if (!found) {
                ench.add(new CompoundTag()
                        .putShort("id", enchantment.getId())
                        .putShort("lvl", enchantment.getLevel())
                );
            }
        }

        this.setNamedTag(tag);
        return this;
    }

    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return new Enchantment[0];
        }

        List<Enchantment> enchantments = new ArrayList<>();

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", CompoundTag.class);
        for (CompoundTag entry : ench.getAll()) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            e.setLevel(entry.getShort("lvl"));
            enchantments.add(e);
        }

        return enchantments.stream().toArray(Enchantment[]::new);
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return true;
            }
        }

        return false;
    }

    public String getCustomName() {
        if (!this.hasCompoundTag()) {
            return "";
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return ((CompoundTag) tag1).getString("Name");
            }
        }

        return "";
    }

    public Item setCustomName(String name) {
        if (name == null || name.equals("")) {
            this.clearCustomName();
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").putString("Name", name);
        } else {
            tag.putCompound("display", new CompoundTag("display")
                    .putString("Name", name)
            );
        }
        this.setNamedTag(tag);
        return this;
    }

    public Item clearCustomName() {
        if (!this.hasCompoundTag()) {
            return this;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").remove("Name");
            if (tag.getCompound("display").isEmpty()) {
                tag.remove("display");
            }

            this.setNamedTag(tag);
        }

        return this;
    }

    public Tag getNamedTagEntry(String name) {
        CompoundTag tag = this.getNamedTag();
        if (tag != null) {
            return tag.contains(name) ? tag.get(name) : null;
        }

        return null;
    }

    public CompoundTag getNamedTag() {
        if (!this.hasCompoundTag()) {
            return null;
        } else if (this.cachedNBT != null) {
            return this.cachedNBT;
        }
        return this.cachedNBT = parseCompoundTag(this.tags);
    }

    public Item setNamedTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            return this.clearNamedTag();
        }

        this.cachedNBT = tag;
        this.tags = writeCompoundTag(tag);

        return this;
    }

    public Item clearNamedTag() {
        return this.setCompoundTag(new byte[0]);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    final public String getName() {
        return this.hasCustomName() ? this.getCustomName() : this.name;
    }

    final public boolean canBePlaced() {
        return ((this.block != null) && this.block.canBePlaced());
    }

    public Block getBlock() {
        if (this.block != null) {
            return this.block.clone();
        } else {
            return new BlockAir();
        }
    }

    public int getId() {
        return id;
    }

    public int getDamage() {
        return meta;
    }

    public void setDamage(Integer meta) {
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
    }

    public int getMaxStackSize() {
        return 64;
    }

    final public Short getFuelTime() {
        if (!Fuel.duration.containsKey(id)) {
            return null;
        }
        if (this.id != BUCKET || this.meta == 10) {
            return Fuel.duration.get(this.id);
        }
        return null;
    }

    public boolean useOn(Entity entity) {
        return false;
    }

    public boolean useOn(Block block) {
        return false;
    }

    public boolean isTool() {
        return false;
    }

    public int getMaxDurability() {
        return -1;
    }

    public int getTier() {
        return 0;
    }

    public boolean isPickaxe() {
        return false;
    }

    public boolean isAxe() {
        return false;
    }

    public boolean isSword() {
        return false;
    }

    public boolean isShovel() {
        return false;
    }

    public boolean isHoe() {
        return false;
    }

    public boolean isShears() {
        return false;
    }

    public boolean isArmor() {
        return false;
    }

    public boolean isHelmet() {
        return false;
    }

    public boolean isChestplate() {
        return false;
    }

    public boolean isLeggings() {
        return false;
    }

    public boolean isBoots() {
        return false;
    }

    public int getEnchantAbility() {
        return 0;
    }

    @Override
    final public String toString() {
        return "Item " + this.name + " (" + this.id + ":" + (!this.hasMeta ? "?" : this.meta) + ")x" + this.count + (this.hasCompoundTag() ? " tags:0x" + Binary.bytesToHexString(this.getCompoundTag()) : "");
    }

    public int getDestroySpeed(Block block, Player player) {
        return 1;
    }

    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        return false;
    }

    @Override
    public final boolean equals(Object item) {
        return item instanceof Item && this.equals((Item) item, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        return this.getId() == item.getId() && (!checkDamage || this.getDamage() == item.getDamage()) && (!checkCompound || Arrays.equals(this.getCompoundTag(), item.getCompoundTag()));
    }

    public final boolean deepEquals(Item item) {
        return deepEquals(item, true);
    }

    public final boolean deepEquals(Item item, boolean checkDamage) {
        return deepEquals(item, checkDamage, true);
    }

    public final boolean deepEquals(Item item, boolean checkDamage, boolean checkCompound) {
        if (this.equals(item, checkDamage, checkCompound)) {
            return true;
        } else if (item.hasCompoundTag()) {
            return item.getNamedTag().equals(this.getNamedTag());
        } else if (this.hasCompoundTag()) {
            return this.getNamedTag().equals(item.getNamedTag());
        }

        return false;
    }

    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.tags = this.tags.clone();
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

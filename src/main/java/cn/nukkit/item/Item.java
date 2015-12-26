package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Fence;
import cn.nukkit.block.Flower;
import cn.nukkit.block.RedstoneTorch;
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
import java.util.Objects;

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

    public static final int SANDSTONE = 24;

    public static final int BED_BLOCK = 26;


    public static final int COBWEB = 30;
    public static final int TALL_GRASS = 31;
    public static final int BUSH = 32;
    public static final int DEAD_BUSH = 32;
    public static final int WOOL = 35;
    public static final int DANDELION = 37;
    public static final int POPPY = 38;
    public static final int ROSE = 38;
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

    public static final int COBBLE_STAIRS = 67;
    public static final int COBBLESTONE_STAIRS = 67;
    public static final int WALL_SIGN = 68;

    public static final int IRON_DOOR_BLOCK = 71;

    public static final int REDSTONE_ORE = 73;
    public static final int GLOWING_REDSTONE_ORE = 74;
    public static final int LIT_REDSTONE_ORE = 74;

    public static final int REDSTONE_TORCH = 76;

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


    public static final int LIT_PUMPKIN = 91;
    public static final int JACK_O_LANTERN = 91;
    public static final int CAKE_BLOCK = 92;

    public static final int TRAPDOOR = 96;

    public static final int STONE_BRICKS = 98;
    public static final int STONE_BRICK = 98;

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

    public static final int END_PORTAL = 120;
    public static final int END_STONE = 121;

    public static final int SANDSTONE_STAIRS = 128;
    public static final int EMERALD_ORE = 129;

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

    public static final int CARROT_BLOCK = 141;
    public static final int POTATO_BLOCK = 142;

    public static final int ANVIL = 145;

    public static final int REDSTONE_BLOCK = 152;

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
    
    public static final int IRON_TRAPDOOR = 167;
    public static final int HAY_BALE = 170;
    public static final int CARPET = 171;
    public static final int HARDENED_CLAY = 172;
    public static final int COAL_BLOCK = 173;

    public static final int DOUBLE_PLANT = 175;

    public static final int FENCE_GATE_SPRUCE = 183;
    public static final int FENCE_GATE_BIRCH = 184;
    public static final int FENCE_GATE_JUNGLE = 185;
    public static final int FENCE_GATE_DARK_OAK = 186;
    public static final int FENCE_GATE_ACACIA = 187;

    public static final int GRASS_PATH = 198;

    public static final int PODZOL = 243;
    public static final int BEETROOT_BLOCK = 244;
    public static final int STONECUTTER = 245;
    public static final int GLOWING_OBSIDIAN = 246;
    public static final int NETHER_REACTOR = 247;


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

    public static final int LEATHER = 334;

    public static final int BRICK = 336;
    public static final int CLAY = 337;
    public static final int SUGARCANE = 338;
    public static final int SUGAR_CANE = 338;
    public static final int SUGAR_CANES = 338;
    public static final int PAPER = 339;
    public static final int BOOK = 340;
    public static final int SLIMEBALL = 341;

    public static final int EGG = 344;
    public static final int COMPASS = 345;

    public static final int CLOCK = 347;
    public static final int GLOWSTONE_DUST = 348;
    public static final int RAW_FISH = 349;
    public static final int COOKED_FISH = 350;
    public static final int DYE = 351;
    public static final int BONE = 352;
    public static final int SUGAR = 353;
    public static final int CAKE = 354;
    public static final int BED = 355;


    public static final int COOKIE = 357;


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

    public static final int GLISTERING_MELON = 382;
    public static final int SPAWN_EGG = 383;

    public static final int EMERALD = 388;

    public static final int CARROT = 391;
    public static final int CARROTS = 391;
    public static final int POTATO = 392;
    public static final int POTATOES = 392;
    public static final int BAKED_POTATO = 393;
    public static final int BAKED_POTATOES = 393;

    public static final int GOLDEN_CARROT = 396;

    public static final int PUMPKIN_PIE = 400;

    public static final int NETHER_BRICK = 405;
    public static final int QUARTZ = 406;
    public static final int NETHER_QUARTZ = 406;

    public static final int CAMERA = 456;
    public static final int BEETROOT = 457;
    public static final int BEETROOT_SEEDS = 458;
    public static final int BEETROOT_SEED = 458;
    public static final int BEETROOT_SOUP = 459;

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
            list[IRON_SHOVEL] = IronShovel.class;
            list[IRON_PICKAXE] = IronPickaxe.class;
            list[IRON_AXE] = IronAxe.class;
            list[FLINT_STEEL] = FlintSteel.class;
            list[APPLE] = Apple.class;
            list[BOW] = Bow.class;
            list[ARROW] = Arrow.class;
            list[COAL] = Coal.class;
            list[DIAMOND] = Diamond.class;
            list[IRON_INGOT] = IronIngot.class;
            list[GOLD_INGOT] = GoldIngot.class;
            list[IRON_SWORD] = IronSword.class;
            list[WOODEN_SWORD] = WoodenSword.class;
            list[WOODEN_SHOVEL] = WoodenShovel.class;
            list[WOODEN_PICKAXE] = WoodenPickaxe.class;
            list[WOODEN_AXE] = WoodenAxe.class;
            list[STONE_SWORD] = StoneSword.class;
            list[STONE_SHOVEL] = StoneShovel.class;
            list[STONE_PICKAXE] = StonePickaxe.class;
            list[STONE_AXE] = StoneAxe.class;
            list[DIAMOND_SWORD] = DiamondSword.class;
            list[DIAMOND_SHOVEL] = DiamondShovel.class;
            list[DIAMOND_PICKAXE] = DiamondPickaxe.class;
            list[DIAMOND_AXE] = DiamondAxe.class;
            list[STICK] = Stick.class;
            list[BOWL] = Bowl.class;
            list[MUSHROOM_STEW] = MushroomStew.class;
            list[GOLD_SWORD] = GoldSword.class;
            list[GOLD_SHOVEL] = GoldShovel.class;
            list[GOLD_PICKAXE] = GoldPickaxe.class;
            list[GOLD_AXE] = GoldAxe.class;
            list[STRING] = StringItem.class;
            list[FEATHER] = Feather.class;
            list[GUNPOWDER] = Gunpowder.class;
            list[WOODEN_HOE] = WoodenHoe.class;
            list[STONE_HOE] = StoneHoe.class;
            list[IRON_HOE] = IronHoe.class;
            list[DIAMOND_HOE] = DiamondHoe.class;
            list[GOLD_HOE] = GoldHoe.class;
            list[WHEAT_SEEDS] = WheatSeeds.class;
            list[WHEAT] = Wheat.class;
            list[BREAD] = Bread.class;
            list[LEATHER_CAP] = LeatherCap.class;
            list[LEATHER_TUNIC] = LeatherTunic.class;
            list[LEATHER_PANTS] = LeatherPants.class;
            list[LEATHER_BOOTS] = LeatherBoots.class;
            list[CHAIN_HELMET] = ChainHelmet.class;
            list[CHAIN_CHESTPLATE] = ChainChestplate.class;
            list[CHAIN_LEGGINGS] = ChainLeggings.class;
            list[CHAIN_BOOTS] = ChainBoots.class;
            list[IRON_HELMET] = IronHelmet.class;
            list[IRON_CHESTPLATE] = IronChestplate.class;
            list[IRON_LEGGINGS] = IronLeggings.class;
            list[IRON_BOOTS] = IronBoots.class;
            list[DIAMOND_HELMET] = DiamondHelmet.class;
            list[DIAMOND_CHESTPLATE] = DiamondChestplate.class;
            list[DIAMOND_LEGGINGS] = DiamondLeggings.class;
            list[DIAMOND_BOOTS] = DiamondBoots.class;
            list[GOLD_HELMET] = GoldHelmet.class;
            list[GOLD_CHESTPLATE] = GoldChestplate.class;
            list[GOLD_LEGGINGS] = GoldLeggings.class;
            list[GOLD_BOOTS] = GoldBoots.class;
            list[FLINT] = Flint.class;
            list[RAW_PORKCHOP] = RawPorkchop.class;
            list[COOKED_PORKCHOP] = CookedPorkchop.class;
            list[PAINTING] = Painting.class;
            list[GOLDEN_APPLE] = GoldenApple.class;
            list[SIGN] = Sign.class;
            list[WOODEN_DOOR] = WoodenDoor.class;
            list[BUCKET] = Bucket.class;
            list[MINECART] = Minecart.class;
            list[IRON_DOOR] = IronDoor.class;
            list[REDSTONE] = Redstone.class;
            list[SNOWBALL] = Snowball.class;
            list[LEATHER] = Leather.class;
            list[BRICK] = Brick.class;
            list[CLAY] = Clay.class;
            list[SUGARCANE] = Sugarcane.class;
            list[PAPER] = Paper.class;
            list[BOOK] = Book.class;
            list[SLIMEBALL] = Slimeball.class;
            list[EGG] = Egg.class;
            list[COMPASS] = Compass.class;
            list[CLOCK] = Clock.class;
            list[GLOWSTONE_DUST] = GlowstoneDust.class;
            list[RAW_FISH] = Fish.class;
            list[COOKED_FISH] = CookedFish.class;
            list[DYE] = Dye.class;
            list[BONE] = Bone.class;
            list[SUGAR] = Sugar.class;
            list[CAKE] = Cake.class;
            list[BED] = Bed.class;
            list[COOKIE] = Cookie.class;
            list[SHEARS] = Shears.class;
            list[MELON] = Melon.class;
            list[PUMPKIN_SEEDS] = PumpkinSeeds.class;
            list[MELON_SEEDS] = MelonSeeds.class;
            list[RAW_BEEF] = RawBeef.class;
            list[STEAK] = Steak.class;
            list[RAW_CHICKEN] = RawChicken.class;
            list[COOKED_CHICKEN] = CookedChicken.class;
            list[GOLD_NUGGET] = GoldNugget.class;
            list[SPAWN_EGG] = SpawnEgg.class;
            list[EMERALD] = Emerald.class;
            list[CARROT] = Carrot.class;
            list[POTATO] = Potato.class;
            list[BAKED_POTATO] = BakedPotato.class;
            list[PUMPKIN_PIE] = PumpkinPie.class;
            list[NETHER_BRICK] = NetherBrick.class;
            list[QUARTZ] = Quartz.class;
            list[QUARTZ] = NetherQuartz.class;
            // list[CAMERA] = Camera.class;
            list[BEETROOT] = Beetroot.class;
            list[BEETROOT_SEEDS] = BeetrootSeeds.class;
            list[BEETROOT_SOUP] = BeetrootSoup.class;
            list[REDSTONE_TORCH] = RedstoneTorch.class;
            list[BREWING_STAND] = BrewingStand.class;
            list[GLASS_BOTTLE] = GlassBottle.class;
            list[POTION] = Potion.class;

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
        addCreativeItem(Item.get(Item.STAINED_CLAY, 7));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 6));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 5));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 4));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 3));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 2));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 1));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 15));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 14));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 13));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 12));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 11));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 10));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 9));
        addCreativeItem(Item.get(Item.STAINED_CLAY, 8));
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
        addCreativeItem(Item.get(Item.SLAB, 1));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 0));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 1));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 2));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 3));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 4));
        addCreativeItem(Item.get(Item.WOODEN_SLAB, 5));
        addCreativeItem(Item.get(Item.SLAB, 3));
        addCreativeItem(Item.get(Item.SLAB, 4));
        addCreativeItem(Item.get(Item.SLAB, 5));
        addCreativeItem(Item.get(Item.SLAB, 6));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 0));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 1));
        addCreativeItem(Item.get(Item.QUARTZ_BLOCK, 2));
        addCreativeItem(Item.get(Item.COAL_ORE, 0));
        addCreativeItem(Item.get(Item.IRON_ORE, 0));
        addCreativeItem(Item.get(Item.GOLD_ORE, 0));
        addCreativeItem(Item.get(Item.DIAMOND_ORE, 0));
        addCreativeItem(Item.get(Item.LAPIS_ORE, 0));
        addCreativeItem(Item.get(Item.REDSTONE_ORE, 0));
        addCreativeItem(Item.get(Item.REDSTONE_TORCH, 0));
        addCreativeItem(Item.get(Item.EMERALD_ORE, 0));
        addCreativeItem(Item.get(Item.OBSIDIAN, 0));
        addCreativeItem(Item.get(Item.ICE, 0));
        addCreativeItem(Item.get(Item.SNOW_BLOCK, 0));
        addCreativeItem(Item.get(Item.END_STONE, 0));
        addCreativeItem(Item.get(Item.QUARTZ, 0));

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
        addCreativeItem(Item.get(Item.NETHER_REACTOR, 0));
        addCreativeItem(Item.get(Item.LADDER, 0));
        addCreativeItem(Item.get(Item.SPONGE, 0));
        addCreativeItem(Item.get(Item.GLASS_PANE, 0));
        addCreativeItem(Item.get(Item.WOODEN_DOOR, 0));
        addCreativeItem(Item.get(Item.TRAPDOOR, 0));
        addCreativeItem(Item.get(Item.IRON_TRAPDOOR, 0));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_OAK));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_SPRUCE));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_BIRCH));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_JUNGLE));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_ACACIA));
        addCreativeItem(Item.get(Item.FENCE, Fence.FENCE_DARK_OAK));
        addCreativeItem(Item.get(Item.NETHER_BRICK_FENCE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_BIRCH, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_SPRUCE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_DARK_OAK, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_JUNGLE, 0));
        addCreativeItem(Item.get(Item.FENCE_GATE_ACACIA, 0));
        addCreativeItem(Item.get(Item.IRON_BARS, 0));
        addCreativeItem(Item.get(Item.BED, 0));
        addCreativeItem(Item.get(Item.BOOKSHELF, 0));
        addCreativeItem(Item.get(Item.PAINTING, 0));
        addCreativeItem(Item.get(Item.WORKBENCH, 0));
        addCreativeItem(Item.get(Item.STONECUTTER, 0));
        addCreativeItem(Item.get(Item.CHEST, 0));
        addCreativeItem(Item.get(Item.FURNACE, 0));
        addCreativeItem(Item.get(Item.END_PORTAL, 0));
        addCreativeItem(Item.get(Item.DANDELION, 0));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_POPPY));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_BLUE_ORCHID));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_ALLIUM));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_AZURE_BLUET));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_RED_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_ORANGE_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_WHITE_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_PINK_TULIP));
        addCreativeItem(Item.get(Item.RED_FLOWER, Flower.TYPE_OXEYE_DAISY));
        //TODO: Lilac
        //TODO: Double Tallgrass
        //TODO: Large Fern
        //TODO: Rose Bush
        //TODO: Peony
        addCreativeItem(Item.get(Item.BROWN_MUSHROOM, 0));
        addCreativeItem(Item.get(Item.RED_MUSHROOM, 0));
        //TODO: Mushroom block (brown, cover)
        //TODO: Mushroom block (red, cover)
        //TODO: Mushroom block (brown, stem)
        //TODO: Mushroom block (red, stem)
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
        addCreativeItem(Item.get(Item.SIGN, 0));
        addCreativeItem(Item.get(Item.MONSTER_SPAWNER, 0));
        addCreativeItem(Item.get(Item.WOOL, 0));
        addCreativeItem(Item.get(Item.WOOL, 7));
        addCreativeItem(Item.get(Item.WOOL, 6));
        addCreativeItem(Item.get(Item.WOOL, 5));
        addCreativeItem(Item.get(Item.WOOL, 4));
        addCreativeItem(Item.get(Item.WOOL, 3));
        addCreativeItem(Item.get(Item.WOOL, 2));
        addCreativeItem(Item.get(Item.WOOL, 1));
        addCreativeItem(Item.get(Item.WOOL, 15));
        addCreativeItem(Item.get(Item.WOOL, 14));
        addCreativeItem(Item.get(Item.WOOL, 13));
        addCreativeItem(Item.get(Item.WOOL, 12));
        addCreativeItem(Item.get(Item.WOOL, 11));
        addCreativeItem(Item.get(Item.WOOL, 10));
        addCreativeItem(Item.get(Item.WOOL, 9));
        addCreativeItem(Item.get(Item.WOOL, 8));
        addCreativeItem(Item.get(Item.CARPET, 0));
        addCreativeItem(Item.get(Item.CARPET, 7));
        addCreativeItem(Item.get(Item.CARPET, 6));
        addCreativeItem(Item.get(Item.CARPET, 5));
        addCreativeItem(Item.get(Item.CARPET, 4));
        addCreativeItem(Item.get(Item.CARPET, 3));
        addCreativeItem(Item.get(Item.CARPET, 2));
        addCreativeItem(Item.get(Item.CARPET, 1));
        addCreativeItem(Item.get(Item.CARPET, 15));
        addCreativeItem(Item.get(Item.CARPET, 14));
        addCreativeItem(Item.get(Item.CARPET, 13));
        addCreativeItem(Item.get(Item.CARPET, 12));
        addCreativeItem(Item.get(Item.CARPET, 11));
        addCreativeItem(Item.get(Item.CARPET, 10));
        addCreativeItem(Item.get(Item.CARPET, 9));
        addCreativeItem(Item.get(Item.CARPET, 8));


        addCreativeItem(Item.get(Item.ANVIL, 0));
        addCreativeItem(Item.get(Item.ANVIL, 4));
        addCreativeItem(Item.get(Item.ANVIL, 8));

        //Tools
        //TODO addCreativeItem(Item.get(Item.RAILS, 0));
        //TODO addCreativeItem(Item.get(Item.POWERED_RAILS, 0));
        addCreativeItem(Item.get(Item.TORCH, 0));
        addCreativeItem(Item.get(Item.BUCKET, 0));
        addCreativeItem(Item.get(Item.BUCKET, 1));
        addCreativeItem(Item.get(Item.BUCKET, 8));
        addCreativeItem(Item.get(Item.BUCKET, 10));
        addCreativeItem(Item.get(Item.REDSTONE, 0));
        addCreativeItem(Item.get(Item.TNT, 0));
        addCreativeItem(Item.get(Item.IRON_HOE, 0));
        addCreativeItem(Item.get(Item.IRON_SHOVEL, 0));
        addCreativeItem(Item.get(Item.IRON_SWORD, 0));
        addCreativeItem(Item.get(Item.BOW, 0));
        addCreativeItem(Item.get(Item.SHEARS, 0));
        addCreativeItem(Item.get(Item.FLINT_AND_STEEL, 0));
        addCreativeItem(Item.get(Item.CLOCK, 0));
        addCreativeItem(Item.get(Item.COMPASS, 0));
        addCreativeItem(Item.get(Item.MINECART, 0));
        //addCreativeItem(Item.get(Item.SPAWN_EGG, Villager.NETWORK_ID));
        //addCreativeItem(Item.get(Item.SPAWN_EGG, 10)); //Chicken
        //addCreativeItem(Item.get(Item.SPAWN_EGG, 11)); //Cow
        //addCreativeItem(Item.get(Item.SPAWN_EGG, 12)); //Pig
        //addCreativeItem(Item.get(Item.SPAWN_EGG, 13)); //Sheep
        //TODO: Wolf
        //TODO: Mooshroom
        //TODO: Creeper
        //TODO: Enderman
        //TODO: Silverfish
        //TODO: Skeleton
        //TODO: Slime
        //addCreativeItem(Item.get(Item.SPAWN_EGG, Zombie.NETWORK_ID));
        //TODO: PigZombie
        //addCreativeItem(Item.get(Item.SPAWN_EGG, Squid.NETWORK_ID));

        addCreativeItem(Item.get(Item.SNOWBALL));


        //Seeds
        addCreativeItem(Item.get(Item.SUGARCANE, 0));
        addCreativeItem(Item.get(Item.WHEAT, 0));
        addCreativeItem(Item.get(Item.SEEDS, 0));
        addCreativeItem(Item.get(Item.MELON_SEEDS, 0));
        addCreativeItem(Item.get(Item.PUMPKIN_SEEDS, 0));
        addCreativeItem(Item.get(Item.CARROT, 0));
        addCreativeItem(Item.get(Item.POTATO, 0));
        addCreativeItem(Item.get(Item.BEETROOT_SEEDS, 0));
        addCreativeItem(Item.get(Item.EGG, 0));
        addCreativeItem(Item.get(Item.RAW_FISH, 0));
        addCreativeItem(Item.get(Item.RAW_FISH, 1));
        addCreativeItem(Item.get(Item.RAW_FISH, 2));
        addCreativeItem(Item.get(Item.RAW_FISH, 3));
        addCreativeItem(Item.get(Item.COOKED_FISH, 0));
        addCreativeItem(Item.get(Item.COOKED_FISH, 1));
        addCreativeItem(Item.get(Item.DYE, 0));
        addCreativeItem(Item.get(Item.DYE, 7));
        addCreativeItem(Item.get(Item.DYE, 6));
        addCreativeItem(Item.get(Item.DYE, 5));
        addCreativeItem(Item.get(Item.DYE, 4));
        addCreativeItem(Item.get(Item.DYE, 3));
        addCreativeItem(Item.get(Item.DYE, 2));
        addCreativeItem(Item.get(Item.DYE, 1));
        addCreativeItem(Item.get(Item.DYE, 15));
        addCreativeItem(Item.get(Item.DYE, 14));
        addCreativeItem(Item.get(Item.DYE, 13));
        addCreativeItem(Item.get(Item.DYE, 12));
        addCreativeItem(Item.get(Item.DYE, 11));
        addCreativeItem(Item.get(Item.DYE, 10));
        addCreativeItem(Item.get(Item.DYE, 9));
        addCreativeItem(Item.get(Item.DYE, 8));
    }

    public static void clearCreativeItems() {
        Item.creative.clear();
    }

    public static ArrayList<Item> getCreativeItems() {
        return Item.creative;
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

        int meta;
        if (b.length == 1) {
            meta = 0;
        } else {
            meta = Integer.valueOf(b[1]) & 0xFFFF;
        }
        return get(Integer.valueOf(b[0]) & 0xFFFF, meta);
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

    public Enchantment getEnchantment(short id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : ((ListTag<CompoundTag>) this.getNamedTag().getList("ench")).getAll()) {
            if (entry.getShort("id") == id) {
                Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
                e.setLevel(entry.getShort("lvl"));
                return e;
            }
        }

        return null;
    }

    public void addEnchantment(Enchantment enchantment) {
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

        boolean found = false;

        ench = tag.getList("ench", new ListTag<>());
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

        this.setNamedTag(tag);
    }

    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return new Enchantment[0];
        }

        List<Enchantment> enchantments = new ArrayList<>();

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", new ListTag<>());
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
            return Block.get(AIR);
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


    public final boolean equals(Item item) {
        return equals(item, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        return this.id == item.getId() && ((!checkDamage) || Objects.equals(this.getDamage(), item.getDamage())) && ((!checkCompound) || Arrays.equals(this.getCompoundTag(), item.getCompoundTag()));
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
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

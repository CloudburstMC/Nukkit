package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Block extends Position implements Metadatable, Cloneable, AxisAlignedBB {
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
    public static final int STICKY_PISTON = 29;
    public static final int COBWEB = 30;
    public static final int TALL_GRASS = 31;
    public static final int BUSH = 32;
    public static final int DEAD_BUSH = 32;
    public static final int PISTON = 33;
    public static final int PISTON_HEAD = 34;
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
    public static final int DOUBLE_STONE_SLAB = 43;
    public static final int DOUBLE_SLABS = 43;
    public static final int SLAB = 44;
    public static final int STONE_SLAB = 44;
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
    public static final int UNLIT_REDSTONE_TORCH = 75;
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
    public static final int JUKEBOX = 84;
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
    public static final int UNPOWERED_REPEATER = 93;
    public static final int POWERED_REPEATER = 94;
    public static final int INVISIBLE_BEDROCK = 95;
    public static final int TRAPDOOR = 96;
    public static final int MONSTER_EGG = 97;
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
    public static final int FENCE_GATE_OAK = 107;
    public static final int BRICK_STAIRS = 108;
    public static final int STONE_BRICK_STAIRS = 109;
    public static final int MYCELIUM = 110;
    public static final int WATER_LILY = 111;
    public static final int LILY_PAD = 111;
    public static final int NETHER_BRICKS = 112;
    public static final int NETHER_BRICK_BLOCK = 112;
    public static final int NETHER_BRICK_FENCE = 113;
    public static final int NETHER_BRICKS_STAIRS = 114;
    public static final int NETHER_WART_BLOCK = 115;
    public static final int ENCHANTING_TABLE = 116;
    public static final int ENCHANT_TABLE = 116;
    public static final int ENCHANTMENT_TABLE = 116;
    public static final int BREWING_STAND_BLOCK = 117;
    public static final int BREWING_BLOCK = 117;
    public static final int CAULDRON_BLOCK = 118;
    public static final int END_PORTAL = 119;
    public static final int END_PORTAL_FRAME = 120;
    public static final int END_STONE = 121;
    public static final int DRAGON_EGG = 122;
    public static final int REDSTONE_LAMP = 123;
    public static final int LIT_REDSTONE_LAMP = 124;
    //Note: dropper CAN NOT BE HARVESTED WITH HAND -- canHarvestWithHand method should be overridden FALSE.
    public static final int DROPPER = 125;
    public static final int ACTIVATOR_RAIL = 126;
    public static final int COCOA = 127;
    public static final int COCOA_BLOCK = 127;
    public static final int SANDSTONE_STAIRS = 128;
    public static final int EMERALD_ORE = 129;
    public static final int ENDER_CHEST = 130;
    public static final int TRIPWIRE_HOOK = 131;
    public static final int TRIPWIRE = 132;
    public static final int EMERALD_BLOCK = 133;
    public static final int SPRUCE_WOOD_STAIRS = 134;
    public static final int SPRUCE_WOODEN_STAIRS = 134;
    public static final int BIRCH_WOOD_STAIRS = 135;
    public static final int BIRCH_WOODEN_STAIRS = 135;
    public static final int JUNGLE_WOOD_STAIRS = 136;
    public static final int JUNGLE_WOODEN_STAIRS = 136;

    public static final int BEACON = 138;
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
    public static final int STAINED_TERRACOTTA = 159;
    public static final int STAINED_HARDENED_CLAY = 159;
    public static final int STAINED_GLASS_PANE = 160;
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
    public static final int PRISMARINE = 168;
    public static final int SEA_LANTERN = 169;
    public static final int HAY_BALE = 170;
    public static final int CARPET = 171;
    public static final int TERRACOTTA = 172;
    public static final int COAL_BLOCK = 173;
    public static final int PACKED_ICE = 174;
    public static final int DOUBLE_PLANT = 175;

    public static final int DAYLIGHT_DETECTOR_INVERTED = 178;
    public static final int RED_SANDSTONE = 179;
    public static final int RED_SANDSTONE_STAIRS = 180;
    public static final int DOUBLE_RED_SANDSTONE_SLAB = 181;
    public static final int RED_SANDSTONE_SLAB = 182;
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
    public static final int ITEM_FRAME_BLOCK = 199;
    public static final int CHORUS_FLOWER = 200;
    public static final int PURPUR_BLOCK = 201;

    public static final int PURPUR_STAIRS = 203;
    public static final int DOUBLE_PURPUR_SLAB = 204;
    public static final int PURPUR_SLAB = 205;
    public static final int END_BRICKS = 206;
    //Note: frosted ice CAN NOT BE HARVESTED WITH HAND -- canHarvestWithHand method should be overridden FALSE.
    public static final int ICE_FROSTED = 207;
    public static final int END_ROD = 208;
    public static final int END_GATEWAY = 209;

    public static final int MAGMA = 213;
    public static final int BLOCK_NETHER_WART_BLOCK = 214;
    public static final int RED_NETHER_BRICK = 215;
    public static final int BONE_BLOCK = 216;

    public static final int SHULKER_BOX = 218;
    public static final int PURPLE_GLAZED_TERRACOTTA = 219;
    public static final int WHITE_GLAZED_TERRACOTTA = 220;
    public static final int ORANGE_GLAZED_TERRACOTTA = 221;
    public static final int MAGENTA_GLAZED_TERRACOTTA = 222;
    public static final int LIGHT_BLUE_GLAZED_TERRACOTTA = 223;
    public static final int YELLOW_GLAZED_TERRACOTTA = 224;
    public static final int LIME_GLAZED_TERRACOTTA = 225;
    public static final int PINK_GLAZED_TERRACOTTA = 226;
    public static final int GRAY_GLAZED_TERRACOTTA = 227;
    public static final int SILVER_GLAZED_TERRACOTTA = 228;
    public static final int CYAN_GLAZED_TERRACOTTA = 229;
    public static final int BLUE_GLAZED_TERRACOTTA = 231;
    public static final int BROWN_GLAZED_TERRACOTTA = 232;
    public static final int GREEN_GLAZED_TERRACOTTA = 233;
    public static final int RED_GLAZED_TERRACOTTA = 234;
    public static final int BLACK_GLAZED_TERRACOTTA = 235;
    public static final int CONCRETE = 236;
    public static final int CONCRETE_POWDER = 237;

    public static final int CHORUS_PLANT = 240;
    public static final int STAINED_GLASS = 241;
    public static final int PODZOL = 243;
    public static final int BEETROOT_BLOCK = 244;
    public static final int STONECUTTER = 245;
    public static final int GLOWING_OBSIDIAN = 246;
    public static final int NETHER_REACTOR = 247; //Should not be removed

    public static final int PISTON_EXTENSION = 250;

    public static final int OBSERVER = 251;

    public static Class[] list = null;
    public static Block[] fullList = null;
    public static int[] light = null;
    public static int[] lightFilter = null;
    public static boolean[] solid = null;
    public static double[] hardness = null;
    public static boolean[] transparent = null;
    /**
     * if a block has can have variants
     */
    public static boolean[] hasMeta = null;

    protected Block() {}

    @SuppressWarnings("unchecked")
    public static void init() {
        if (list == null) {
            list = new Class[256];
            fullList = new Block[4096];
            light = new int[256];
            lightFilter = new int[256];
            solid = new boolean[256];
            hardness = new double[256];
            transparent = new boolean[256];
            hasMeta = new boolean[256];

            list[AIR] = BlockAir.class; //0
            list[STONE] = BlockStone.class; //1
            list[GRASS] = BlockGrass.class; //2
            list[DIRT] = BlockDirt.class; //3
            list[COBBLESTONE] = BlockCobblestone.class; //4
            list[PLANKS] = BlockPlanks.class; //5
            list[SAPLING] = BlockSapling.class; //6
            list[BEDROCK] = BlockBedrock.class; //7
            list[WATER] = BlockWater.class; //8
            list[STILL_WATER] = BlockWaterStill.class; //9
            list[LAVA] = BlockLava.class; //10
            list[STILL_LAVA] = BlockLavaStill.class; //11
            list[SAND] = BlockSand.class; //12
            list[GRAVEL] = BlockGravel.class; //13
            list[GOLD_ORE] = BlockOreGold.class; //14
            list[IRON_ORE] = BlockOreIron.class; //15
            list[COAL_ORE] = BlockOreCoal.class; //16
            list[WOOD] = BlockWood.class; //17
            list[LEAVES] = BlockLeaves.class; //18
            list[SPONGE] = BlockSponge.class; //19
            list[GLASS] = BlockGlass.class; //20
            list[LAPIS_ORE] = BlockOreLapis.class; //21
            list[LAPIS_BLOCK] = BlockLapis.class; //22
            //TODO: list[DISPENSER] = BlockDispenser.class; //23
            list[SANDSTONE] = BlockSandstone.class; //24
            list[NOTEBLOCK] = BlockNoteblock.class; //25
            list[BED_BLOCK] = BlockBed.class; //26
            list[POWERED_RAIL] = BlockRailPowered.class; //27
            list[DETECTOR_RAIL] = BlockRailDetector.class; //28
            list[STICKY_PISTON] = BlockPistonSticky.class; //29
            list[COBWEB] = BlockCobweb.class; //30
            list[TALL_GRASS] = BlockTallGrass.class; //31
            list[DEAD_BUSH] = BlockDeadBush.class; //32
            list[PISTON] = BlockPiston.class; //33
            list[PISTON_HEAD] = BlockPistonHead.class; //34
            list[WOOL] = BlockWool.class; //35
            list[DANDELION] = BlockDandelion.class; //37
            list[FLOWER] = BlockFlower.class; //38
            list[BROWN_MUSHROOM] = BlockMushroomBrown.class; //39
            list[RED_MUSHROOM] = BlockMushroomRed.class; //40
            list[GOLD_BLOCK] = BlockGold.class; //41
            list[IRON_BLOCK] = BlockIron.class; //42
            list[DOUBLE_STONE_SLAB] = BlockDoubleSlabStone.class; //43
            list[STONE_SLAB] = BlockSlabStone.class; //44
            list[BRICKS_BLOCK] = BlockBricks.class; //45
            list[TNT] = BlockTNT.class; //46
            list[BOOKSHELF] = BlockBookshelf.class; //47
            list[MOSS_STONE] = BlockMossStone.class; //48
            list[OBSIDIAN] = BlockObsidian.class; //49
            list[TORCH] = BlockTorch.class; //50
            list[FIRE] = BlockFire.class; //51
            list[MONSTER_SPAWNER] = BlockMobSpawner.class; //52
            list[WOOD_STAIRS] = BlockStairsWood.class; //53
            list[CHEST] = BlockChest.class; //54
            list[REDSTONE_WIRE] = BlockRedstoneWire.class; //55
            list[DIAMOND_ORE] = BlockOreDiamond.class; //56
            list[DIAMOND_BLOCK] = BlockDiamond.class; //57
            list[WORKBENCH] = BlockCraftingTable.class; //58
            list[WHEAT_BLOCK] = BlockWheat.class; //59
            list[FARMLAND] = BlockFarmland.class; //60
            list[FURNACE] = BlockFurnace.class; //61
            list[BURNING_FURNACE] = BlockFurnaceBurning.class; //62
            list[SIGN_POST] = BlockSignPost.class; //63
            list[WOOD_DOOR_BLOCK] = BlockDoorWood.class; //64
            list[LADDER] = BlockLadder.class; //65
            list[RAIL] = BlockRail.class; //66
            list[COBBLESTONE_STAIRS] = BlockStairsCobblestone.class; //67
            list[WALL_SIGN] = BlockWallSign.class; //68
            list[LEVER] = BlockLever.class; //69
            list[STONE_PRESSURE_PLATE] = BlockPressurePlateStone.class; //70
            list[IRON_DOOR_BLOCK] = BlockDoorIron.class; //71
            list[WOODEN_PRESSURE_PLATE] = BlockPressurePlateWood.class; //72
            list[REDSTONE_ORE] = BlockOreRedstone.class; //73
            list[GLOWING_REDSTONE_ORE] = BlockOreRedstoneGlowing.class; //74
            list[UNLIT_REDSTONE_TORCH] = BlockRedstoneTorchUnlit.class;
            list[REDSTONE_TORCH] = BlockRedstoneTorch.class; //76
            list[STONE_BUTTON] = BlockButtonStone.class; //77
            list[SNOW_LAYER] = BlockSnowLayer.class; //78
            list[ICE] = BlockIce.class; //79
            list[SNOW_BLOCK] = BlockSnow.class; //80
            list[CACTUS] = BlockCactus.class; //81
            list[CLAY_BLOCK] = BlockClay.class; //82
            list[SUGARCANE_BLOCK] = BlockSugarcane.class; //83
            list[JUKEBOX] = BlockJukebox.class; //84
            list[FENCE] = BlockFence.class; //85
            list[PUMPKIN] = BlockPumpkin.class; //86
            list[NETHERRACK] = BlockNetherrack.class; //87
            list[SOUL_SAND] = BlockSoulSand.class; //88
            list[GLOWSTONE_BLOCK] = BlockGlowstone.class; //89
            list[NETHER_PORTAL] = BlockNetherPortal.class; //90
            list[LIT_PUMPKIN] = BlockPumpkinLit.class; //91
            list[CAKE_BLOCK] = BlockCake.class; //92
            list[UNPOWERED_REPEATER] = BlockRedstoneRepeaterUnpowered.class; //93
            list[POWERED_REPEATER] = BlockRedstoneRepeaterPowered.class; //94
            list[INVISIBLE_BEDROCK] = BlockBedrockInvisible.class; //95
            list[TRAPDOOR] = BlockTrapdoor.class; //96
            list[MONSTER_EGG] = BlockMonsterEgg.class; //97
            list[STONE_BRICKS] = BlockBricksStone.class; //98
            list[BROWN_MUSHROOM_BLOCK] = BlockHugeMushroomBrown.class; //99
            list[RED_MUSHROOM_BLOCK] = BlockHugeMushroomRed.class; //100
            list[IRON_BARS] = BlockIronBars.class; //101
            list[GLASS_PANE] = BlockGlassPane.class; //102
            list[MELON_BLOCK] = BlockMelon.class; //103
            list[PUMPKIN_STEM] = BlockStemPumpkin.class; //104
            list[MELON_STEM] = BlockStemMelon.class; //105
            list[VINE] = BlockVine.class; //106
            list[FENCE_GATE] = BlockFenceGate.class; //107
            list[BRICK_STAIRS] = BlockStairsBrick.class; //108
            list[STONE_BRICK_STAIRS] = BlockStairsStoneBrick.class; //109
            list[MYCELIUM] = BlockMycelium.class; //110
            list[WATER_LILY] = BlockWaterLily.class; //111
            list[NETHER_BRICKS] = BlockBricksNether.class; //112
            list[NETHER_BRICK_FENCE] = BlockFenceNetherBrick.class; //113
            list[NETHER_BRICKS_STAIRS] = BlockStairsNetherBrick.class; //114
            list[NETHER_WART_BLOCK] = BlockNetherWart.class; //115
            list[ENCHANTING_TABLE] = BlockEnchantingTable.class; //116
            list[BREWING_STAND_BLOCK] = BlockBrewingStand.class; //117
            list[CAULDRON_BLOCK] = BlockCauldron.class; //118
            list[END_PORTAL] = BlockEndPortal.class; //119
            list[END_PORTAL_FRAME] = BlockEndPortalFrame.class; //120
            list[END_STONE] = BlockEndStone.class; //121
            list[DRAGON_EGG] = BlockDragonEgg.class; //122
            list[REDSTONE_LAMP] = BlockRedstoneLamp.class; //123
            list[LIT_REDSTONE_LAMP] = BlockRedstoneLampLit.class; //124
            //TODO: list[DROPPER] = BlockDropper.class; //125
            list[ACTIVATOR_RAIL] = BlockRailActivator.class; //126
            list[COCOA] = BlockCocoa.class; //127
            list[SANDSTONE_STAIRS] = BlockStairsSandstone.class; //128
            list[EMERALD_ORE] = BlockOreEmerald.class; //129
            list[ENDER_CHEST] = BlockEnderChest.class; //130
            list[TRIPWIRE_HOOK] = BlockTripWireHook.class;
            list[TRIPWIRE] = BlockTripWire.class; //132
            list[EMERALD_BLOCK] = BlockEmerald.class; //133
            list[SPRUCE_WOOD_STAIRS] = BlockStairsSpruce.class; //134
            list[BIRCH_WOOD_STAIRS] = BlockStairsBirch.class; //135
            list[JUNGLE_WOOD_STAIRS] = BlockStairsJungle.class; //136

            list[BEACON] = BlockBeacon.class; //138
            list[STONE_WALL] = BlockWall.class; //139
            list[FLOWER_POT_BLOCK] = BlockFlowerPot.class; //140
            list[CARROT_BLOCK] = BlockCarrot.class; //141
            list[POTATO_BLOCK] = BlockPotato.class; //142
            list[WOODEN_BUTTON] = BlockButtonWooden.class; //143
            list[SKULL_BLOCK] = BlockSkull.class; //144
            list[ANVIL] = BlockAnvil.class; //145
            list[TRAPPED_CHEST] = BlockTrappedChest.class; //146
            list[LIGHT_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateLight.class; //147
            list[HEAVY_WEIGHTED_PRESSURE_PLATE] = BlockWeightedPressurePlateHeavy.class; //148
            list[UNPOWERED_COMPARATOR] = BlockRedstoneComparatorUnpowered.class; //149
            list[POWERED_COMPARATOR] = BlockRedstoneComparatorPowered.class; //149
            list[DAYLIGHT_DETECTOR] = BlockDaylightDetector.class; //151
            list[REDSTONE_BLOCK] = BlockRedstone.class; //152
            list[QUARTZ_ORE] = BlockOreQuartz.class; //153
            list[HOPPER_BLOCK] = BlockHopper.class; //154
            list[QUARTZ_BLOCK] = BlockQuartz.class; //155
            list[QUARTZ_STAIRS] = BlockStairsQuartz.class; //156
            list[DOUBLE_WOOD_SLAB] = BlockDoubleSlabWood.class; //157
            list[WOOD_SLAB] = BlockSlabWood.class; //158
            list[STAINED_TERRACOTTA] = BlockTerracottaStained.class; //159
            list[STAINED_GLASS_PANE] = BlockGlassPaneStained.class; //160

            list[LEAVES2] = BlockLeaves2.class; //161
            list[WOOD2] = BlockWood2.class; //162
            list[ACACIA_WOOD_STAIRS] = BlockStairsAcacia.class; //163
            list[DARK_OAK_WOOD_STAIRS] = BlockStairsDarkOak.class; //164
            list[SLIME_BLOCK] = BlockSlime.class; //165

            list[IRON_TRAPDOOR] = BlockTrapdoorIron.class; //167
            list[PRISMARINE] = BlockPrismarine.class; //168
            list[SEA_LANTERN] = BlockSeaLantern.class; //169
            list[HAY_BALE] = BlockHayBale.class; //170
            list[CARPET] = BlockCarpet.class; //171
            list[TERRACOTTA] = BlockTerracotta.class; //172
            list[COAL_BLOCK] = BlockCoal.class; //173
            list[PACKED_ICE] = BlockIcePacked.class; //174
            list[DOUBLE_PLANT] = BlockDoublePlant.class; //175

            list[DAYLIGHT_DETECTOR_INVERTED] = BlockDaylightDetectorInverted.class; //178
            list[RED_SANDSTONE] = BlockRedSandstone.class; //179
            list[RED_SANDSTONE_STAIRS] = BlockStairsRedSandstone.class; //180
            list[DOUBLE_RED_SANDSTONE_SLAB] = BlockDoubleSlabRedSandstone.class; //181
            list[RED_SANDSTONE_SLAB] = BlockSlabRedSandstone.class; //182
            list[FENCE_GATE_SPRUCE] = BlockFenceGateSpruce.class; //183
            list[FENCE_GATE_BIRCH] = BlockFenceGateBirch.class; //184
            list[FENCE_GATE_JUNGLE] = BlockFenceGateJungle.class; //185
            list[FENCE_GATE_DARK_OAK] = BlockFenceGateDarkOak.class; //186
            list[FENCE_GATE_ACACIA] = BlockFenceGateAcacia.class; //187

            list[SPRUCE_DOOR_BLOCK] = BlockDoorSpruce.class; //193
            list[BIRCH_DOOR_BLOCK] = BlockDoorBirch.class; //194
            list[JUNGLE_DOOR_BLOCK] = BlockDoorJungle.class; //195
            list[ACACIA_DOOR_BLOCK] = BlockDoorAcacia.class; //196
            list[DARK_OAK_DOOR_BLOCK] = BlockDoorDarkOak.class; //197
            list[GRASS_PATH] = BlockGrassPath.class; //198
            list[ITEM_FRAME_BLOCK] = BlockItemFrame.class; //199
            //TODO: list[CHORUS_FLOWER] = BlockChorusFlower.class; //200
            list[PURPUR_BLOCK] = BlockPurpur.class; //201

            list[PURPUR_STAIRS] = BlockStairsPurpur.class; //203

            list[END_BRICKS] = BlockBricksEndStone.class; //206

            list[END_ROD] = BlockEndRod.class; //208
            list[END_GATEWAY] = BlockEndGateway.class; //209

            list[BONE_BLOCK] = BlockBone.class; //216

            //TODO: list[SHULKER_BOX] = BlockShulkerBox.class; //218
            list[PURPLE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPurple.class; //219
            list[WHITE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedWhite.class; //220
            list[ORANGE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedOrange.class; //221
            list[MAGENTA_GLAZED_TERRACOTTA] = BlockTerracottaGlazedMagenta.class; //222
            list[LIGHT_BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLightBlue.class; //223
            list[YELLOW_GLAZED_TERRACOTTA] = BlockTerracottaGlazedYellow.class; //224
            list[LIME_GLAZED_TERRACOTTA] = BlockTerracottaGlazedLime.class; //225
            list[PINK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedPink.class; //226
            list[GRAY_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGray.class; //227
            list[SILVER_GLAZED_TERRACOTTA] = BlockTerracottaGlazedSilver.class; //228
            list[CYAN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedCyan.class; //229

            list[BLUE_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlue.class; //231
            list[BROWN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBrown.class; //232
            list[GREEN_GLAZED_TERRACOTTA] = BlockTerracottaGlazedGreen.class; //233
            list[RED_GLAZED_TERRACOTTA] = BlockTerracottaGlazedRed.class; //234
            list[BLACK_GLAZED_TERRACOTTA] = BlockTerracottaGlazedBlack.class; //235
            list[CONCRETE] = BlockConcrete.class; //236
            list[CONCRETE_POWDER] = BlockConcretePowder.class; //237

            //TODO: list[CHORUS_PLANT] = BlockChorusPlant.class; //240
            list[STAINED_GLASS] = BlockGlassStained.class; //241
            list[PODZOL] = BlockPodzol.class; //243
            list[BEETROOT_BLOCK] = BlockBeetroot.class; //244
            list[GLOWING_OBSIDIAN] = BlockObsidianGlowing.class; //246
            //TODO: list[NETHER_REACTOR] = BlockNetherReactor.class; //247 Should not be removed

            //TODO: list[PISTON_EXTENSION] = BlockPistonExtension.class; //250

            //TODO: list[OBSERVER] = BlockObserver.class; //251

            for (int id = 0; id < 256; id++) {
                Class c = list[id];
                if (c != null) {
                    Block block;
                    try {
                        block = (Block) c.newInstance();
                        try {
                            Constructor constructor = c.getDeclaredConstructor(int.class);
                            constructor.setAccessible(true);
                            for (int data = 0; data < 16; ++data) {
                                fullList[(id << 4) | data] = (Block) constructor.newInstance(data);
                            }
                            hasMeta[id] = true;
                        } catch (NoSuchMethodException ignore) {
                            for (int data = 0; data < 16; ++data) {
                                fullList[(id << 4) | data] = block;
                            }
                        }
                    } catch (Exception e) {
                        Server.getInstance().getLogger().error("Error while registering " + c.getName(), e);
                        for (int data = 0; data < 16; ++data) {
                            fullList[(id << 4) | data] = new BlockUnknown(id, data);
                        }
                        return;
                    }

                    solid[id] = block.isSolid();
                    transparent[id] = block.isTransparent();
                    hardness[id] = block.getHardness();
                    light[id] = block.getLightLevel();

                    if (block.isSolid()) {
                        if (block.isTransparent()) {
                            if (block instanceof BlockLiquid || block instanceof BlockIce) {
                                lightFilter[id] = 2;
                            } else {
                                lightFilter[id] = 1;
                            }
                        } else {
                            lightFilter[id] = 15;
                        }
                    } else {
                        lightFilter[id] = 1;
                    }
                } else {
                    lightFilter[id] = 1;
                    for (int data = 0; data < 16; ++data) {
                        fullList[(id << 4) | data] = new BlockUnknown(id, data);
                    }
                }
            }
        }
    }

    public static Block get(int id) {
        return fullList[id << 4].clone();
    }

    public static Block get(int id, Integer meta) {
        if (meta != null) {
            return fullList[(id << 4) + meta].clone();
        } else {
            return fullList[id << 4].clone();
        }
    }

    @SuppressWarnings("unchecked")
    public static Block get(int id, Integer meta, Position pos) {
        Block block = fullList[(id << 4) | (meta == null ? 0 : meta)].clone();
        if (pos != null) {
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
        }
        return block;
    }

    public static Block get(int id, int data) {
        return fullList[(id << 4) + data].clone();
    }

    public static Block get(int fullId, Level level, int x, int y, int z) {
        Block block = fullList[fullId].clone();
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = level;
        return block;
    }

    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, true);
    }

    //http://minecraft.gamepedia.com/Breaking
    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public int tickRate() {
        return 10;
    }

    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, new BlockAir(), true, true);
    }

    public int onUpdate(int type) {
        return 0;
    }

    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(Item item, Player player) {
        return false;
    }

    public double getHardness() {
        return 10;
    }

    public double getResistance() {
        return 1;
    }

    public int getBurnChance() {
        return 0;
    }

    public int getBurnAbility() {
        return 0;
    }

    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    public double getFrictionFactor() {
        return 0.6;
    }

    public int getLightLevel() {
        return 0;
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean canBeFlowedInto() {
        return false;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    public boolean canBePushed() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    public boolean canBeClimbed() {
        return false;
    }

    public BlockColor getColor() {
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public abstract String getName();

    public abstract int getId();

    /**
     * The full id is a combination of the id and data.
     * @return
     */
    public int getFullId() {
        return (getId() << 4);
    }

    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    public int getDamage() {
        return 0;
    }

    public void setDamage(int meta) {
        // Do nothing
    }

    public final void setDamage(Integer meta) {
        setDamage((meta == null ? 0 : meta & 0x0f));
    }

    final public void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
    }

    public Item[] getDrops(Item item) {
        if (this.getId() < 0 || this.getId() > list.length) { //Unknown blocks
            return new Item[0];
        } else {
            return new Item[]{
                    this.toItem()
            };
        }
    }

    private static double toolBreakTimeBonus0(
            int toolType, int toolTier, boolean isWoolBlock, boolean isCobweb) {
        if (toolType == ItemTool.TYPE_SWORD) return isCobweb ? 15.0 : 1.0;
        if (toolType == ItemTool.TYPE_SHEARS) return isWoolBlock ? 5.0 : 15.0;
        if (toolType == ItemTool.TYPE_NONE) return 1.0;
        switch (toolTier) {
            case ItemTool.TIER_WOODEN:
                return 2.0;
            case ItemTool.TIER_STONE:
                return 4.0;
            case ItemTool.TIER_IRON:
                return 6.0;
            case ItemTool.TIER_DIAMOND:
                return 8.0;
            case ItemTool.TIER_GOLD:
                return 12.0;
            default:
                return 1.0;
        }
    }

    private static double speedBonusByEfficiencyLore0(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0) return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private static double speedRateByHasteLore0(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

    private static int toolType0(Item item) {
        if (item.isSword()) return ItemTool.TYPE_SWORD;
        if (item.isShovel()) return ItemTool.TYPE_SHOVEL;
        if (item.isPickaxe()) return ItemTool.TYPE_PICKAXE;
        if (item.isAxe()) return ItemTool.TYPE_AXE;
        if (item.isShears()) return ItemTool.TYPE_SHEARS;
        return ItemTool.TYPE_NONE;
    }

    private static boolean correctTool0(int blockToolType, Item item) {
        return (blockToolType == ItemTool.TYPE_SWORD && item.isSword()) ||
                (blockToolType == ItemTool.TYPE_SHOVEL && item.isShovel()) ||
                (blockToolType == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                (blockToolType == ItemTool.TYPE_AXE && item.isAxe()) ||
                (blockToolType == ItemTool.TYPE_SHEARS && item.isShears()) ||
                blockToolType == ItemTool.TYPE_NONE;
    }

    //http://minecraft.gamepedia.com/Breaking
    private static double breakTime0(double blockHardness, boolean correctTool, boolean canHarvestWithHand,
                                     int blockId, int toolType, int toolTier, int efficiencyLoreLevel, int hasteEffectLevel,
                                     boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5 : 5.0) * blockHardness;
        double speed = 1.0 / baseTime;
        boolean isWoolBlock = blockId == Block.WOOL, isCobweb = blockId == Block.COBWEB;
        if (correctTool) speed *= toolBreakTimeBonus0(toolType, toolTier, isWoolBlock, isCobweb);
        speed += speedBonusByEfficiencyLore0(efficiencyLoreLevel);
        speed *= speedRateByHasteLore0(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity) speed *= 0.2;
        if (outOfWaterButNotOnGround) speed *= 0.2;
        return 1.0 / speed;
    }

    public double getBreakTime(Item item, Player player) {
        Objects.requireNonNull(item, "getBreakTime: Item can not be null");
        Objects.requireNonNull(player, "getBreakTime: Player can not be null");
        double blockHardness = getHardness();
        boolean correctTool = correctTool0(getToolType(), item);
        boolean canHarvestWithHand = canHarvestWithHand();
        int blockId = getId();
        int itemToolType = toolType0(item);
        int itemTier = item.getTier();
        int efficiencyLoreLevel = Optional.ofNullable(item.getEnchantment(Enchantment.ID_EFFICIENCY))
                .map(Enchantment::getLevel).orElse(0);
        int hasteEffectLevel = Optional.ofNullable(player.getEffect(Effect.HASTE))
                .map(Effect::getAmplifier).orElse(0);
        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater() &&
                Optional.ofNullable(player.getInventory().getHelmet().getEnchantment(Enchantment.ID_WATER_WORKER))
                        .map(Enchantment::getLevel).map(l -> l >= 1).orElse(false);
        boolean outOfWaterButNotOnGround = (!player.isInsideOfWater()) && (!player.isOnGround());
        return breakTime0(blockHardness, correctTool, canHarvestWithHand, blockId, itemToolType, itemTier,
                efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround);
    }

    /**
     * @deprecated This function is lack of Player class and is not accurate enough, use #getBreakTime(Item, Player)
     */
    @Deprecated
    public double getBreakTime(Item item) {
        double base = this.getHardness() * 1.5;
        if (this.canBeBrokenWith(item)) {
            if (this.getToolType() == ItemTool.TYPE_SHEARS && item.isShears()) {
                base /= 15;
            } else if (
                    (this.getToolType() == ItemTool.TYPE_PICKAXE && item.isPickaxe()) ||
                            (this.getToolType() == ItemTool.TYPE_AXE && item.isAxe()) ||
                            (this.getToolType() == ItemTool.TYPE_SHOVEL && item.isShovel())
                    ) {
                int tier = item.getTier();
                switch (tier) {
                    case ItemTool.TIER_WOODEN:
                        base /= 2;
                        break;
                    case ItemTool.TIER_STONE:
                        base /= 4;
                        break;
                    case ItemTool.TIER_IRON:
                        base /= 6;
                        break;
                    case ItemTool.TIER_DIAMOND:
                        base /= 8;
                        break;
                    case ItemTool.TIER_GOLD:
                        base /= 12;
                        break;
                }
            }
        } else {
            base *= 3.33;
        }

        if (item.isSword()) {
            base *= 0.5;
        }

        return base;
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    public Block getSide(BlockFace face) {
        if (this.isValid()) {
            return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset());
        }
        return this.getSide(face, 1);
    }

    public Block getSide(BlockFace face, int step) {
        if (this.isValid()) {
            if (step == 1) {
                return this.getLevel().getBlock((int) x + face.getXOffset(), (int) y + face.getYOffset(), (int) z + face.getZOffset());
            } else {
                return this.getLevel().getBlock((int) x + face.getXOffset() * step, (int) y + face.getYOffset() * step, (int) z + face.getZOffset() * step);
            }
        }
        Block block = Block.get(Item.AIR, 0);
        block.x = (int) x + face.getXOffset() * step;
        block.y = (int) y + face.getYOffset() * step;
        block.z = (int) z + face.getZOffset() * step;
        return block;
    }

    public Block up() {
        return up(1);
    }

    public Block up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Block down() {
        return down(1);
    }

    public Block down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Block north() {
        return north(1);
    }

    public Block north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Block south() {
        return south(1);
    }

    public Block south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Block east() {
        return east(1);
    }

    public Block east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Block west() {
        return west(1);
    }

    public Block west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    @Override
    public String toString() {
        return "Block[" + this.getName() + "] (" + this.getId() + ":" + this.getDamage() + ")";
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB bb1 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    public void onEntityCollide(Entity entity) {

    }

    public AxisAlignedBB getBoundingBox() {
        return this.recalculateBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return this.recalculateCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z;
    }

    @Override
    public double getMaxX() {
        return this.x + 1;
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1;
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return getBoundingBox();
    }

    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        AxisAlignedBB bb = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, bb.getMinX());
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, bb.getMaxX());
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, bb.getMinY());
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, bb.getMaxY());
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, bb.getMinZ());
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, bb.getMaxZ());

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 vector = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int f = -1;

        if (vector == v1) {
            f = 4;
        } else if (vector == v2) {
            f = 5;
        } else if (vector == v3) {
            f = 0;
        } else if (vector == v4) {
            f = 1;
        } else if (vector == v5) {
            f = 2;
        } else if (vector == v6) {
            f = 3;
        }

        return MovingObjectPosition.fromBlock((int) this.x, (int) this.y, (int) this.z, f, vector.add(this.x, this.y, this.z));
    }

    public String getSaveId() {
        String name = getClass().getName();
        return name.substring(16, name.length());
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    public Block clone() {
        return (Block) super.clone();
    }

    public int getWeakPower(BlockFace face) {
        return 0;
    }

    public int getStrongPower(BlockFace side) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public String getLocationHash() {
        return this.getFloorX() + ":" + this.getFloorY() + ":" + this.getFloorZ();
    }

    public int getDropExp() {
        return 0;
    }

    public boolean isNormalBlock() {
        return !isTransparent() && isSolid() && !isPowerSource();
    }

    public static boolean equals(Block b1, Block b2) {
        return equals(b1, b2, true);
    }

    public static boolean equals(Block b1, Block b2, boolean checkDamage) {
        return b1 != null && b2 != null && b1.getId() == b2.getId() && (!checkDamage || b1.getDamage() == b2.getDamage());
    }

    public Item toItem() {
        return new ItemBlock(this, this.getDamage(), 1);
    }
}

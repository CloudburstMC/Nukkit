package cn.nukkit.inventory;

import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.utils.Utils;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    public Map<UUID, Recipe> recipes = new HashMap<>();

    protected Map<String, Map<String, Recipe>> recipeLookup = new HashMap<>();

    public Map<String, FurnaceRecipe> furnaceRecipes = new HashMap<>();

    public Map<String, BrewingRecipe> brewingRecipes = new HashMap<>();

    private static int RECIPE_COUNT = 0;

    public CraftingManager() {
        this.registerStonecutter();
        this.registerFurnace();
        this.registerBrewing();

        this.registerDyes();
        this.registerIngots();
        this.registerTools();
        this.registerWeapons();
        this.registerArmor();
        this.registerFood();
        this.registerWoodenDoors();

        this.registerRecipe((new ShapedRecipe(Item.get(Item.CLAY_BLOCK, 0, 1),
                "XX ",
                "XX ",
                "   "
        )).setIngredient("X", Item.get(Item.CLAY, 0, 4)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WORKBENCH, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.WOODEN_PLANK, null)));

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.GLOWSTONE_BLOCK, 0, 1))).addIngredient(Item.get(Item.GLOWSTONE_DUST, 0, 4)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.LIT_PUMPKIN, 0, 1))).addIngredient(Item.get(Item.PUMPKIN, 0, 1)).addIngredient(Item.get(Item.TORCH, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.SNOW_BLOCK, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.SNOWBALL)));

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.SNOW_LAYER, 0, 6))).addIngredient(Item.get(Item.SNOW_BLOCK, 0, 3)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.STICK, 0, 4),
                "X ",
                "X "
        )).setIngredient("X", Item.get(Item.WOODEN_PLANK, null)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.STONECUTTER, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.COBBLESTONE)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, BlockWood.OAK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, BlockWood.SPRUCE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, BlockWood.BIRCH, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, BlockWood.JUNGLE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD2, BlockWood2.ACACIA, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD2, BlockWood2.DARK_OAK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOOL, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.STRING, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.TORCH, 0, 4),
                "C ",
                "S"
        )).setIngredient("C", Item.get(Item.COAL, 0, 1)).setIngredient("S", Item.get(Item.STICK, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.TORCH, 0, 4),
                "C ",
                "S"
        )).setIngredient("C", Item.get(Item.COAL, 1, 1)).setIngredient("S", Item.get(Item.STICK, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.SUGAR, 0, 1),
                "S"
        )).setIngredient("S", Item.get(Item.SUGARCANE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BED, 0, 1),
                "WWW",
                "PPP",
                "   "
        )).setIngredient("W", Item.get(Item.WOOL, null, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.CHEST, 0, 1),
                "PPP",
                "P P",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 1)));
        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ENCHANTMENT_TABLE, 0, 1),
                " B ",
                "DOD",
                "OOO"
        )).setIngredient("D", Item.get(Item.DIAMOND, 0, 2)).setIngredient("O", Item.get(Item.OBSIDIAN, 0, 4)).setIngredient("B", Item.get(Item.BOOK, 0, 1)));
        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, 0, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.SPRUCE, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.BIRCH, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.JUNGLE, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.ACACIA, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.DARK_OAK, 3),
                "PSP",
                "PSP",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_SPRUCE, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_BIRCH, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_JUNGLE, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_DARK_OAK, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_ACACIA, 0, 1),
                "SPS",
                "SPS",
                "   "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FURNACE, 0, 1),
                "CCC",
                "C C",
                "CCC"
        )).setIngredient("C", Item.get(Item.COBBLESTONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GLASS_PANE, 0, 16),
                "GGG",
                "GGG",
                "   "
        )).setIngredient("G", Item.get(Item.GLASS, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.LADDER, 0, 2),
                "S S",
                "SSS",
                "S S"
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.TRAPDOOR, 0, 2),
                "PPP",
                "PPP",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.IRON_DOOR, 0, 1),
                "II ",
                "II ",
                "II "
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 0, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 1, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 2, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 3, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 4, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 5, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.OAK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SPRUCE_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.SPRUCE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BIRCH_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.BIRCH, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.JUNGLE_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.JUNGLE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ACACIA_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.ACACIA, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.DARK_OAK_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.DARK_OAK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BUCKET, 0, 1),
                "I I",
                " I ",
                "   "
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.CLOCK, 0, 1),
                " G ",
                "GRG",
                " G "
        )).setIngredient("G", Item.get(Item.GOLD_INGOT, 0, 1)).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COMPASS, 0, 1),
                " I ",
                "IRI",
                " I "
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.TNT, 0, 1),
                "GSG",
                "SGS",
                "GSG"
        )).setIngredient("G", Item.get(Item.GUNPOWDER, 0, 1)).setIngredient("S", Item.get(Item.SAND, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOWL, 0, 4),
                "P P",
                " P ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANKS, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.MINECART, 0, 1),
                "I I",
                "III"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOOK, 0, 1),
                "P P",
                " P "
        )).setIngredient("P", Item.get(Item.PAPER, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOOKSHELF, 0, 1),
                "PPP",
                "BBB",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 1)).setIngredient("B", Item.get(Item.BOOK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PAINTING, 0, 1),
                "SSS",
                "SWS",
                "SSS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("W", Item.get(Item.WOOL, null, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.PAPER, 0, 3),
                "SS",
                "S"
        )).setIngredient("S", Item.get(Item.SUGARCANE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SIGN, 0, 3),
                "PPP",
                "PPP",
                " S"
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANKS, null, 1))); //TODO: check if it gives one sign or three

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.IRON_BARS, 0, 16),
                "III",
                "III",
                "III"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GLASS_BOTTLE, 0, 3),
                "I I",
                " I ",
                "   "
        )).setIngredient("I", Item.get(Item.GLASS, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BREWING_STAND, 0, 1),
                " I ",
                "CCC"
        )).setIngredient("C", Item.get(Item.COBBLESTONE, 0, 1)).setIngredient("I", Item.get(Item.BLAZE_ROD, 0, 1)));


    }

    protected void registerFurnace() {
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STONE, 0, 1), Item.get(Item.COBBLESTONE, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STONE_BRICK, 2, 1), Item.get(Item.STONE_BRICK, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.GLASS, 0, 1), Item.get(Item.SAND, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COAL, 1, 1), Item.get(Item.TRUNK, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.GOLD_INGOT, 0, 1), Item.get(Item.GOLD_ORE, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.IRON_INGOT, 0, 1), Item.get(Item.IRON_ORE, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.EMERALD, 0, 1), Item.get(Item.EMERALD_ORE, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DIAMOND, 0, 1), Item.get(Item.DIAMOND_ORE, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.NETHER_BRICK, 0, 1), Item.get(Item.NETHERRACK, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_PORKCHOP, 0, 1), Item.get(Item.RAW_PORKCHOP, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.BRICK, 0, 1), Item.get(Item.CLAY, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_FISH, 0, 1), Item.get(Item.RAW_FISH, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_FISH, 1, 1), Item.get(Item.RAW_FISH, 1, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DYE, 2, 1), Item.get(Item.CACTUS, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DYE, 1, 1), Item.get(Item.RED_MUSHROOM, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STEAK, 0, 1), Item.get(Item.RAW_BEEF, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_CHICKEN, 0, 1), Item.get(Item.RAW_CHICKEN, 0, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.BAKED_POTATO, 0, 1), Item.get(Item.POTATO, 0, 1)));

        this.registerRecipe(new FurnaceRecipe(Item.get(Item.HARDENED_CLAY, 0, 1), Item.get(Item.CLAY_BLOCK, 0, 1)));
    }

    protected void registerBrewing() {
        for (int ingredient : new int[]{Item.NETHER_WART, Item.GOLD_NUGGET, Item.GHAST_TEAR, Item.GLOWSTONE_DUST, Item.REDSTONE_DUST, Item.GUNPOWDER, Item.MAGMA_CREAM, Item.BLAZE_POWDER, Item.GOLDEN_CARROT, Item.SPIDER_EYE, Item.FERMENTED_SPIDER_EYE, Item.GLISTERING_MELON, Item.SUGAR, Item.RAW_FISH}) {
            BlockEntityBrewingStand.ingredients.add(ingredient); //temporally solution for ingredients
        }

        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.AWKWARD, 1), Item.get(Item.NETHER_WART, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.THICK, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.MUNDANE_II, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH, 1), Item.get(Item.BLAZE_POWDER, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.STRENGTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.STRENGTH_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.WEAKNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NO_EFFECTS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.WEAKNESS_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.WEAKNESS, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1), Item.get(Item.GOLDEN_CARROT, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.NIGHT_VISION_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.INVISIBLE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INVISIBLE_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.NIGHT_VISION_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1), Item.get(Item.MAGMA_CREAM, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.LEAPING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.FIRE_RESISTANCE_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SLOWNESS_LONG, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED_LONG, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED, 1), Item.get(Item.SUGAR, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.SPEED_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.SPEED, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1), Item.get(Item.GLISTERING_MELON, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON, 1), Item.get(Item.SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.POISON_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION, 1), Item.get(Item.GHAST_TEAR, 0, 1), Item.get(Item.POTION, ItemPotion.AWKWARD, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION_LONG, 1), Item.get(Item.REDSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.REGENERATION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.REGENERATION_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.REGENERATION, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.WATER_BREATHING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.POISON, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.GLOWSTONE_DUST, 0, 1), Item.get(Item.POTION, ItemPotion.HARMING, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.INSTANT_HEALTH_II, 1)));
        registerBrewingRecipe(new BrewingRecipe(Item.get(Item.POTION, ItemPotion.HARMING_II, 1), Item.get(Item.FERMENTED_SPIDER_EYE, 0, 1), Item.get(Item.POTION, ItemPotion.POISON_LONG, 1)));
    }

    protected void registerStonecutter() {
        Map<String, String[]> shapes = new HashMap<String, String[]>() {
            {
                put("slab", new String[]{
                        "   ",
                        "XXX",
                        "   "
                });
                put("stairs", new String[]{
                        "X  ",
                        "XX ",
                        "XXX"
                });
                put("wall/fence", new String[]{
                        "XXX",
                        "XXX",
                        "   "
                });
                put("blockrecipe1", new String[]{
                        "XX",
                        "XX"
                });
                put("blockrecipe2X1", new String[]{
                        "   ",
                        " X ",
                        " X "
                });
                put("blockrecipe2X2", new String[]{
                        "AB",
                        "BA"
                });
                put("blockrecipe1X2", new String[]{
                        "  ",
                        "AB"
                });
            }
        };

        List<Recipe> buildRecipes = new ArrayList<>();

        Entry[] recipes = new Entry[]{
                new Entry(Item.SLAB, BlockSlab.STONE, Item.STONE, BlockStone.NORMAL, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.COBBLESTONE, Item.COBBLESTONE, 0, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.SANDSTONE, Item.SANDSTONE, 0, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.BRICK, Item.BRICK, 0, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.STONE_BRICK, Item.STONE_BRICK, BlockBricksStone.NORMAL, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.NETHER_BRICK, Item.NETHER_BRICK_BLOCK, 0, "slab", 6),
                new Entry(Item.SLAB, BlockSlab.QUARTZ, Item.QUARTZ_BLOCK, 0, "slab", 6),
                new Entry(Item.COBBLESTONE_STAIRS, 0, Item.COBBLESTONE, 0, "stairs", 4),
                new Entry(Item.SANDSTONE_STAIRS, 0, Item.SANDSTONE, 0, "stairs", 4),
                new Entry(Item.STONE_BRICK_STAIRS, 0, Item.STONE_BRICK, BlockBricksStone.NORMAL, "stairs", 4),
                new Entry(Item.BRICK_STAIRS, 0, Item.BRICKS_BLOCK, 0, "stairs", 4),
                new Entry(Item.NETHER_BRICKS_STAIRS, 0, Item.NETHER_BRICK_BLOCK, 0, "stairs", 4),
                new Entry(Item.COBBLESTONE_WALL, BlockWall.NONE_MOSSY_WALL, Item.COBBLESTONE, 0, "wall/fence", 6),
                new Entry(Item.COBBLESTONE_WALL, BlockWall.MOSSY_WALL, Item.MOSSY_STONE, 0, "wall/fence", 6),
                new Entry(Item.NETHER_BRICK_FENCE, 0, Item.NETHER_BRICK_BLOCK, 0, "wall/fence", 6),
                new Entry(Item.NETHER_BRICKS, 0, Item.NETHER_BRICK, 0, "blockrecipe1", 1),
                new Entry(Item.SANDSTONE, BlockSandstone.NORMAL, Item.SAND, 0, "blockrecipe1", 1),
                new Entry(Item.SANDSTONE, BlockSandstone.CHISELED, Item.SANDSTONE, BlockSandstone.NORMAL, "blockrecipe1", 4),
                new Entry(Item.STONE_BRICK, BlockBricksStone.NORMAL, Item.STONE, BlockStone.NORMAL, "blockrecipe1", 4),
                new Entry(Item.STONE_BRICK, BlockBricksStone.NORMAL, Item.STONE, BlockStone.POLISHED_GRANITE, "blockrecipe1", 4),
                new Entry(Item.STONE_BRICK, BlockBricksStone.NORMAL, Item.STONE, BlockStone.POLISHED_DIORITE, "blockrecipe1", 4),
                new Entry(Item.STONE_BRICK, BlockBricksStone.NORMAL, Item.STONE, BlockStone.POLISHED_ANDESITE, "blockrecipe1", 4),
                new Entry(Item.STONE, BlockStone.POLISHED_GRANITE, Item.STONE, BlockStone.GRANITE, "blockrecipe1", 4),
                new Entry(Item.STONE, BlockStone.POLISHED_DIORITE, Item.STONE, BlockStone.DIORITE, "blockrecipe1", 4),
                new Entry(Item.STONE, BlockStone.POLISHED_ANDESITE, Item.STONE, BlockStone.ANDESITE, "blockrecipe1", 4),
                new Entry(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_NORMAL, Item.QUARTZ, BlockStone.ANDESITE, "blockrecipe1", 4),
                new Entry(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_CHISELED, Item.SLAB, BlockSlab.QUARTZ, "blockrecipe2X1", 1),
                new Entry(Item.SANDSTONE, BlockSandstone.CHISELED, Item.SLAB, BlockSlab.SANDSTONE, "blockrecipe2X1", 1),
                new Entry(Item.STONE_BRICK, BlockBricksStone.CHISELED, Item.SLAB, BlockSlab.STONE_BRICK, "blockrecipe2X1", 1)
        };

        for (Entry recipe : recipes) {
            buildRecipes.add(this.createOneIngredientStonecutterRecipe(
                    shapes.get(recipe.recipeShape),
                    recipe.resultItemId,
                    recipe.resultMeta,
                    recipe.resultAmount,
                    recipe.ingredientItemId,
                    recipe.ingredientMeta,
                    'X'));
        }

        this.sortAndAddRecipesArray(buildRecipes.stream().toArray(Recipe[]::new));
    }

    private void sortAndAddRecipesArray(Recipe[] recipes) {
        for (int i = 0; i < recipes.length; i++) {
            Recipe current = recipes[i];
            Item result = current.getResult();

            for (int j = recipes.length - 1; j > i; --j) {
                if (this.comparator.compare(result, recipes[j].getResult()) > 0) {
                    Recipe sawp = current;
                    current = recipes[j];
                    recipes[j] = sawp;
                    result = current.getResult();
                }
            }

            this.registerRecipe(current);
        }
    }

    private ShapedRecipe createOneIngredientStonecutterRecipe(String[] recipeShape, int resultItemId, Integer resultItemMeta, int resultItemAmount, int ingredientId, Integer ingredientMeta, char ingredient) {
        int ingredientAmount = 0;
        int height = 0;

        for (String line : recipeShape) {
            height++;
            for (int i = 0; i < line.length(); i++) {
                if (ingredient == line.charAt(i)) {
                    ingredientAmount++;
                }
            }
        }

        ShapedRecipe recipe;

        if (height < 3) {
            recipe = ((new StonecutterShapedRecipe(Item.get(resultItemId, resultItemMeta, resultItemAmount), recipeShape)).setIngredient(ingredient, Item.get(ingredientId, ingredientMeta, ingredientAmount)));
        } else {
            recipe = ((new StonecutterBigShapedRecipe(Item.get(resultItemId, resultItemMeta, resultItemAmount), recipeShape)).setIngredient(ingredient, Item.get(ingredientId, ingredientMeta, ingredientAmount)));
        }

        return recipe;
    }

    protected void registerFood() {
        //TODO: check COOKIES
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.MELON_SEEDS, 0, 1))).addIngredient(Item.get(Item.MELON_SLICE, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.PUMPKIN_SEEDS, 0, 4))).addIngredient(Item.get(Item.PUMPKIN, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.PUMPKIN_PIE, 0, 1))).addIngredient(Item.get(Item.PUMPKIN, 0, 1)).addIngredient(Item.get(Item.EGG, 0, 1)).addIngredient(Item.get(Item.SUGAR, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.MUSHROOM_STEW, 0, 1))).addIngredient(Item.get(Item.BOWL, 0, 1)).addIngredient(Item.get(Item.BROWN_MUSHROOM, 0, 1)).addIngredient(Item.get(Item.RED_MUSHROOM, 0, 1)));
        this.registerRecipe((new BigShapelessRecipe(Item.get(Item.MELON_BLOCK, 0, 1))).addIngredient(Item.get(Item.MELON_SLICE, 0, 9)));
        this.registerRecipe((new BigShapelessRecipe(Item.get(Item.BEETROOT_SOUP, 0, 1))).addIngredient(Item.get(Item.BEETROOT, 0, 4)).addIngredient(Item.get(Item.BOWL, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.BREAD, 0, 1))).addIngredient(Item.get(Item.WHEAT, 0, 3)));
        this.registerRecipe((new BigShapelessRecipe(Item.get(Item.CAKE, 0, 1))).addIngredient(Item.get(Item.WHEAT, 0, 3)).addIngredient(Item.get(Item.BUCKET, 1, 3)).addIngredient(Item.get(Item.EGG, 0, 1)).addIngredient(Item.get(Item.SUGAR, 0, 2)));
    }

    protected void registerArmor() {
        int[][] types = {
                {Item.LEATHER, Item.FIRE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.LEATHER_CAP, Item.CHAIN_HELMET, Item.IRON_HELMET, Item.DIAMOND_HELMET, Item.GOLD_HELMET},
                {Item.LEATHER_TUNIC, Item.CHAIN_CHESTPLATE, Item.IRON_CHESTPLATE, Item.DIAMOND_CHESTPLATE, Item.GOLD_CHESTPLATE},
                {Item.LEATHER_PANTS, Item.CHAIN_LEGGINGS, Item.IRON_LEGGINGS, Item.DIAMOND_LEGGINGS, Item.GOLD_LEGGINGS},
                {Item.LEATHER_BOOTS, Item.CHAIN_BOOTS, Item.IRON_BOOTS, Item.DIAMOND_BOOTS, Item.GOLD_BOOTS},
        };

        String[][] shapes = new String[][]{
                new String[]{
                        "XXX",
                        "X X",
                        "   "
                },
                new String[]{
                        "X X",
                        "XXX",
                        "XXX"
                },
                new String[]{
                        "XXX",
                        "X X",
                        "X X"
                },
                new String[]{
                        "   ",
                        "X X",
                        "X X"
                }
        };
        for (int i = 1; i < 5; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapedRecipe(Item.get(type, 0, 1), shapes[i - 1])).setIngredient("X", Item.get(types[0][j], 0, 1)));
            }
        }
    }

    protected void registerWeapons() {
        int[][] types = {
                {Item.WOODEN_PLANK, Item.COBBLESTONE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.WOODEN_SWORD, Item.STONE_SWORD, Item.IRON_SWORD, Item.DIAMOND_SWORD, Item.GOLD_SWORD},
        };
        for (int i = 1; i < 2; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapedRecipe(Item.get(type, 0, 1),
                        " X ",
                        " X ",
                        " I "
                )).setIngredient("X", Item.get(types[0][j], null)).setIngredient("I", Item.get(Item.STICK)));
            }
        }

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ARROW, 0, 1),
                " F ",
                " S ",
                " P "
        )).setIngredient("S", Item.get(Item.STICK)).setIngredient("F", Item.get(Item.FLINT)).setIngredient("P", Item.get(Item.FEATHER)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOW, 0, 1),
                " X~",
                "X ~",
                " X~"
        )).setIngredient("~", Item.get(Item.STRING)).setIngredient("X", Item.get(Item.STICK)));
    }

    protected void registerTools() {
        int[][] types = {
                {Item.WOODEN_PLANK, Item.COBBLESTONE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.WOODEN_PICKAXE, Item.STONE_PICKAXE, Item.IRON_PICKAXE, Item.DIAMOND_PICKAXE, Item.GOLD_PICKAXE},
                {Item.WOODEN_SHOVEL, Item.STONE_SHOVEL, Item.IRON_SHOVEL, Item.DIAMOND_SHOVEL, Item.GOLD_SHOVEL},
                {Item.WOODEN_AXE, Item.STONE_AXE, Item.IRON_AXE, Item.DIAMOND_AXE, Item.GOLD_AXE},
                {Item.WOODEN_HOE, Item.STONE_HOE, Item.IRON_HOE, Item.DIAMOND_HOE, Item.GOLD_HOE},
        };
        String[][] shapes = new String[][]{
                new String[]{
                        "XXX",
                        " I ",
                        " I "
                },
                new String[]{
                        " X ",
                        " I ",
                        " I "
                },
                new String[]{
                        "XX ",
                        "XI ",
                        " I "
                },
                new String[]{
                        "XX ",
                        " I ",
                        " I "
                }
        };
        for (int i = 1; i < 5; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapedRecipe(Item.get(type, 0, 1), shapes[i - 1])).setIngredient('X', Item.get(types[0][j], null)).setIngredient('I', Item.get(Item.STICK)));
            }
        }

        this.registerRecipe((new ShapedRecipe(Item.get(Item.FLINT_AND_STEEL, 0, 1),
                " S",
                "F "
        )).setIngredient('F', Item.get(Item.FLINT)).setIngredient("S", Item.get(Item.IRON_INGOT)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.SHEARS, 0, 1),
                " X",
                "X "
        )).setIngredient("X", Item.get(Item.IRON_INGOT)));
    }

    protected void registerWoodenDoors() {
        int[] items = new int[]{Item.WOODEN_DOOR, Item.SPRUCE_DOOR, Item.BIRCH_DOOR, Item.JUNGLE_DOOR, Item.ACACIA_DOOR, Item.DARK_OAK_DOOR};

        for (int i = 0; i < 6; i++) {
            this.registerRecipe((new BigShapedRecipe(Item.get(items[i], 0, 1),
                    "XX ",
                    "XX ",
                    "XX "
            )).setIngredient("X", Item.get(Item.PLANKS, i, 1)));
        }
    }

    protected void registerDyes() {
        for (int i = 0; i < 16; ++i) {
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.STAINED_CLAY, 15 - i, 8))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.HARDENED_CLAY, 0, 8)));
            //TODO: add glass things?
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));

            this.registerRecipe((new ShapelessRecipe(Item.get(Item.CARPET, i, 3))).addIngredient(Item.get(Item.WOOL, i, 2)));
        }

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 11, 2))).addIngredient(Item.get(Item.DANDELION, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 15, 3))).addIngredient(Item.get(Item.BONE, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 3, 2))).addIngredient(Item.get(Item.DYE, 14, 1)).addIngredient(Item.get(Item.DYE, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 3, 3))).addIngredient(Item.get(Item.DYE, 1, 1)).addIngredient(Item.get(Item.DYE, 0, 1)).addIngredient(Item.get(Item.DYE, 11, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 9, 2))).addIngredient(Item.get(Item.DYE, 15, 1)).addIngredient(Item.get(Item.DYE, 1, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 14, 2))).addIngredient(Item.get(Item.DYE, 11, 1)).addIngredient(Item.get(Item.DYE, 1, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 10, 2))).addIngredient(Item.get(Item.DYE, 2, 1)).addIngredient(Item.get(Item.DYE, 15, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 12, 2))).addIngredient(Item.get(Item.DYE, 4, 1)).addIngredient(Item.get(Item.DYE, 15, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 6, 2))).addIngredient(Item.get(Item.DYE, 4, 1)).addIngredient(Item.get(Item.DYE, 2, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 5, 2))).addIngredient(Item.get(Item.DYE, 4, 1)).addIngredient(Item.get(Item.DYE, 1, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 13, 3))).addIngredient(Item.get(Item.DYE, 4, 1)).addIngredient(Item.get(Item.DYE, 1, 1)).addIngredient(Item.get(Item.DYE, 15, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 1, 1))).addIngredient(Item.get(Item.BEETROOT, 0, 1)));

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 13, 4))).addIngredient(Item.get(Item.DYE, 15, 1)).addIngredient(Item.get(Item.DYE, 1, 2)).addIngredient(Item.get(Item.DYE, 4, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 13, 2))).addIngredient(Item.get(Item.DYE, 5, 1)).addIngredient(Item.get(Item.DYE, 9, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 8, 2))).addIngredient(Item.get(Item.DYE, 0, 1)).addIngredient(Item.get(Item.DYE, 15, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 7, 3))).addIngredient(Item.get(Item.DYE, 0, 1)).addIngredient(Item.get(Item.DYE, 15, 2)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 7, 2))).addIngredient(Item.get(Item.DYE, 0, 1)).addIngredient(Item.get(Item.DYE, 8, 1)));

    }

    protected void registerIngots() {
        int[][] ingots = new int[][]{
                {Item.GOLD_BLOCK, Item.GOLD_INGOT},
                {Item.GOLD_INGOT, Item.GOLD_NUGGET},
                {Item.IRON_BLOCK, Item.IRON_INGOT},
                {Item.DIAMOND_BLOCK, Item.DIAMOND},
                {Item.EMERALD_BLOCK, Item.EMERALD},
                {Item.REDSTONE_BLOCK, Item.REDSTONE_DUST},
                {Item.COAL_BLOCK, Item.COAL},
                {Item.HAY_BALE, Item.WHEAT},
                {Item.LAPIS_BLOCK, Item.DYE, 4}
        };

        for (int[] e : ingots) {
            int block = e[0];
            int ingot = e[1];
            int ingot_meta = e.length > 2 ? e[2] : 0;

            this.registerRecipe((new ShapedRecipe(Item.get(ingot, ingot_meta, 9),
                    "X"
            )).setIngredient("X", Item.get(block, 0, 1)));

            this.registerRecipe((new BigShapedRecipe(Item.get(block, 0, 1),
                    "XXX",
                    "XXX",
                    "XXX"
            )).setIngredient("X", Item.get(ingot, ingot_meta, 1)));
        }

    }

    public final Comparator<Item> comparator = new Comparator<Item>() {
        @Override
        public int compare(Item i1, Item i2) {
            if (i1.getId() > i2.getId()) {
                return 1;
            } else if (i1.getId() < i2.getId()) {
                return -1;
            } else if (i1.getDamage() > i2.getDamage()) {
                return 1;
            } else if (i1.getDamage() < i2.getDamage()) {
                return -1;
            } else if (i1.getCount() > i2.getCount()) {
                return 1;
            } else if (i1.getCount() < i2.getCount()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public Recipe getRecipe(UUID id) {
        return this.recipes.containsKey(id) ? this.recipes.get(id) : null;
    }

    public Map<UUID, Recipe> getRecipes() {
        return recipes;
    }

    public Map<String, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        if (this.furnaceRecipes.containsKey(input.getId() + ":" + input.getDamage())) {
            return this.furnaceRecipes.get(input.getId() + ":" + input.getDamage());
        } else if (this.furnaceRecipes.containsKey(input.getId() + ":?")) {
            return this.furnaceRecipes.get(input.getId() + ":?");
        }

        return null;
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        Item result = recipe.getResult();
        this.recipes.put(recipe.getId(), recipe);
        Map<Integer, Map<Integer, Item>> ingredients = recipe.getIngredientMap();
        String hash = "";
        for (Map<Integer, Item> v : ingredients.values()) {
            for (Item item : v.values()) {
                if (item != null) {
                    hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
                }
            }

            hash += ";";
        }

        String index = result.getId() + ":" + (result.hasMeta() ? result.getDamage() : "");
        if (!this.recipeLookup.containsKey(index)) {
            this.recipeLookup.put(index, new HashMap<>());
        }

        this.recipeLookup.get(index).put(hash, recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        Item result = recipe.getResult();
        this.recipes.put(recipe.getId(), recipe);
        String hash = "";
        List<Item> ingredients = recipe.getIngredientList();
        Collections.sort(ingredients, this.comparator);
        for (Item item : ingredients) {
            hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        if (!this.recipeLookup.containsKey(result.getId() + ":" + result.getDamage())) {
            this.recipeLookup.put(result.getId() + ":" + result.getDamage(), new HashMap<>());
        }
        this.recipeLookup.get(result.getId() + ":" + result.getDamage()).put(hash, recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(input.getId() + ":" + (!input.hasMeta() ? "?" : input.getDamage()), recipe);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getInput();
        Item potion = recipe.getPotion();

        this.brewingRecipes.put(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        if (brewingRecipes.containsKey(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()))) {
            return brewingRecipes.get(input.getId() + ":" + (!potion.hasMeta() ? 0 : potion.getDamage()));
        }
        return null;
    }

    public boolean matchRecipe(ShapelessRecipe recipe) {
        String idx = recipe.getResult().getId() + ":" + recipe.getResult().getDamage();
        if (!this.recipeLookup.containsKey(idx)) {
            return false;
        }

        String hash = "";
        List<Item> ingredients = recipe.getIngredientList();
        Collections.sort(ingredients, this.comparator);
        for (Item item : ingredients) {
            hash += item.getId() + ":" + (!item.hasMeta() ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        if (this.recipeLookup.get(idx).containsKey(hash)) {
            return true;
        }

        Recipe hasRecipe = null;

        for (Recipe r : this.recipeLookup.get(idx).values()) {
            if (r instanceof ShapelessRecipe) {
                if (((ShapelessRecipe) r).getIngredientCount() != ingredients.size()) {
                    continue;
                }
                List<Item> checkInput = ((ShapelessRecipe) r).getIngredientList();
                for (Item item : ingredients) {
                    int amount = item.getCount();
                    for (Item checkItem : checkInput) {
                        if (checkItem.equals(item, checkItem.hasMeta())) {
                            int remove = Math.min(checkItem.getCount(), amount);
                            checkItem.setCount(checkItem.getCount() - amount);
                            if (checkItem.getCount() == 0) {
                                checkInput.remove(checkItem);
                            }
                            amount -= remove;
                            if (amount == 0) {
                                break;
                            }
                        }
                    }
                }

                if (checkInput.isEmpty()) {
                    hasRecipe = r;
                    break;
                }
            }
        }
        return hasRecipe != null;
    }

    public void registerRecipe(Recipe recipe) {
        recipe.setId(Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag())));

        if (recipe instanceof ShapedRecipe) {
            this.registerShapedRecipe((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            this.registerShapelessRecipe((ShapelessRecipe) recipe);
        } else if (recipe instanceof FurnaceRecipe) {
            this.registerFurnaceRecipe((FurnaceRecipe) recipe);
        }
    }

    public static class Entry {
        int resultItemId;
        int resultMeta;
        int ingredientItemId;
        int ingredientMeta;
        String recipeShape;
        int resultAmount;

        public Entry(int resultItemId, int resultMeta, int ingredientItemId, int ingredientMeta, String recipeShape, int resultAmount) {
            this.resultItemId = resultItemId;
            this.resultMeta = resultMeta;
            this.ingredientItemId = ingredientItemId;
            this.ingredientMeta = ingredientMeta;
            this.recipeShape = recipeShape;
            this.resultAmount = resultAmount;
        }
    }
}

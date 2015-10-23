package cn.nukkit.inventory;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
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

    private static int RECIPE_COUNT = 0;

    public CraftingManager() {
        this.registerStonecutter();
        this.registerFurnace();


        this.registerDyes();
        this.registerIngots();
        this.registerTools();
        this.registerWeapons();
        this.registerArmor();
        this.registerFood();

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.CLAY_BLOCK, 0, 1))).addIngredient(Item.get(Item.CLAY, 0, 4)));

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

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.OAK, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, Wood.OAK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.SPRUCE, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, Wood.SPRUCE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.BIRCH, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, Wood.BIRCH, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.JUNGLE, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD, Wood.JUNGLE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.ACACIA, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD2, Wood2.ACACIA, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOODEN_PLANK, Planks.DARK_OAK, 4),
                "X"
        )).setIngredient("X", Item.get(Item.WOOD2, Wood2.DARK_OAK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WOOL, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.STRING, 0, 4)));

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
                "PPP"
        )).setIngredient("W", Item.get(Item.WOOL, null, 3)).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.CHEST, 0, 1),
                "PPP",
                "P P",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 8)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, 0, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.OAK, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, Planks.SPRUCE, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.SPRUCE, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, Planks.BIRCH, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.BIRCH, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, Planks.JUNGLE, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.JUNGLE, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, Planks.ACACIA, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.ACACIA, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, Planks.DARK_OAK, 3),
                "PSP",
                "PSP"
        )).setIngredient("S", Item.get(Item.STICK, 0, 2)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.DARK_OAK, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.OAK, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_SPRUCE, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.SPRUCE, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_BIRCH, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.BIRCH, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_JUNGLE, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.JUNGLE, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_DARK_OAK, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.DARK_OAK, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE_GATE_ACACIA, 0, 1),
                "SPS",
                "SPS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 4)).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.ACACIA, 2)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FURNACE, 0, 1),
                "CCC",
                "C C",
                "CCC"
        )).setIngredient("C", Item.get(Item.COBBLESTONE, 0, 8)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GLASS_PANE, 0, 16),
                "GGG",
                "GGG"
        )).setIngredient("G", Item.get(Item.GLASS, 0, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.LADDER, 0, 2),
                "S S",
                "SSS",
                "S S"
        )).setIngredient("S", Item.get(Item.STICK, 0, 7)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.NETHER_REACTOR, 0, 1),
                "IDI",
                "IDI",
                "IDI"
        )).setIngredient("D", Item.get(Item.DIAMOND, 0, 3)).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.TRAPDOOR, 0, 2),
                "PPP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_DOOR, 0, 1),
                "PP",
                "PP",
                "PP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.OAK, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.OAK, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.OAK, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SPRUCE_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.SPRUCE, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.SPRUCE, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.SPRUCE, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BIRCH_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.BIRCH, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.BIRCH, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.BIRCH, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.JUNGLE_WOOD_STAIRS, 0, 4),
                "P",
                "PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.JUNGLE, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.JUNGLE, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.JUNGLE, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ACACIA_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.ACACIA, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.ACACIA, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.ACACIA, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.DARK_OAK_WOOD_STAIRS, 0, 4),
                "  P",
                " PP",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.DARK_OAK, 6)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, Planks.DARK_OAK, 6),
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, Planks.DARK_OAK, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BUCKET, 0, 1),
                "I I",
                " I"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.CLOCK, 0, 1),
                " G",
                "GR",
                " G"
        )).setIngredient("G", Item.get(Item.GOLD_INGOT, 0, 4)).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COMPASS, 0, 1),
                " I ",
                "IRI",
                " I"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 4)).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.TNT, 0, 1),
                "GSG",
                "SGS",
                "GSG"
        )).setIngredient("G", Item.get(Item.GUNPOWDER, 0, 5)).setIngredient("S", Item.get(Item.SAND, null, 4)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOWL, 0, 4),
                "P P",
                " P"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANKS, null, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.MINECART, 0, 1),
                "I I",
                "III"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 5)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOOK, 0, 1),
                "P P",
                " P "
        )).setIngredient("P", Item.get(Item.PAPER, 0, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOOKSHELF, 0, 1),
                "PBP",
                "PBP",
                "PBP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, null, 6)).setIngredient("B", Item.get(Item.BOOK, 0, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PAINTING, 0, 1),
                "SSS",
                "SWS",
                "SSS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 8)).setIngredient("W", Item.get(Item.WOOL, null, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.PAPER, 0, 3),
                "SS",
                "S"
        )).setIngredient("S", Item.get(Item.SUGARCANE, 0, 3)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SIGN, 0, 3),
                "PPP",
                "PPP",
                " S"
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANKS, null, 6))); //TODO: check if it gives one sign or three

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.IRON_BARS, 0, 16),
                "III",
                "III",
                "III"
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 9)));
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

        int RESULT_ITEMID = 0;
        int RESULT_META = 1;
        int INGREDIENT_ITEMID = 2;
        int INGREDIENT_META = 3;
        int RECIPE_SHAPE = 4;
        int RESULT_AMOUNT = 5;

        Object[][] recipes = new Object[][]{
                new Object[]{Item.SLAB, Slab.STONE, Item.STONE, Stone.NORMAL, "slab", 6},
                new Object[]{Item.SLAB, Slab.COBBLESTONE, Item.COBBLESTONE, 0, "slab", 6},
                new Object[]{Item.SLAB, Slab.SANDSTONE, Item.SANDSTONE, 0, "slab", 6},
                new Object[]{Item.SLAB, Slab.BRICK, Item.BRICK, 0, "slab", 6},
                new Object[]{Item.SLAB, Slab.STONE_BRICK, Item.STONE_BRICK, StoneBricks.NORMAL, "slab", 6},
                new Object[]{Item.SLAB, Slab.NETHER_BRICK, Item.NETHER_BRICK_BLOCK, 0, "slab", 6},
                new Object[]{Item.SLAB, Slab.QUARTZ, Item.QUARTZ_BLOCK, 0, "slab", 6},
                new Object[]{Item.COBBLESTONE_STAIRS, 0, Item.COBBLESTONE, 0, "stairs", 4},
                new Object[]{Item.SANDSTONE_STAIRS, 0, Item.SANDSTONE, 0, "stairs", 4},
                new Object[]{Item.STONE_BRICK_STAIRS, 0, Item.STONE_BRICK, StoneBricks.NORMAL, "stairs", 4},
                new Object[]{Item.BRICK_STAIRS, 0, Item.BRICKS_BLOCK, 0, "stairs", 4},
                new Object[]{Item.NETHER_BRICKS_STAIRS, 0, Item.NETHER_BRICK_BLOCK, 0, "stairs", 4},
                new Object[]{Item.COBBLESTONE_WALL, StoneWall.NONE_MOSSY_WALL, Item.COBBLESTONE, 0, "wall/fence", 6},
                new Object[]{Item.COBBLESTONE_WALL, StoneWall.MOSSY_WALL, Item.MOSSY_STONE, 0, "wall/fence", 6},
                new Object[]{Item.NETHER_BRICK_FENCE, 0, Item.NETHER_BRICK_BLOCK, 0, "wall/fence", 6},
                new Object[]{Item.NETHER_BRICKS, 0, Item.NETHER_BRICK, 0, "blockrecipe1", 1},
                new Object[]{Item.SANDSTONE, SandStone.NORMAL, Item.SAND, 0, "blockrecipe1", 1},
                new Object[]{Item.SANDSTONE, SandStone.CHISELED, Item.SANDSTONE, SandStone.NORMAL, "blockrecipe1", 4},
                new Object[]{Item.STONE_BRICK, StoneBricks.NORMAL, Item.STONE, Stone.NORMAL, "blockrecipe1", 4},
                new Object[]{Item.STONE_BRICK, StoneBricks.NORMAL, Item.STONE, Stone.POLISHED_GRANITE, "blockrecipe1", 4},
                new Object[]{Item.STONE_BRICK, StoneBricks.NORMAL, Item.STONE, Stone.POLISHED_DIORITE, "blockrecipe1", 4},
                new Object[]{Item.STONE_BRICK, StoneBricks.NORMAL, Item.STONE, Stone.POLISHED_ANDESITE, "blockrecipe1", 4},
                new Object[]{Item.STONE, Stone.POLISHED_GRANITE, Item.STONE, Stone.GRANITE, "blockrecipe1", 4},
                new Object[]{Item.STONE, Stone.POLISHED_DIORITE, Item.STONE, Stone.DIORITE, "blockrecipe1", 4},
                new Object[]{Item.STONE, Stone.POLISHED_ANDESITE, Item.STONE, Stone.ANDESITE, "blockrecipe1", 4},
                new Object[]{Item.QUARTZ_BLOCK, Quartz.QUARTZ_NORMAL, Item.QUARTZ, Stone.ANDESITE, "blockrecipe1", 4},
                new Object[]{Item.QUARTZ_BLOCK, Quartz.QUARTZ_CHISELED, Item.SLAB, Slab.QUARTZ, "blockrecipe2X1", 1},
                new Object[]{Item.SANDSTONE, SandStone.CHISELED, Item.SLAB, Slab.SANDSTONE, "blockrecipe2X1", 1},
                new Object[]{Item.STONE_BRICK, StoneBricks.CHISELED, Item.SLAB, Slab.STONE_BRICK, "blockrecipe2X1", 1},
        };

        for (Object[] recipe : recipes) {
            buildRecipes.add(this.createOneIngredientStonecutterRecipe(
                    shapes.get(recipe[RECIPE_SHAPE]),
                    (int) recipe[RESULT_ITEMID],
                    (int) recipe[RESULT_META],
                    (int) recipe[RESULT_AMOUNT],
                    (int) recipe[INGREDIENT_ITEMID],
                    (int) recipe[INGREDIENT_META],
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

        ShapedRecipe recipe = null;

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
        Map<Integer, Integer> ingots = new HashMap<Integer, Integer>() {
            {
                put(Item.GOLD_BLOCK, Item.GOLD_INGOT);
                put(Item.IRON_BLOCK, Item.IRON_INGOT);
                put(Item.DIAMOND_BLOCK, Item.DIAMOND);
                put(Item.EMERALD_BLOCK, Item.EMERALD);
                put(Item.REDSTONE_BLOCK, Item.REDSTONE_DUST);
                put(Item.COAL_BLOCK, Item.COAL);
                put(Item.HAY_BALE, Item.WHEAT);
            }
        };

        for (int block : ingots.keySet()) {
            int ingot = ingots.get(block);
            this.registerRecipe((new BigShapelessRecipe(Item.get(block, 0, 1))).addIngredient(Item.get(ingot, 0, 9)));
            this.registerRecipe((new ShapelessRecipe(Item.get(ingot, 0, 9))).addIngredient(Item.get(block, 0, 1)));
        }


        this.registerRecipe((new BigShapelessRecipe(Item.get(Item.LAPIS_BLOCK, 0, 1))).addIngredient(Item.get(Item.DYE, 4, 9)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.DYE, 4, 9))).addIngredient(Item.get(Item.LAPIS_BLOCK, 0, 1)));

        this.registerRecipe((new BigShapelessRecipe(Item.get(Item.GOLD_INGOT, 0, 1))).addIngredient(Item.get(Item.GOLD_NUGGET, 0, 9)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.GOLD_NUGGET, 0, 9))).addIngredient(Item.get(Item.GOLD_INGOT, 0, 1)));

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
                    hash += item.getId() + ":" + (item.getDamage() == null ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
                }
            }

            hash += ";";
        }
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        Item result = recipe.getResult();
        this.recipes.put(recipe.getId(), recipe);
        String hash = "";
        List<Item> ingredients = recipe.getIngredientList();
        Collections.sort(ingredients, this.comparator);
        for (Item item : ingredients) {
            hash += item.getId() + ":" + (item.getDamage() == null ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        if (!this.recipeLookup.containsKey(result.getId() + ":" + result.getDamage())) {
            this.recipeLookup.put(result.getId() + ":" + result.getDamage(), new HashMap<>());
        }
        this.recipeLookup.get(result.getId() + ":" + result.getDamage()).put(hash, recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(input.getId() + ":" + (input.getDamage() == null ? "?" : input.getDamage()), recipe);
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
            hash += item.getId() + ":" + (item.getDamage() == null ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
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
                        if (checkItem.equals(item, checkItem.getDamage() != null)) {
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
}

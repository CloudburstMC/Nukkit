package cn.nukkit.inventory;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.utils.Utils;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    public final Map<UUID, Recipe> recipes = new HashMap<>();

    protected final Map<String, Map<String, Recipe>> recipeLookup = new HashMap<>();

    public final Map<String, FurnaceRecipe> furnaceRecipes = new HashMap<>();

    public final Map<String, BrewingRecipe> brewingRecipes = new HashMap<>();

    private static int RECIPE_COUNT = 0;

    public CraftingManager() {
        /*try { //TODO: json
            Utils.writeFile(Server.getInstance().getDataPath() + "recipes.json", Server.class.getClassLoader().getResourceAsStream("recipes.json"));
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }


        Config recipes = new Config(Server.getInstance().getDataPath() + "recipes.json", Config.JSON);

        MainLogger.getLogger().info("Loading recipes...");
        for (Object obj : recipes.getAll().values()) {
            ConfigSection recipe;
            if (obj instanceof ConfigSection) {
                recipe = (ConfigSection) obj;
            } else {
                continue;
            }


            switch (recipe.getInt("type")) {
                case 0:
                    // TODO: handle multiple result items
                    Map<String, Object> first = recipe.getMapList("output").get(0);
                    ShapelessRecipe result = new ShapelessRecipe(Item.get((int) first.get("id"), (int) first.get("damage"), (int) first.get("count"), first.get("nbt").toString().getBytes()));
                    for (Map<String, Object> ingredient : recipe.getMapList("input")) {
                        result.addIngredient(Item.get((int) ingredient.get("id"), (int) ingredient.get("damage"), (int) ingredient.get("count"), ingredient.get("nbt").toString().getBytes()));
                    }
                    this.registerRecipe(result);
                    break;
                case 1:
                    // TODO: handle multiple result items
                    first = recipe.getMapList("output").get(0);
                    ShapedRecipe shapedRecipe = new ShapedRecipe(Item.get((int) first.get("id"), (int) first.get("damage"), (int) first.get("count"), first.get("nbt").toString().getBytes()));
                    List<List<Map<String, Object>>> shape = Utils.toChunk(recipe.getList("input"), recipe.getInt("width"));

                    for (int y = 0; y < shape.size(); y++) {
                        List<Map<String, Object>> row = shape.get(y);

                        for (int x = 0; x < row.size(); x++) {
                            Map<String, Object> ingredient = row.get(x);
                            shapedRecipe.addIngredient(x, y, Item.get((int) ingredient.get("id"), (int) ingredient.get("damage"), (int) ingredient.get("count"), ingredient.get("nbt").toString().getBytes()));
                        }
                    }
                    this.registerRecipe(shapedRecipe);
                    break;
                case 2:
                case 3:
                    ConfigSection resultMap = recipe.getSection("output");
                    Item resultItem = Item.get(resultMap.getInt("id"), resultMap.getInt("damage"), resultMap.getInt("count"), resultMap.getString("nbt").getBytes());
                    this.registerRecipe(new FurnaceRecipe(resultItem, Item.get(recipe.getInt("id"), recipe.getInt("damage", -1), 1)));
                    break;
                default:
                    break;
            }
        }*/

        this.registerFurnace();
        this.registerBrewing();
        this.registerDyes();
        this.registerIngots();
        this.registerTools();
        this.registerWeapons();
        this.registerArmor();
        this.registerFood();
        this.registerWoodenDoors();

        this.registerRecipe((new ShapedRecipe(Item.get(Item.PRISMARINE, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.PRISMARINE_SHARD)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PRISMARINE, 1, 1),
                "XXX",
                "XXX",
                "XXX"
        )).setIngredient("X", Item.get(Item.PRISMARINE_SHARD)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PRISMARINE, 2, 1),
                "XXX",
                "XIX",
                "XXX"
        )).setIngredient("X", Item.get(Item.PRISMARINE_SHARD)).setIngredient("I", Item.get(Item.DYE)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PRISMARINE, 2, 1),
                "XXX",
                "XIX",
                "XXX"
        )).setIngredient("X", Item.get(Item.PRISMARINE_SHARD)).setIngredient("I", Item.get(Item.DYE)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SEA_LANTERN, 0, 1),
                "XSX",
                "SSS",
                "XSX"
        )).setIngredient("X", Item.get(Item.PRISMARINE_SHARD)).setIngredient("S", Item.get(Item.PRISMARINE_CRYSTALS)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BEACON, 0, 1),
                "GGG",
                "GSG",
                "OOO"
        )).setIngredient("G", Item.get(Item.GLASS)).setIngredient("S", Item.get(Item.NETHER_STAR)).setIngredient("O", Item.get(Item.OBSIDIAN)));


        this.registerRecipe((new ShapedRecipe(Item.get(Item.CLAY_BLOCK, 0, 1),
                "XX ",
                "XX ",
                "   "
        )).setIngredient("X", Item.get(Item.CLAY, 0, 4)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.WORKBENCH, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.WOODEN_PLANK, null)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.GLOWSTONE_BLOCK, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.GLOWSTONE_DUST, 0, 1)));
        this.registerRecipe((new ShapedRecipe(Item.get(Item.LIT_PUMPKIN, 0, 1),
                "X ",
                "Y "
        )).setIngredient("X", Item.get(Item.PUMPKIN, 0, 1)).setIngredient("Y", Item.get(Item.TORCH, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.SNOW_BLOCK, 0, 1),
                "XX",
                "XX"
        )).setIngredient("X", Item.get(Item.SNOWBALL, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.SNOW_LAYER, 0, 6),
                "   ",
                "XXX",
                "   "
        )).setIngredient("X", Item.get(Item.SNOW_BLOCK, 0, 1)));


        this.registerRecipe((new ShapedRecipe(Item.get(Item.STICK, 0, 4),
                "X ",
                "X "
        )).setIngredient("X", Item.get(Item.WOODEN_PLANK, null)));

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

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FENCE, BlockPlanks.OAK, 3),
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

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.NETHER_BRICK_FENCE, 0, 3),
                "PPP",
                "PPP",
                "   "
        )).setIngredient("P", Item.get(Item.NETHER_BRICK_BLOCK, 0, 1)));

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

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COBBLESTONE_WALL, BlockWall.NONE_MOSSY_WALL, 6),
                "CCC",
                "CCC",
                "   "
        )).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COBBLESTONE_WALL, BlockWall.MOSSY_WALL, 6),
                "MMM",
                "MMM",
                "   "
        )).setIngredient("M", Item.get(Item.MOSS_STONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.FURNACE, 0, 1),
                "CCC",
                "C C",
                "CCC"
        )).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GLASS_PANE, 0, 16),
                "GGG",
                "GGG",
                "   "
        )).setIngredient("G", Item.get(Item.GLASS, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.LADDER, 0, 3),
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
        )).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 0, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 1, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 2, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 3, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 4, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BOAT, 5, 1),
                "PSP",
                "PPP",
                "   "
        )).setIngredient("S", Item.get(Item.WOODEN_SHOVEL, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SANDSTONE_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.SANDSTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SPRUCE_WOOD_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BIRCH_WOOD_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.JUNGLE_WOOD_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ACACIA_WOOD_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.DARK_OAK_WOOD_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COBBLESTONE_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.COBBLESTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BRICK_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.BRICKS_BLOCK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE_BRICK_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.STONE_BRICK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.QUARTZ_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.QUARTZ_BLOCK, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.NETHER_BRICKS_STAIRS, 0, 4),
                "P  ",
                "PP ",
                "PPP"
        )).setIngredient("P", Item.get(Item.NETHER_BRICK_BLOCK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.STONE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.STONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.SANDSTONE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.SANDSTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.OAK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.SPRUCE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.BIRCH, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.JUNGLE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.ACACIA, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOOD_SLAB, BlockPlanks.DARK_OAK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.WOODEN_PLANK, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.COBBLESTONE, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.COBBLESTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.BRICK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.BRICK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.STONE_BRICK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.STONE_BRICK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.QUARTZ, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.QUARTZ_BLOCK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SLAB, BlockSlabStone.NETHER_BRICK, 6),
                "PPP",
                "   ",
                "   "
        )).setIngredient("P", Item.get(Item.NETHER_BRICK_BLOCK, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_NORMAL, 1),
                "NN ",
                "NN ",
                "   "
        )).setIngredient("N", Item.get(Item.NETHER_QUARTZ, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_PILLAR, 2),
                "N  ",
                "N  ",
                "   "
        )).setIngredient("N", Item.get(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_NORMAL, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.QUARTZ_BLOCK, BlockQuartz.QUARTZ_CHISELED, 1),
                "   ",
                " N ",
                " N "
        )).setIngredient("N", Item.get(Item.SLAB, BlockSlabStone.QUARTZ, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SANDSTONE, BlockSandstone.NORMAL, 1),
                "   ",
                "SS ",
                "SS "
        )).setIngredient("S", Item.get(Item.SAND, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SANDSTONE, BlockSandstone.SMOOTH, 4),
                "   ",
                "SS ",
                "SS "
        )).setIngredient("S", Item.get(Item.SANDSTONE, BlockSandstone.NORMAL, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SANDSTONE, BlockSandstone.CHISELED, 1),
                "   ",
                " N ",
                " N "
        )).setIngredient("N", Item.get(Item.SLAB, BlockSlabStone.SANDSTONE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE_BRICK, BlockBricksStone.NORMAL, 4),
                "   ",
                "SS ",
                "SS "
        )).setIngredient("S", Item.get(Item.STONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE_BRICK, BlockBricksStone.MOSSY, 1),
                "   ",
                "SL ",
                "   "
        )).setIngredient("S", Item.get(Item.STONE_BRICK, BlockBricksStone.NORMAL, 1)).setIngredient("L", Item.get(Item.VINES, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE_BRICK, BlockBricksStone.CHISELED, 1),
                "   ",
                " S ",
                " S "
        )).setIngredient("S", Item.get(Item.SLAB, BlockSlabStone.STONE_BRICK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.MOSS_STONE, 0, 1),
                "   ",
                "SL ",
                "   "
        )).setIngredient("S", Item.get(Item.COBBLESTONE, null, 1)).setIngredient("L", Item.get(Item.VINES, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.GRANITE, 1),
                "   ",
                "DN ",
                "   "
        )).setIngredient("D", Item.get(Item.STONE, BlockStone.DIORITE, 1)).setIngredient("N", Item.get(Item.NETHER_QUARTZ, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.POLISHED_GRANITE, 4),
                "   ",
                "GG ",
                "GG "
        )).setIngredient("G", Item.get(Item.STONE, BlockStone.GRANITE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.DIORITE, 2),
                "   ",
                "CN ",
                "NC "
        )).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)).setIngredient("N", Item.get(Item.NETHER_QUARTZ, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.POLISHED_DIORITE, 4),
                "   ",
                "DD ",
                "DD "
        )).setIngredient("D", Item.get(Item.STONE, BlockStone.DIORITE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.ANDESITE, 2),
                "   ",
                "DC ",
                "   "
        )).setIngredient("D", Item.get(Item.STONE, BlockStone.DIORITE, 1)).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE, BlockStone.POLISHED_ANDESITE, 4),
                "   ",
                "AA ",
                "AA "
        )).setIngredient("A", Item.get(Item.STONE, BlockStone.ANDESITE, 1)));

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

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PAPER, 0, 3),
                "   ",
                "SSS",
                "   "
        )).setIngredient("S", Item.get(Item.SUGARCANE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.SIGN, 0, 3),
                "PPP",
                "PPP",
                " S "
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("P", Item.get(Item.WOODEN_PLANKS, null, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.IRON_BARS, 0, 16),
                "   ",
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
        )).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)).setIngredient("I", Item.get(Item.BLAZE_ROD, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.ITEM_FRAME, 0, 3),
                "SSS",
                "SLS",
                "SSS"
        )).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("L", Item.get(Item.LEATHER, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.RED_SANDSTONE_STAIRS, 0, 4),
                "  S",
                " SS",
                "SSS"
        )).setIngredient("S", Item.get(Item.RED_SANDSTONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.RED_SANDSTONE_SLAB, 0, 6),
                "   ",
                "SSS",
                "   "
        )).setIngredient("S", Item.get(Item.RED_SANDSTONE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.RED_SANDSTONE, BlockSandstone.NORMAL, 1),
                "   ",
                "SS ",
                "SS "
        )).setIngredient("S", Item.get(Item.SAND, 1, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.RED_SANDSTONE, BlockSandstone.SMOOTH, 4),
                "   ",
                "SS ",
                "SS "
        )).setIngredient("S", Item.get(Item.RED_SANDSTONE, BlockSandstone.NORMAL, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.RED_SANDSTONE, BlockSandstone.CHISELED, 1),
                "   ",
                " N ",
                " N "
        )).setIngredient("N", Item.get(Item.RED_SANDSTONE_SLAB, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.STONE_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.STONE, 0, 1)));


        //all planks...
        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.OAK, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.SPRUCE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.BIRCH, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.JUNGLE, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.ACACIA, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                "   ",
                " N ",
                "   "
        )).setIngredient("N", Item.get(Item.PLANKS, BlockPlanks.DARK_OAK, 1)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.ENDER_CHEST, 0, 1),
                "PPP",
                "PSP",
                "PPP"
        ).setIngredient("P", Item.get(Item.OBSIDIAN, 0, 1)).setIngredient("S", Item.get(Item.ENDER_EYE, 0, 1)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.REDSTONE_TORCH, 0, 1),
                "  R",
                "  S"
        ).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)).setIngredient("S", Item.get(Item.STICK, 0, 1)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.LEVER, 0, 1),
                "  S",
                "  C"
        ).setIngredient("C", Item.get(Item.COBBLESTONE, null, 1)).setIngredient("S", Item.get(Item.STICK, 0, 1)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.STONE_BUTTON, 0, 1),
                "  S"
        ).setIngredient("S", Item.get(Item.STONE, 0, 1)));

        for (int i = 0; i < 6; i++) {
            this.registerRecipe(new BigShapedRecipe(Item.get(Item.WOODEN_BUTTON, 0, 1),
                    "  W"
            ).setIngredient("W", Item.get(Item.PLANKS, i, 1)));

            this.registerRecipe(new BigShapedRecipe(Item.get(Item.WOODEN_PRESSURE_PLATE, 0, 1),
                    "WW "
            ).setIngredient("W", Item.get(Item.PLANKS, i, 2)));

            this.registerRecipe(new BigShapedRecipe(Item.get(Item.TRIPWIRE_HOOK, 0, 2),
                    " I ",
                    " S ",
                    " P "
            ).setIngredient("P", Item.get(Item.PLANKS, i, 1)).setIngredient("S", Item.get(Item.STICK, 0, 1)).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 1)));
        }

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.STONE_PRESSURE_PLATE, 0, 1),
                "SS "
        ).setIngredient("S", Item.get(Item.STONE, 0, 2)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.HEAVY_WEIGHTED_PRESSURE_PLATE, 0, 1),
                "II "
        ).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 2)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.LIGHT_WEIGHTED_PRESSURE_PLATE, 0, 1),
                "GG "
        ).setIngredient("G", Item.get(Item.GOLD_INGOT, 0, 2)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.REDSTONE_LAMP, 0, 1),
                " R ",
                "RGR",
                " R "
        ).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 4)).setIngredient("G", Item.get(Item.GLOWSTONE_BLOCK, 0, 1)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.REPEATER, 0, 1),
                "   ",
                "TRT",
                "SSS"
        ).setIngredient("R", Item.get(Item.REDSTONE_DUST, 0, 1)).setIngredient("T", Item.get(Item.REDSTONE_TORCH, 0, 2)).setIngredient("S", Item.get(Item.STONE, 0, 3)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.COMPARATOR, 0, 1),
                " T ",
                "TQT",
                "SSS"
        ).setIngredient("Q", Item.get(Item.QUARTZ, 0, 1)).setIngredient("T", Item.get(Item.REDSTONE_TORCH, 0, 3)).setIngredient("S", Item.get(Item.STONE, 0, 3)));

        this.registerRecipe(new BigShapedRecipe(Item.get(Item.HOPPER, 0, 1),
                "I I",
                "ICI",
                " I "
        ).setIngredient("I", Item.get(Item.IRON_INGOT, 0, 5)).setIngredient("C", Item.get(Item.CHEST, 0, 1)));
    }

    protected void registerFurnace() {
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STONE, 0, 1), Item.get(Item.COBBLESTONE, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STONE_BRICK, BlockBricksStone.CRACKED, 1), Item.get(Item.STONE_BRICK, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.GLASS, 0, 1), Item.get(Item.SAND, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COAL, 1, 1), Item.get(Item.TRUNK, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.GOLD_INGOT, 0, 1), Item.get(Item.GOLD_ORE, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.IRON_INGOT, 0, 1), Item.get(Item.IRON_ORE, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.EMERALD, 0, 1), Item.get(Item.EMERALD_ORE, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DIAMOND, 0, 1), Item.get(Item.DIAMOND_ORE, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.NETHER_BRICK, 0, 1), Item.get(Item.NETHERRACK, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_PORKCHOP, 0, 1), Item.get(Item.RAW_PORKCHOP, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.BRICK, 0, 1), Item.get(Item.CLAY, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_FISH, 0, 1), Item.get(Item.RAW_FISH, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_FISH, 1, 1), Item.get(Item.RAW_FISH, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DYE, 2, 1), Item.get(Item.CACTUS, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.DYE, 1, 1), Item.get(Item.RED_MUSHROOM, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.STEAK, 0, 1), Item.get(Item.RAW_BEEF, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_CHICKEN, 0, 1), Item.get(Item.RAW_CHICKEN, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.BAKED_POTATO, 0, 1), Item.get(Item.POTATO, null, 1)));
        this.registerRecipe(new FurnaceRecipe(Item.get(Item.COOKED_MUTTON, 0, 1), Item.get(Item.RAW_MUTTON, null, 1)));

        this.registerRecipe(new FurnaceRecipe(Item.get(Item.TERRACOTTA, 0, 1), Item.get(Item.CLAY_BLOCK, null, 1)));
    }

    protected void registerBrewing() {
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

    protected void registerFood() {
        this.registerRecipe((new BigShapedRecipe(Item.get(Item.MELON_BLOCK, 0, 1),
                "XXX",
                "XXX",
                "XXX"
        )).setIngredient("X", Item.get(Item.MELON_SLICE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BEETROOT_SOUP, 0, 1),
                "XXX",
                "XXX",
                " Y "
        )).setIngredient("X", Item.get(Item.BEETROOT, 0, 1)).setIngredient("Y", Item.get(Item.BOWL, 0, 1)));


        this.registerRecipe((new BigShapedRecipe(Item.get(Item.BREAD, 0, 1),
                "   ",
                "   ",
                "XXX"
        )).setIngredient("X", Item.get(Item.WHEAT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.CAKE, 0, 1),
                "XXX",
                "YZY",
                "AAA"
        )).setIngredient("X", Item.get(Item.BUCKET, 1, 1)).setIngredient("Y", Item.get(Item.SUGAR, 0, 1)).setIngredient("Z", Item.get(Item.EGG, 0, 1)).setIngredient("A", Item.get(Item.WHEAT, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.COOKIE, 0, 8),
                "   ",
                "   ",
                "XYX"
        )).setIngredient("X", Item.get(Item.WHEAT, 0, 1)).setIngredient("Y", Item.get(Item.DYE, 3, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GOLDEN_APPLE, 0, 1),
                "XXX",
                "XYX",
                "XXX"
        )).setIngredient("X", Item.get(Item.GOLD_INGOT, 0, 1)).setIngredient("Y", Item.get(Item.APPLE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.GOLDEN_APPLE_ENCHANTED, 1, 1),
                "XXX",
                "XYX",
                "XXX"
        )).setIngredient("X", Item.get(Item.GOLD_BLOCK, 0, 1)).setIngredient("Y", Item.get(Item.APPLE, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.MUSHROOM_STEW, 0, 1),
                " X ",
                " Y ",
                " Z "
        )).setIngredient("X", Item.get(Item.RED_MUSHROOM, 0, 1)).setIngredient("Y", Item.get(Item.BROWN_MUSHROOM, 0, 1)).setIngredient("Z", Item.get(Item.BOWL, 0, 1)));

        this.registerRecipe((new BigShapedRecipe(Item.get(Item.PUMPKIN_PIE, 0, 1),
                "   ",
                "XY ",
                "Z  "
        )).setIngredient("X", Item.get(Item.PUMPKIN, 0, 1)).setIngredient("Y", Item.get(Item.EGG, 0, 1)).setIngredient("Z", Item.get(Item.SUGAR, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.MELON_SEEDS, 0, 1),
                "X ",
                "  "
        )).setIngredient("X", Item.get(Item.MELON_SLICE, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.PUMPKIN_SEEDS, 0, 4),
                "X ",
                "  "
        )).setIngredient("X", Item.get(Item.PUMPKIN, 0, 1)));
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
            this.registerRecipe((new BigShapedRecipe(Item.get(items[i], 0, 3),
                    "XX ",
                    "XX ",
                    "XX "
            )).setIngredient("X", Item.get(Item.PLANKS, i, 1)));
        }
    }

    protected void registerPlanks() {
        //TODO
    }

    protected void registerDyes() {
        for (int i = 0; i < 16; ++i) {
            this.registerRecipe((new ShapedRecipe(Item.get(Item.WOOL, 15 - i, 1),
                    "  ",
                    "DW"
            )).setIngredient('D', Item.get(Item.DYE, i, 1)).setIngredient('W', Item.get(Item.WOOL, 0, 1)));

            this.registerRecipe((new BigShapedRecipe(Item.get(Item.STAINED_TERRACOTTA, 15 - i, 8),
                    "CCC",
                    "CDC",
                    "CCC"
            )).setIngredient('D', Item.get(Item.DYE, i, 1)).setIngredient('C', Item.get(Item.TERRACOTTA, 0, 1)));
            //TODO: add glass things?

            this.registerRecipe((new ShapedRecipe(Item.get(Item.CARPET, i, 3),
                    "  ",
                    "WW"
            )).setIngredient('W', Item.get(Item.WOOL, i, 1)));
        }

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.YELLOW, 2),
                "  ",
                "D "
        )).setIngredient('D', Item.get(Item.DANDELION, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.WHITE, 3),
                "  ",
                "B "
        )).setIngredient('B', Item.get(Item.BONE, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.BROWN, 2),
                " B",
                "O "
        )).setIngredient('O', Item.get(Item.DYE, ItemDye.ORANGE, 1)).setIngredient('B', Item.get(Item.DYE, ItemDye.BLACK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.BROWN, 3),
                "RB",
                "Y "
        )).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)).setIngredient('B', Item.get(Item.DYE, ItemDye.BLACK, 1)).setIngredient('Y', Item.get(Item.DYE, ItemDye.YELLOW, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.PINK, 2),
                " R",
                "W "
        )).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.ORANGE, 2),
                " R",
                "Y "
        )).setIngredient('Y', Item.get(Item.DYE, ItemDye.YELLOW, 1)).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.LIME, 2),
                " G",
                "W "
        )).setIngredient('G', Item.get(Item.DYE, ItemDye.GREEN, 1)).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.LIGHT_BLUE, 2),
                " B",
                "W "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLUE, 1)).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.CYAN, 2),
                " G",
                "B "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLUE, 1)).setIngredient('G', Item.get(Item.DYE, ItemDye.GREEN, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.PURPLE, 2),
                " R",
                "B "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLUE, 1)).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.MAGENTA, 3),
                "RW",
                "B "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLUE, 1)).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.RED, 1),
                "  ",
                "B "
        )).setIngredient('B', Item.get(Item.BEETROOT, 0, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.MAGENTA, 4),
                "RB",
                "WR"
        )).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)).setIngredient('R', Item.get(Item.DYE, ItemDye.RED, 1)).setIngredient('B', Item.get(Item.DYE, ItemDye.BLUE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.MAGENTA, 2),
                "  D",
                "P "
        )).setIngredient('P', Item.get(Item.DYE, ItemDye.PURPLE, 1)).setIngredient('D', Item.get(Item.DYE, ItemDye.PINK, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.GRAY, 2),
                " W",
                "B "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLACK, 1)).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.LIGHT_GRAY, 3),
                "B ",
                "WW"
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLACK, 1)).setIngredient('W', Item.get(Item.DYE, ItemDye.WHITE, 1)));

        this.registerRecipe((new ShapedRecipe(Item.get(Item.DYE, ItemDye.LIGHT_GRAY, 2),
                " G",
                "B "
        )).setIngredient('B', Item.get(Item.DYE, ItemDye.BLACK, 1)).setIngredient('G', Item.get(Item.DYE, ItemDye.GRAY, 1)));
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

    public final Comparator<Item> comparator = (i1, i2) -> {
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
                if (item != null && item.getId() != Item.AIR) {
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
        ingredients.sort(this.comparator);
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
        ingredients.sort(this.comparator);
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

    public Recipe[] getRecipesByResult(Item result) {
        return recipeLookup.get(result.getId() + ":" + result.getDamage()).values().stream().toArray(Recipe[]::new);
    }

    public static class Entry {
        final int resultItemId;
        final int resultMeta;
        final int ingredientItemId;
        final int ingredientMeta;
        final String recipeShape;
        final int resultAmount;

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

package cn.nukkit.inventory;

import cn.nukkit.block.Stone;
import cn.nukkit.item.Item;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftingManager {

    public Set<Recipe> recipes = new HashSet<>();

    protected Map<String, Map<String, Recipe>> recipeLookup = new HashMap<>();

    public Map<String, FurnaceRecipe> furnaceRecipes = new HashMap<>();

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
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.QUARTZ_BLOCK, 0, 1))).addIngredient(Item.get(Item.QUARTZ, 0, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.BRICK_STAIRS, 0, 4))).addIngredient(Item.get(Item.BRICKS_BLOCK, 0, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.BRICKS_BLOCK, 0, 1))).addIngredient(Item.get(Item.BRICK, 0, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 4, 6))).addIngredient(Item.get(Item.BRICKS_BLOCK, 0, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.QUARTZ_BLOCK, 1, 1))).addIngredient(Item.get(Item.SLAB, 6, 2)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 3, 6))).addIngredient(Item.get(Item.COBBLESTONE, 0, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.COBBLESTONE_WALL, 0, 6))).addIngredient(Item.get(Item.COBBLESTONE, 0, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.COBBLESTONE_WALL, 1, 6))).addIngredient(Item.get(Item.MOSS_STONE, 0, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.NETHER_BRICKS, 0, 1))).addIngredient(Item.get(Item.NETHER_BRICK, 0, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.NETHER_BRICKS_STAIRS, 0, 4))).addIngredient(Item.get(Item.NETHER_BRICKS, 0, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.QUARTZ_BLOCK, 2, 2))).addIngredient(Item.get(Item.QUARTZ_BLOCK, 0, 2)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 6, 6))).addIngredient(Item.get(Item.QUARTZ_BLOCK, 0, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SANDSTONE_STAIRS, 0, 4))).addIngredient(Item.get(Item.SANDSTONE, 0, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SANDSTONE, 0, 1))).addIngredient(Item.get(Item.SAND, 0, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SANDSTONE, 2, 4))).addIngredient(Item.get(Item.SANDSTONE, 0, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SANDSTONE, 1, 1))).addIngredient(Item.get(Item.SLAB, 1, 2)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 1, 6))).addIngredient(Item.get(Item.SANDSTONE, 0, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE_BRICK_STAIRS, 0, 4))).addIngredient(Item.get(Item.STONE_BRICK, null, 6)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE_BRICK, 0, 4))).addIngredient(Item.get(Item.STONE, null, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE_BRICK, 3, 1))).addIngredient(Item.get(Item.SLAB, 5, 2)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE_BRICK, 1, 1))).addIngredient(Item.get(Item.STONE_BRICK, 0, 1)).addIngredient(Item.get(Item.VINES, 0, 1)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 5, 6))).addIngredient(Item.get(Item.STONE_BRICK, null, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.SLAB, 0, 6))).addIngredient(Item.get(Item.STONE, null, 3)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.COBBLESTONE_STAIRS, 0, 4))).addIngredient(Item.get(Item.COBBLESTONE, 0, 6)));

        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.POLISHED_GRANITE, 4))).addIngredient(Item.get(Item.STONE, Stone.GRANITE, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.POLISHED_DIORITE, 4))).addIngredient(Item.get(Item.STONE, Stone.DIORITE, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.POLISHED_ANDESITE, 4))).addIngredient(Item.get(Item.STONE, Stone.ANDESITE, 4)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.GRANITE, 1))).addIngredient(Item.get(Item.STONE, Stone.DIORITE, 1)).addIngredient(Item.get(Item.QUARTZ, 0, 1)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.DIORITE, 2))).addIngredient(Item.get(Item.COBBLESTONE, 0, 2)).addIngredient(Item.get(Item.QUARTZ, 0, 2)));
        this.registerRecipe((new StonecutterShapelessRecipe(Item.get(Item.STONE, Stone.ANDESITE, 2))).addIngredient(Item.get(Item.COBBLESTONE, 0, 1)).addIngredient(Item.get(Item.STONE, Stone.DIORITE, 1)));
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
        int[] cost = new int[]{5, 8, 7, 4};
        int[][] types = {
                {Item.LEATHER, Item.FIRE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.LEATHER_CAP, Item.CHAIN_HELMET, Item.IRON_HELMET, Item.DIAMOND_HELMET, Item.GOLD_HELMET},
                {Item.LEATHER_TUNIC, Item.CHAIN_CHESTPLATE, Item.IRON_CHESTPLATE, Item.DIAMOND_CHESTPLATE, Item.GOLD_CHESTPLATE},
                {Item.LEATHER_PANTS, Item.CHAIN_LEGGINGS, Item.IRON_LEGGINGS, Item.DIAMOND_LEGGINGS, Item.GOLD_LEGGINGS},
                {Item.LEATHER_BOOTS, Item.CHAIN_BOOTS, Item.IRON_BOOTS, Item.DIAMOND_BOOTS, Item.GOLD_BOOTS},
        };
        for (int i = 1; i < 5; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapelessRecipe(Item.get(type, 0, 1))).addIngredient(Item.get(types[0][j], 0, cost[i - 1])));
            }
        }
    }

    protected void registerWeapons() {
        int[] cost = new int[]{2};
        int[][] types = {
                {Item.WOODEN_PLANK, Item.COBBLESTONE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.WOODEN_SWORD, Item.STONE_SWORD, Item.IRON_SWORD, Item.DIAMOND_SWORD, Item.GOLD_SWORD},
        };
        for (int i = 1; i < 2; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapelessRecipe(Item.get(type, 0, 1))).addIngredient(Item.get(types[0][j], null, cost[i - 1])).addIngredient(Item.get(Item.STICK, 0, 1)));
            }
        }

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.ARROW, 0, 1))).addIngredient(Item.get(Item.STICK, 0, 1)).addIngredient(Item.get(Item.FLINT, 0, 1)).addIngredient(Item.get(Item.FEATHER, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.BOW, 0, 1))).addIngredient(Item.get(Item.STRING, 0, 3)).addIngredient(Item.get(Item.STICK, 0, 3)));
    }

    protected void registerTools() {
        int[] cost = new int[]{3, 1, 3, 2};
        int[][] types = {
                {Item.WOODEN_PLANK, Item.COBBLESTONE, Item.IRON_INGOT, Item.DIAMOND, Item.GOLD_INGOT},
                {Item.WOODEN_PICKAXE, Item.STONE_PICKAXE, Item.IRON_PICKAXE, Item.DIAMOND_PICKAXE, Item.GOLD_PICKAXE},
                {Item.WOODEN_SHOVEL, Item.STONE_SHOVEL, Item.IRON_SHOVEL, Item.DIAMOND_SHOVEL, Item.GOLD_SHOVEL},
                {Item.WOODEN_AXE, Item.STONE_AXE, Item.IRON_AXE, Item.DIAMOND_AXE, Item.GOLD_AXE},
                {Item.WOODEN_HOE, Item.STONE_HOE, Item.IRON_HOE, Item.DIAMOND_HOE, Item.GOLD_HOE},
        };
        for (int i = 1; i < 5; ++i) {
            for (int j = 0; j < types[i].length; j++) {
                int type = types[i][j];
                this.registerRecipe((new BigShapelessRecipe(Item.get(type, 0, 1))).addIngredient(Item.get(types[0][j], null, cost[i - 1])).addIngredient(Item.get(Item.STICK, 0, 2)));
            }
        }

        this.registerRecipe((new ShapelessRecipe(Item.get(Item.FLINT_AND_STEEL, 0, 1))).addIngredient(Item.get(Item.IRON_INGOT, 0, 1)).addIngredient(Item.get(Item.FLINT, 0, 1)));
        this.registerRecipe((new ShapelessRecipe(Item.get(Item.SHEARS, 0, 1))).addIngredient(Item.get(Item.IRON_INGOT, 0, 2)));
    }

    protected void registerDyes() {
        for (int i = 0; i < 16; ++i) {
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            this.registerRecipe((new ShapelessRecipe(Item.get(Item.STAINED_CLAY, 15 - i, 8))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.HARDENED_CLAY, 0, 8)));
            //TODO: add glass things?
            //this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            //this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));
            //this.registerRecipe((new ShapelessRecipe(Item.get(Item.WOOL, 15 - i, 1))).addIngredient(Item.get(Item.DYE, i, 1)).addIngredient(Item.get(Item.WOOL, 0, 1)));

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

        //this.registerRecipe((new BigShapelessRecipe(Item.get(Item.GOLD_INGOT, 0, 1))).addIngredient(Item.get(Item.GOLD_NUGGET, 0, 9)));
        //this.registerRecipe((new ShapelessRecipe(Item.get(Item.GOLD_NUGGET, 0, 9))).addIngredient(Item.get(Item.GOLD_INGOT, 0, 1)));

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

    public Set<Recipe> getRecipes() {
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

    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        Item result = recipe.getResult();
        this.recipes.add(recipe);
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

    public Recipe matchTransaction(CraftingTransactionGroup ts) {
        Item result = ts.getResult();

        if (result == null) {
            return null;
        }

        String k = result.getId() + ":" + result.getDamage();

        if (!this.recipeLookup.containsKey(k)) {
            return null;
        }

        String hash = "";
        List<Item> input = ts.getRecipe();
        Collections.sort(input, this.comparator);
        int inputCount = 0;

        for (Item item : input) {
            inputCount += item.getCount();
            hash += item.getId() + ":" + (item.getDamage() == null ? "?" : item.getDamage()) + "x" + item.getCount() + ",";
        }

        Recipe recipe = null;

        if (!this.recipeLookup.get(k).containsKey(hash)) {
            Recipe hasRecipe = null;

            for (Recipe r : this.recipeLookup.get(k).values()) {
                if (r instanceof ShapelessRecipe) {
                    if (((ShapelessRecipe) r).getIngredientCount() != inputCount) {
                        continue;
                    }
                    List<Item> checkInput = ((ShapelessRecipe) r).getIngredientList();
                    for (Item item : input) {
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

            recipe = hasRecipe;
        } else {
            recipe = this.recipeLookup.get(k).get(hash);
        }

        if (recipe != null) {
            Item checkResult = recipe.getResult();
            if (checkResult.equals(result, true) && checkResult.getCount() == result.getCount()) {
                return recipe;
            }
        }

        return null;
    }

    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            this.registerShapedRecipe((ShapedRecipe) recipe);
        } else if (recipe instanceof ShapelessRecipe) {
            this.registerShapelessRecipe((ShapelessRecipe) recipe);
        } else if (recipe instanceof FurnaceRecipe) {
            this.registerFurnaceRecipe((FurnaceRecipe) recipe);
        }
    }
}

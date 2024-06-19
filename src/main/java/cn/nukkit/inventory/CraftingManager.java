package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.*;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class CraftingManager {

    public final Collection<Recipe> recipes = new ArrayDeque<>();

    public static DataPacket packet;
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = new Int2ObjectOpenHashMap<>();
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = new Int2ObjectOpenHashMap<>();

    public final Map<UUID, MultiRecipe> multiRecipes = new HashMap<>();
    public final Map<Integer, FurnaceRecipe> furnaceRecipes = new Int2ObjectOpenHashMap<>();
    public final Map<Integer, BrewingRecipe> brewingRecipes = new Int2ObjectOpenHashMap<>();
    public final Map<Integer, ContainerRecipe> containerRecipes = new Int2ObjectOpenHashMap<>();
    public final Map<Integer, CampfireRecipe> campfireRecipes = new Int2ObjectOpenHashMap<>(); // Server only
    public final Map<Integer, SmithingRecipe> smithingRecipes = new Int2ObjectOpenHashMap<>();

    private static int RECIPE_COUNT = 0;
    static int NEXT_NETWORK_ID;

    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else return Integer.compare(i1.getCount(), i2.getCount());
    };

    public CraftingManager() {
        InputStream recipesStream = Server.class.getClassLoader().getResourceAsStream("recipes.json");
        if (recipesStream == null) {
            throw new AssertionError("Unable to find recipes.json");
        }

        Config recipesConfig = new Config(Config.JSON);
        recipesConfig.load(recipesStream);
        this.loadRecipes(recipesConfig);

        String path = Server.getInstance().getDataPath() + "custom_recipes.json";
        File filePath = new File(path);

        if (filePath.exists()) {
            Config customRecipes = new Config(filePath, Config.JSON);
            this.loadRecipes(customRecipes);
        }

        this.buildMissingRecipes();

        this.rebuildPacket();

        MainLogger.getLogger().info("Successfully loaded " + this.recipes.size() + " recipes");
    }

    private void buildMissingRecipes() {
        List<Item> oakChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 0, 1));
        oakChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_oak_chest_boat", 0, Item.get(Item.OAK_CHEST_BOAT, 0, 1), oakChestBoat));
        List<Item> birchChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 2, 1));
        birchChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_birch_chest_boat", 0, Item.get(Item.BIRCH_CHEST_BOAT, 0, 1), birchChestBoat));
        List<Item> jungleChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 3, 1));
        jungleChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_jungle_chest_boat", 0, Item.get(Item.JUNGLE_CHEST_BOAT, 0, 1), jungleChestBoat));
        List<Item> spruceChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 1, 1));
        spruceChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_spruce_chest_boat", 0, Item.get(Item.SPRUCE_CHEST_BOAT, 0, 1), spruceChestBoat));
        List<Item> acaciaChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 4, 1));
        acaciaChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_acacia_chest_boat", 0, Item.get(Item.ACACIA_CHEST_BOAT, 0, 1), acaciaChestBoat));
        List<Item> darkOakChestBoat = Arrays.asList(Item.get(Item.CHEST, 0, 1), Item.get(Item.BOAT, 5, 1));
        darkOakChestBoat.sort(recipeComparator);
        this.registerRecipe(new ShapelessRecipe("craft_dark_oak_chest_boat", 0, Item.get(Item.DARK_OAK_CHEST_BOAT, 0, 1), darkOakChestBoat));
        Map<Character, Item> record5 = new CharObjectHashMap<>();
        record5.put('A', Item.get(Item.DISC_FRAGMENT_5, 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_record_5", 0, Item.get(Item.RECORD_5), new String[]{"AAA", "AAA", "AAA"}, record5, new ArrayList<>()));

        Map<Character, Item> recoveryCompass = new CharObjectHashMap<>();
        recoveryCompass.put('A', Item.get(Item.ECHO_SHARD, 0, 1));
        recoveryCompass.put('B', Item.get(Item.COMPASS, 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_recovery_compass", 0, Item.get(Item.RECOVERY_COMPASS), new String[]{"AAA", "ABA", "AAA"}, recoveryCompass, new ArrayList<>()));

        Map<Character, Item> copperBlock = new CharObjectHashMap<>();
        copperBlock.put('A', Item.get(Item.COPPER_INGOT, 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_copper_block", 0, new ItemBlock(Block.get(BlockID.COPPER_BLOCK)), new String[]{"AAA", "AAA", "AAA"}, copperBlock, new ArrayList<>()));

        Map<Character, Item> copperSlab1 = new CharObjectHashMap<>();
        copperSlab1.put('A', new ItemBlock(Block.get(BlockID.CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab1", 0, new ItemBlock(Block.get(BlockID.CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab1, new ArrayList<>()));

        Map<Character, Item> copperSlab2 = new CharObjectHashMap<>();
        copperSlab2.put('A', new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab2", 0, new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab2, new ArrayList<>()));

        Map<Character, Item> copperSlab3 = new CharObjectHashMap<>();
        copperSlab3.put('A', new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab3", 0, new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab3, new ArrayList<>()));

        Map<Character, Item> copperSlab4 = new CharObjectHashMap<>();
        copperSlab4.put('A', new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab4", 0, new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab4, new ArrayList<>()));

        Map<Character, Item> copperSlab1w = new CharObjectHashMap<>();
        copperSlab1w.put('A', new ItemBlock(Block.get(BlockID.WAXED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab1w", 0, new ItemBlock(Block.get(BlockID.WAXED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab1w, new ArrayList<>()));

        Map<Character, Item> copperSlab2w = new CharObjectHashMap<>();
        copperSlab2w.put('A', new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab2w", 0, new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab2w, new ArrayList<>()));

        Map<Character, Item> copperSlab3w = new CharObjectHashMap<>();
        copperSlab3w.put('A', new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab3w", 0, new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab3w, new ArrayList<>()));

        Map<Character, Item> copperSlab4w = new CharObjectHashMap<>();
        copperSlab4w.put('A', new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_slab4w", 0, new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_CUT_COPPER_SLAB), 0, 6), new String[]{"   ", "   ", "AAA"}, copperSlab4w, new ArrayList<>()));

        Map<Character, Item> copperStairs1 = new CharObjectHashMap<>();
        copperStairs1.put('A', new ItemBlock(Block.get(BlockID.CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs1", 0, new ItemBlock(Block.get(BlockID.CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs1, new ArrayList<>()));

        Map<Character, Item> copperStairs2 = new CharObjectHashMap<>();
        copperStairs2.put('A', new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs2", 0, new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs2, new ArrayList<>()));

        Map<Character, Item> copperStairs3 = new CharObjectHashMap<>();
        copperStairs3.put('A', new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs3", 0, new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs3, new ArrayList<>()));

        Map<Character, Item> copperStairs4 = new CharObjectHashMap<>();
        copperStairs4.put('A', new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs4", 0, new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs4, new ArrayList<>()));

        Map<Character, Item> copperStairs1w = new CharObjectHashMap<>();
        copperStairs1w.put('A', new ItemBlock(Block.get(BlockID.WAXED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs1w", 0, new ItemBlock(Block.get(BlockID.WAXED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs1w, new ArrayList<>()));

        Map<Character, Item> copperStairs2w = new CharObjectHashMap<>();
        copperStairs2w.put('A', new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs2w", 0, new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs2w, new ArrayList<>()));

        Map<Character, Item> copperStairs3w = new CharObjectHashMap<>();
        copperStairs3w.put('A', new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs3w", 0, new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs3w, new ArrayList<>()));

        Map<Character, Item> copperStairs4w = new CharObjectHashMap<>();
        copperStairs4w.put('A', new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_CUT_COPPER)));
        this.registerRecipe(new ShapedRecipe("craft_copper_stairs4w", 0, new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_CUT_COPPER_STAIRS), 0, 4), new String[]{"A  ", "AA ", "AAA"}, copperStairs4w, new ArrayList<>()));

        Map<Character, Item> lightningRod = new CharObjectHashMap<>();
        lightningRod.put('A', Item.get(Item.COPPER_INGOT, 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_lightning_rod", 0, new ItemBlock(Block.get(BlockID.LIGHTNING_ROD)), new String[]{" A ", " A ", " A "}, lightningRod, new ArrayList<>()));

        Map<Character, Item> spyglass = new CharObjectHashMap<>();
        spyglass.put('A', Item.get(Item.COPPER_INGOT, 0, 1));
        spyglass.put('B', Item.get(Item.AMETHYST_SHARD, 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_spyglass", 0, Item.get(Item.SPYGLASS), new String[]{" B ", " A ", " A "}, spyglass, new ArrayList<>()));

        Map<Character, Item> tintedGlass = new CharObjectHashMap<>();
        tintedGlass.put('A', Item.get(Item.AMETHYST_SHARD, 0, 1));
        tintedGlass.put('B', new ItemBlock(Block.get(BlockID.GLASS), 0, 1));
        this.registerRecipe(new ShapedRecipe("craft_tinted_glass", 0, new ItemBlock(Block.get(BlockID.TINTED_GLASS), 0, 2), new String[]{" A ", "ABA", " A "}, tintedGlass, new ArrayList<>()));

        this.registerRecipe(new ShapelessRecipe("craft_copper_ingot", 0, Item.get(ItemID.COPPER_INGOT, 0, 9), Collections.singleton(new ItemBlock(Block.get(BlockID.COPPER_BLOCK)))));
        this.registerRecipe(new ShapelessRecipe("craft_amethyst_block", 0, Item.get(ItemID.GLOW_ITEM_FRAME), Arrays.asList(Item.get(ItemID.ITEM_FRAME), Item.get(ItemID.DYE, ItemDye.GLOW_INK_SAC))));
        this.registerRecipe(new ShapelessRecipe("craft_glow_frame", 0, new ItemBlock(Block.get(BlockID.AMETHYST_BLOCK)), Arrays.asList(Item.get(ItemID.AMETHYST_SHARD), Item.get(ItemID.AMETHYST_SHARD), Item.get(ItemID.AMETHYST_SHARD), Item.get(ItemID.AMETHYST_SHARD))));

        this.registerRecipe(new ShapelessRecipe("craft_moss_stone", 0, new ItemBlock(Block.get(BlockID.MOSS_STONE)), Arrays.asList(new ItemBlock(Block.get(BlockID.MOSS_BLOCK)), new ItemBlock(Block.get(BlockID.COBBLESTONE)))));
        this.registerRecipe(new ShapelessRecipe("craft_moss_stone_brick", 0, new ItemBlock(Block.get(BlockID.STONE_BRICK, 1)), Arrays.asList(new ItemBlock(Block.get(BlockID.MOSS_BLOCK)), new ItemBlock(Block.get(BlockID.STONE_BRICK)))));
        this.registerRecipe(new ShapelessRecipe("craft_moss_carpet", 0, new ItemBlock(Block.get(BlockID.MOSS_CARPET), 0, 3), Arrays.asList(new ItemBlock(Block.get(BlockID.MOSS_BLOCK)), new ItemBlock(Block.get(BlockID.MOSS_BLOCK)))));

        this.registerRecipe(new ShapelessRecipe("craft_wax_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.COPPER_BLOCK)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_cut_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.CUT_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_o_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.OXIDIZED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_oc_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_OXIDIZED_CUT_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_w_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.WEATHERED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_wc_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_WEATHERED_CUT_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_e_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.EXPOSED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_wax_ec_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_EXPOSED_CUT_COPPER)), Arrays.asList(Item.get(ItemID.HONEYCOMB), new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER)))));

        this.registerRecipe(new ShapelessRecipe("craft_cut_copper", 0, new ItemBlock(Block.get(BlockID.CUT_COPPER), 0, 4),
                Arrays.asList(new ItemBlock(Block.get(BlockID.COPPER_BLOCK)), new ItemBlock(Block.get(BlockID.COPPER_BLOCK)), new ItemBlock(Block.get(BlockID.COPPER_BLOCK)), new ItemBlock(Block.get(BlockID.COPPER_BLOCK)))));
        this.registerRecipe(new ShapelessRecipe("craft_waxed_cut_copper", 0, new ItemBlock(Block.get(BlockID.WAXED_CUT_COPPER), 0, 4),
                Arrays.asList(new ItemBlock(Block.get(BlockID.WAXED_COPPER)), new ItemBlock(Block.get(BlockID.WAXED_COPPER)), new ItemBlock(Block.get(BlockID.WAXED_COPPER)), new ItemBlock(Block.get(BlockID.WAXED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_oxidized_cut_copper", 0, new ItemBlock(Block.get(BlockID.OXIDIZED_CUT_COPPER), 0, 4),
                Arrays.asList(new ItemBlock(Block.get(BlockID.OXIDIZED_COPPER)), new ItemBlock(Block.get(BlockID.OXIDIZED_COPPER)), new ItemBlock(Block.get(BlockID.OXIDIZED_COPPER)), new ItemBlock(Block.get(BlockID.OXIDIZED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_weathered_cut_copper", 0, new ItemBlock(Block.get(BlockID.WEATHERED_CUT_COPPER), 0, 4),
                Arrays.asList(new ItemBlock(Block.get(BlockID.WEATHERED_COPPER)), new ItemBlock(Block.get(BlockID.WEATHERED_COPPER)), new ItemBlock(Block.get(BlockID.WEATHERED_COPPER)), new ItemBlock(Block.get(BlockID.WEATHERED_COPPER)))));
        this.registerRecipe(new ShapelessRecipe("craft_exposed_cut_copper", 0, new ItemBlock(Block.get(BlockID.EXPOSED_CUT_COPPER), 0, 4),
                Arrays.asList(new ItemBlock(Block.get(BlockID.EXPOSED_COPPER)), new ItemBlock(Block.get(BlockID.EXPOSED_COPPER)), new ItemBlock(Block.get(BlockID.EXPOSED_COPPER)), new ItemBlock(Block.get(BlockID.EXPOSED_COPPER)))));


        this.registerRecipe(new FurnaceRecipe(new ItemBlock(Block.get(BlockID.SMOOTH_BASALT)), new ItemBlock(Block.get(BlockID.BASALT))));
        this.registerRecipe(new FurnaceRecipe(new ItemBlock(Block.get(BlockID.DEEPSLATE)), new ItemBlock(Block.get(BlockID.COBBLED_DEEPSLATE))));
        this.registerRecipe(new FurnaceRecipe(new ItemBlock(Block.get(BlockID.CRACKED_DEEPSLATE_BRICKS)), new ItemBlock(Block.get(BlockID.DEEPSLATE_BRICKS))));
        this.registerRecipe(new FurnaceRecipe(new ItemBlock(Block.get(BlockID.CRACKED_DEEPSLATE_TILES)), new ItemBlock(Block.get(BlockID.DEEPSLATE_TILES))));
        this.registerRecipe(new FurnaceRecipe(new ItemBlock(Block.get(BlockID.CRACKED_POLISHED_BLACKSTONE_BRICKS)), new ItemBlock(Block.get(BlockID.POLISHED_BLACKSTONE_BRICKS))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.IRON_INGOT), Item.get(ItemID.RAW_IRON)));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.GOLD_INGOT), Item.get(ItemID.RAW_GOLD)));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.COPPER_INGOT), Item.get(ItemID.RAW_COPPER)));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.IRON_INGOT), new ItemBlock(Block.get(BlockID.DEEPSLATE_IRON_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.GOLD_INGOT), new ItemBlock(Block.get(BlockID.DEEPSLATE_GOLD_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.COPPER_INGOT), new ItemBlock(Block.get(BlockID.DEEPSLATE_COPPER_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.COAL), new ItemBlock(Block.get(BlockID.DEEPSLATE_COAL_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.EMERALD), new ItemBlock(Block.get(BlockID.DEEPSLATE_EMERALD_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.DIAMOND), new ItemBlock(Block.get(BlockID.DEEPSLATE_DIAMOND_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.REDSTONE), new ItemBlock(Block.get(BlockID.DEEPSLATE_REDSTONE_ORE))));
        this.registerRecipe(new FurnaceRecipe(Item.get(ItemID.DYE, ItemDye.LAPIS_LAZULI), new ItemBlock(Block.get(BlockID.DEEPSLATE_LAPIS_ORE))));

        this.registerSmithingRecipes();
    }

    private void registerSmithingRecipes() {
        ConfigSection smithing = new Config(Config.YAML).loadFromStream(Server.class.getClassLoader().getResourceAsStream("smithing.json")).getRootSection();
        for (Map<String, Object> recipe : (List<Map<String, Object>>) smithing.get((Object) "smithing")) {
            List<Map> outputs = ((List<Map>) recipe.get("output"));
            if (outputs.size() > 1) {
                continue;
            }

            String recipeId = (String) recipe.get("id");

            Map<String, Object> first = outputs.get(0);
            Item item = Item.get(RuntimeItems.getMapping().fromIdentifier((String) first.get("id")).getLegacyId(), 0, 1);

            List<Item> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                Item ing = Item.get(RuntimeItems.getMapping().fromIdentifier((String) ingredient.get("id")).getLegacyId(), 0, 1);
                ingredients.add(ing);
            }

            this.registerRecipe(new SmithingRecipe(recipeId, 0, ingredients, item));
            this.registerRecipe(new SmithingRecipe(recipeId, 0, ingredients, item));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRecipes(Config config) {
        List<Map> recipes = config.getMapList("recipes");
        MainLogger.getLogger().info("Loading recipes...");
        for (Map<String, Object> recipe : recipes) {
            top:
            try {
                switch (Utils.toInt(recipe.get("type"))) {
                    case 0:
                        String craftingBlock = (String) recipe.get("block");
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue;
                        }
                        // TODO: handle multiple result items
                        List<Map> outputs = ((List<Map>) recipe.get("output"));
                        if (outputs.size() > 1) {
                            continue;
                        }
                        Map<String, Object> first = outputs.get(0);
                        List<Item> sorted = new ArrayList<>();
                        for (Map<String, Object> ingredient : ((List<Map>) recipe.get("input"))) {
                            sorted.add(Item.fromJson(ingredient));
                        }
                        // Bake sorted list
                        sorted.sort(recipeComparator);

                        Item resultItem = Item.fromJson(first);
                        this.registerRecipe(new ShapelessRecipe(null, Utils.toInt(recipe.get("priority")), resultItem, sorted)); // null recipeId will be replaced with recipe uuid
                        break;
                    case 1:
                        craftingBlock = (String) recipe.get("block");
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue;
                        }
                        outputs = (List<Map>) recipe.get("output");

                        first = outputs.remove(0);
                        String[] shape = ((List<String>) recipe.get("shape")).toArray(new String[0]);
                        Map<Character, Item> ingredients = new CharObjectHashMap<>();
                        List<Item> extraResults = new ArrayList<>();

                        Map<String, Map<String, Object>> input = (Map) recipe.get("input");
                        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                            char ingredientChar = ingredientEntry.getKey().charAt(0);
                            Item ingredient = Item.fromJson(ingredientEntry.getValue());

                            // TODO: update recipes
                            if (ingredient.getId() == Item.PLANKS && Utils.toInt(ingredientEntry.getValue().getOrDefault("damage", 0)) == -1) {
                                createLegacyPlanksRecipe(recipe, first);
                                break top;
                            }

                            ingredients.put(ingredientChar, ingredient);
                        }

                        for (Map<String, Object> data : outputs) {
                            extraResults.add(Item.fromJson(data));
                        }

                        resultItem = Item.fromJson(first);
                        this.registerRecipe(new ShapedRecipe(null, Utils.toInt(recipe.get("priority")), resultItem, shape, ingredients, extraResults));
                        break;
                    case 2:
                    case 3:
                        craftingBlock = (String) recipe.get("block");
                        if (!"furnace".equals(craftingBlock)) {
                            // Ignore other recipes than furnaces
                            continue;
                        }
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        resultItem = Item.fromJson(resultMap);
                        Item inputItem;
                        try {
                            Map<String, Object> inputMap = (Map) recipe.get("input");
                            inputItem = Item.fromJson(inputMap);
                        } catch (Exception old) {
                            inputItem = Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : -1, 1);
                        }
                        this.registerRecipe(new FurnaceRecipe(resultItem, inputItem));
                        break;
                    case 4:
                        this.registerRecipe(new MultiRecipe(UUID.fromString((String) recipe.get("uuid"))));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                MainLogger.getLogger().error("Exception during registering recipe", e);
            }
        }

        // Load brewing recipes
        List<Map> potionMixes = config.getMapList("potionMixes");

        for (Map potionMix : potionMixes) {
            int fromPotionId = ((Number) potionMix.get("inputId")).intValue(); // gson returns doubles...
            int fromPotionMeta = ((Number) potionMix.get("inputMeta")).intValue();
            int ingredient = ((Number) potionMix.get("reagentId")).intValue();
            int ingredientMeta = ((Number) potionMix.get("reagentMeta")).intValue();
            int toPotionId = ((Number) potionMix.get("outputId")).intValue();
            int toPotionMeta = ((Number) potionMix.get("outputMeta")).intValue();

            registerBrewingRecipe(new BrewingRecipe(Item.get(fromPotionId, fromPotionMeta), Item.get(ingredient, ingredientMeta), Item.get(toPotionId, toPotionMeta)));
        }

        List<Map> containerMixes = config.getMapList("containerMixes");

        for (Map containerMix : containerMixes) {
            int fromItemId = ((Number) containerMix.get("inputId")).intValue();
            int ingredient = ((Number) containerMix.get("reagentId")).intValue();
            int toItemId = ((Number) containerMix.get("outputId")).intValue();

            registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
        }
    }

    private void createLegacyPlanksRecipe(Map<String, Object> recipe, Map<String, Object> first) {
        List<Map> outputs = (List<Map>) recipe.get("output");
        String[] shape = ((List<String>) recipe.get("shape")).toArray(new String[0]);
        List<Item> extraResults = new ArrayList<>();
        for (Map<String, Object> data : outputs) {
            extraResults.add(Item.fromJson(data));
        }
        for (int planksMeta = 0; planksMeta <= 5; planksMeta++) {
            Map<Character, Item> ingredients = new CharObjectHashMap<>();
            Map<String, Map<String, Object>> input = (Map) recipe.get("input");
            for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
                char ingredientChar = ingredientEntry.getKey().charAt(0);
                ingredientEntry.getValue().put("damage", 0);
                Item ingredient = Item.fromJson(ingredientEntry.getValue());
                if (ingredient.getId() == Item.PLANKS) {
                    ingredient.setDamage(planksMeta);
                }
                ingredients.put(ingredientChar, ingredient);
            }
            this.registerRecipe(
                    new ShapedRecipe(null, Utils.toInt(recipe.get("priority")), Item.fromJson(first), shape, ingredients, extraResults));
        }
    }

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;
        for (Recipe recipe : this.getRecipes()) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
            }
        }

        for (FurnaceRecipe recipe : this.getFurnaceRecipes().values()) {
            pk.addFurnaceRecipe(recipe);
        }

        for (MultiRecipe recipe : this.multiRecipes.values()) {
            pk.addMultiRecipe(recipe);
        }

        for (BrewingRecipe recipe : brewingRecipes.values()) {
            pk.addBrewingRecipe(recipe);
        }

        for (ContainerRecipe recipe : containerRecipes.values()) {
            pk.addContainerRecipe(recipe);
        }

        pk.tryEncode();
        packet = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public Map<Integer, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        FurnaceRecipe recipe = this.furnaceRecipes.get(getItemHash(input));
        if (recipe == null) recipe = this.furnaceRecipes.get(getItemHash(input.getId(), 0));
        return recipe;
    }

    private static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream(new byte[5 * items.size()]).reset();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    private static int getFullItemHash(Item item) {
        return (getItemHash(item) << 6) | (item.getCount() & 0x3f);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        this.furnaceRecipes.put(getItemHash(input), recipe);
    }

    private static int getItemHash(Item item) {
        return getItemHash(item.getId(), item.getDamage());
    }

    private static int getItemHash(int id, int meta) {
        return (id << 12) | (meta & 0xfff);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = shapedRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());
        List<Item> inputList = new LinkedList<>(recipe.getIngredientsAggregate());
        map.put(getMultiItemHash(inputList), recipe);
    }


    public void registerRecipe(Recipe recipe) {
        if (recipe instanceof CraftingRecipe) {
            UUID id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));

            ((CraftingRecipe) recipe).setId(id);
            this.recipes.add(recipe);
        }

        recipe.registerToCraftingManager(this);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        List<Item> list = recipe.getIngredientsAggregate();

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map = shapelessRecipes.computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    private static int getPotionHash(int ingredientId, int potionType) {
        return (ingredientId << 15) | potionType;
    }

    private static int getContainerHash(int ingredientId, int containerId) {
        return (ingredientId << 15) | containerId;
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.brewingRecipes.put(getPotionHash(input.getId(), potion.getDamage()), recipe);
    }

    public void registerContainerRecipe(ContainerRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        this.containerRecipes.put(getContainerHash(input.getId(), potion.getId()), recipe);
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        int id = potion.getId();
        if (id == Item.POTION || id == Item.SPLASH_POTION || id == Item.LINGERING_POTION) {
            return this.brewingRecipes.get(getPotionHash(input.getId(), potion.getDamage()));
        }

        return null;
    }

    public ContainerRecipe matchContainerRecipe(Item input, Item potion) {
        return this.containerRecipes.get(getContainerHash(input.getId(), potion.getId()));
    }

    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

        int outputHash = getItemHash(primaryOutput);
        if (this.shapedRecipes.containsKey(outputHash)) {
            inputList.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(inputList);

            Map<UUID, ShapedRecipe> recipeMap = shapedRecipes.get(outputHash);

            if (recipeMap != null) {
                ShapedRecipe recipe = recipeMap.get(inputHash);

                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                    return recipe;
                }

                for (ShapedRecipe shapedRecipe : recipeMap.values()) {
                    if (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList)) {
                        return shapedRecipe;
                    }
                }
            }
        }

        if (shapelessRecipes.containsKey(outputHash)) {
            inputList.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(inputList);

            Map<UUID, ShapelessRecipe> recipes = shapelessRecipes.get(outputHash);

            if (recipes == null) {
                return null;
            }

            ShapelessRecipe recipe = recipes.get(inputHash);

            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }

            for (ShapelessRecipe shapelessRecipe : recipes.values()) {
                if (shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapelessRecipe;
                }
            }
        }

        return null;
    }

    public CampfireRecipe matchCampfireRecipe(Item input) {
        CampfireRecipe recipe = this.campfireRecipes.get(getItemHash(input));
        if (recipe == null) recipe = this.campfireRecipes.get(getItemHash(input.getId(), 0));
        return recipe;
    }

    private boolean matchItemsAccumulation(CraftingRecipe recipe, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        Item recipeResult = recipe.getResult();
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() == 0) {
            int multiplier = primaryOutput.getCount() / recipeResult.getCount();
            return recipe.matchItems(inputList, extraOutputList, multiplier);
        }
        return false;
    }

    public SmithingRecipe matchSmithingRecipe(Item equipment, Item ingredient) {
        return this.smithingRecipes.get(getContainerHash(ingredient.getId(), equipment.getId()));
    }

    public void registerMultiRecipe(MultiRecipe recipe) {
        this.multiRecipes.put(recipe.getId(), recipe);
    }

    public void registerCampfireRecipe(CampfireRecipe recipe) {
        this.campfireRecipes.put(getItemHash(recipe.getInput()), recipe);
    }

    public void registerSmithingRecipe(SmithingRecipe recipe) {
        Item input = recipe.getIngredient();
        Item potion = recipe.getEquipment();
        this.smithingRecipes.put(getContainerHash(input.getId(), potion.getId()), recipe);
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

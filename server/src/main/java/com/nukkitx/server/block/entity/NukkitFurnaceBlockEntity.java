package com.nukkitx.server.block.entity;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.metadata.blockentity.FurnaceBlockEntity;
import com.nukkitx.api.metadata.item.Bucket;
import com.nukkitx.server.inventory.NukkitFurnaceInventory;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 */
@Getter
@Setter
public class NukkitFurnaceBlockEntity extends NukkitNameableBlockEntity implements FurnaceBlockEntity {

    private static final TIntIntHashMap FUEL_MAP = new TIntIntHashMap();
    private static final TIntObjectHashMap<ItemType> RECIPE_MAP = new TIntObjectHashMap<>();

    static {
        FUEL_MAP.put(BlockTypes.COAL_BLOCK.getId(), 16000);
        FUEL_MAP.put(BlockTypes.DRIED_KELP_BLOCK.getId(), 4000);
        FUEL_MAP.put(ItemTypes.BLAZE_ROD.getId(), 2400);
        FUEL_MAP.put(ItemTypes.COAL.getId(), 1600);
        FUEL_MAP.put(ItemTypes.BOAT.getId(), 1200);
        FUEL_MAP.put(ItemTypes.FISHING_ROD.getId(), 300);
        FUEL_MAP.put(ItemTypes.WOODEN_PICKAXE.getId(), 200);
        FUEL_MAP.put(ItemTypes.WOODEN_SHOVEL.getId(), 200);
        FUEL_MAP.put(ItemTypes.WOODEN_HOE.getId(), 200);
        FUEL_MAP.put(ItemTypes.WOODEN_AXE.getId(), 200);
        FUEL_MAP.put(ItemTypes.WOODEN_SWORD.getId(), 200);
        FUEL_MAP.put(ItemTypes.SIGN.getId(), 200);
        FUEL_MAP.put(ItemTypes.WOODEN_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.DARK_OAK_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.ACACIA_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.BIRCH_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.JUNGLE_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.SPRUCE_DOOR.getId(), 200);
        FUEL_MAP.put(ItemTypes.BOWL.getId(), 200);
        FUEL_MAP.put(ItemTypes.STICK.getId(), 100);
        FUEL_MAP.put(ItemTypes.BOW.getId(), 200);
        FUEL_MAP.put(ItemTypes.BANNER.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOOD.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOOD2.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOOD_PLANKS.getId(), 300);
        FUEL_MAP.put(BlockTypes.ACACIA_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.BIRCH_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.DARK_OAK_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.JUNGLE_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.SPRUCE_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.STONE_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.WEIGHTED_PRESSURE_PLATE_HEAVY.getId(), 300);
        FUEL_MAP.put(BlockTypes.WEIGHTED_PRESSURE_PLATE_LIGHT.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOODEN_PRESSURE_PLATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.FENCE.getId(), 300);
        FUEL_MAP.put(BlockTypes.FENCE.getId(), 300);
        FUEL_MAP.put(BlockTypes.FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.ACACIA_FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.BIRCH_FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.DARK_OAK_FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.JUNGLE_FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.SPRUCE_FENCE_GATE.getId(), 300);
        FUEL_MAP.put(BlockTypes.OAK_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.ACACIA_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.SPRUCE_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.BIRCH_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.DARK_OAK_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.JUNGLE_WOOD_STAIRS.getId(), 300);
        FUEL_MAP.put(BlockTypes.TRAPDOOR.getId(), 300);
        FUEL_MAP.put(BlockTypes.CRAFTING_TABLE.getId(), 300);
        FUEL_MAP.put(BlockTypes.BOOKSHELF.getId(), 300);
        FUEL_MAP.put(BlockTypes.CHEST.getId(), 300);
        FUEL_MAP.put(BlockTypes.TRAPPED_CHEST.getId(), 300);
        FUEL_MAP.put(BlockTypes.DAYLIGHT_SENSOR.getId(), 300);
        FUEL_MAP.put(BlockTypes.INVERTED_DAYLIGHT_SENSOR.getId(), 300);
        FUEL_MAP.put(BlockTypes.JUKEBOX.getId(), 300);
        FUEL_MAP.put(BlockTypes.NOTE_BLOCK.getId(), 300);
        FUEL_MAP.put(BlockTypes.BROWN_MUSHROOM_BLOCK.getId(), 300);
        FUEL_MAP.put(BlockTypes.RED_MUSHROOM_BLOCK.getId(), 300);
        FUEL_MAP.put(BlockTypes.STANDING_BANNER.getId(), 300);
        FUEL_MAP.put(BlockTypes.WALL_BANNER.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOODEN_DOUBLE_SLAB.getId(), 300);
        FUEL_MAP.put(BlockTypes.WOODEN_SLAB.getId(), 300);
        FUEL_MAP.put(BlockTypes.LADDER.getId(), 300);
        FUEL_MAP.put(BlockTypes.SAPLING.getId(), 100);
        FUEL_MAP.put(BlockTypes.WOODEN_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.BIRCH_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.ACACIA_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.DARK_OAK_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.JUNGLE_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.SPRUCE_BUTTON.getId(), 100);
        FUEL_MAP.put(BlockTypes.WOOL.getId(), 100);
        FUEL_MAP.put(BlockTypes.CARPET.getId(), 67);

//        RECIPE_MAP.put(ItemTypes.RAW_PORKCHOP.getId(), ItemTypes.COOKED_PORKCHOP);
//        RECIPE_MAP.put(ItemTypes.RAW_BEEF.getId(), ItemTypes.STEAK);
//        RECIPE_MAP.put(ItemTypes.RAW_CHICKEN.getId(), ItemTypes.COOKED_CHICKEN);
//        RECIPE_MAP.put(ItemTypes.RAW_FISH.getId(), ItemTypes.COOKED_FISH);
//        RECIPE_MAP.put(ItemTypes.RAW_SALMON.getId(), ItemTypes.COOKED_SALMON);
//        RECIPE_MAP.put(ItemTypes.POTATO.getId(), ItemTypes.BAKED_POTATO);
//        RECIPE_MAP.put(ItemTypes.MUTTON.getId(), ItemTypes.COOKED_MUTTON);
//        RECIPE_MAP.put(ItemTypes.RAW_RABBIT.getId(), ItemTypes.COOKED_RABBIT);
//        RECIPE_MAP.put(ItemTypes.KELP.getId(), ItemTypes.DRIED_KELP);
//
//        RECIPE_MAP.put(BlockTypes.IRON_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.GOLD_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.SAND.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.COBBLESTONE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.NETHERRACK.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.CLAY.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.STONE_BRICK.getId(), ItemTypes.);
//
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 0), BlockTypes.WHITE_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 1), BlockTypes.ORANGE_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 2), BlockTypes.MAGENTA_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 3), BlockTypes.LIGHT_BLUE_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 4), BlockTypes.YELLOW_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 5), BlockTypes.LIME_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 6), BlockTypes.PINK_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 7), BlockTypes.GRAY_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 8), BlockTypes.LIGHT_GRAY_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 9), BlockTypes.CYAN_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 10), BlockTypes.PURPLE_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 11), BlockTypes.BLUE_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 12), BlockTypes.BROWN_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 13), BlockTypes.GREEN_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 14), BlockTypes.RED_GLAZED_TERRACOTTA);
//        RECIPE_MAP.put(ItemUtil.hash(BlockTypes.COLORED_TERRACOTTA.getId(), 15), BlockTypes.BLACK_GLAZED_TERRACOTTA);
//
//        RECIPE_MAP.put(BlockTypes.DIAMOND_ORE.getId(), ItemTypes.DIAMOND);
//        RECIPE_MAP.put(BlockTypes.LAPIS_LAZULI_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.REDSTONE_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.COAL_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.EMERALD_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes.NETHER_QUARTZ_ORE.getId(), ItemTypes.);
//        RECIPE_MAP.put(BlockTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
//        RECIPE_MAP.put(ItemTypes..getId(), ItemTypes.);
    }

    private final NukkitFurnaceInventory inventory;

    public int cookTime;
    public int burnTime;
    public int maxBurnTime;

    private int recipeUsedSize;

    private boolean keepPacked; //idk what is it?

    public NukkitFurnaceBlockEntity(NukkitFurnaceInventory inventory) {
        super(BlockEntityType.FURNACE);

        this.inventory = inventory;
    }

    @Override
    public boolean isFuel(@Nullable ItemInstance item) {
        return getBurnDuration(item) > 0;
    }

    @Override
    public int getBurnDuration(@Nullable ItemInstance item) {
        if (item == null) {
            return 0;
        }

        if (item.getItemType() == ItemTypes.BUCKET) {
            Bucket data = item.ensureItemData(Bucket.class);

            if (data == Bucket.LAVA) {
                return 20000;
            }

            return 0;
        }

        return FUEL_MAP.get(item.getItemType().getId());
    }

    @Override
    public boolean isIngredient(@Nullable ItemInstance item) {
        return false;
    }

    public ItemInstance getResult(ItemInstance ingredient) {
        return null; //TODO
    }
}

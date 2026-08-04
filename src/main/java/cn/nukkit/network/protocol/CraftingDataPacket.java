package cn.nukkit.network.protocol;

import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_DATA_PACKET;

    public static final String CRAFTING_TAG_CRAFTING_TABLE = "crafting_table";
    public static final String CRAFTING_TAG_CARTOGRAPHY_TABLE = "cartography_table";
    public static final String CRAFTING_TAG_STONECUTTER = "stonecutter";
    public static final String CRAFTING_TAG_FURNACE = "furnace";
    public static final String CRAFTING_TAG_CAMPFIRE = "campfire";
    public static final String CRAFTING_TAG_BLAST_FURNACE = "blast_furnace";
    public static final String CRAFTING_TAG_SMOKER = "smoker";
    public static final String CRAFTING_TAG_SMITHING_TABLE = "smithing_table";

    private final List<ShapedRecipe> shapedData = new ArrayList<>();
    private final List<Recipe> shapelessData = new ArrayList<>();
    private final List<MultiRecipe> multiData = new ArrayList<>();
    private final List<SmithingRecipe> smithingTransformData = new ArrayList<>();
    private final List<BrewingRecipe> brewingEntries = new ArrayList<>();
    private final List<ContainerRecipe> containerEntries = new ArrayList<>();
    public boolean cleanRecipes = true;

    public void addShapelessRecipe(ShapelessRecipe... recipe) {
        Collections.addAll(shapelessData, recipe);
    }

    public void addSmithingRecipe(SmithingRecipe... recipe) {
        Collections.addAll(smithingTransformData, recipe);
    }

    public void addShapedRecipe(ShapedRecipe... recipe) {
        Collections.addAll(shapedData, recipe);
    }

    public void addFurnaceRecipe(FurnaceRecipe... recipe) {
        Collections.addAll(shapelessData, recipe);
    }

    public void addBrewingRecipe(BrewingRecipe... recipe) {
        Collections.addAll(brewingEntries, recipe);
    }

    public void addMultiRecipe(MultiRecipe... recipe) {
        Collections.addAll(multiData, recipe);
    }

    public void addContainerRecipe(ContainerRecipe... recipe) {
        Collections.addAll(containerEntries, recipe);
    }

    @Override
    public DataPacket clean() {
        shapedData.clear();
        shapelessData.clear();
        multiData.clear();
        smithingTransformData.clear();
        brewingEntries.clear();
        containerEntries.clear();
        return super.clean();
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();

        this.putUnsignedVarInt(shapedData.size());
        for (ShapedRecipe shaped : shapedData) {
            this.putString(shaped.getRecipeId());
            this.putVarInt(shaped.getWidth());
            this.putVarInt(shaped.getHeight());
            this.putUnsignedVarInt((long) shaped.getWidth() * shaped.getHeight());
            for (int z = 0; z < shaped.getHeight(); ++z) {
                for (int x = 0; x < shaped.getWidth(); ++x) {
                    this.putRecipeIngredient(shaped.getIngredient(x, z));
                }
            }
            List<Item> outputs = new ArrayList<>();
            outputs.add(shaped.getResult());
            outputs.addAll(shaped.getExtraResults());
            this.putUnsignedVarInt(outputs.size());
            for (Item output : outputs) {
                this.putSlot(output, true);
            }
            this.putUUID(shaped.getId());
            this.putString(CRAFTING_TAG_CRAFTING_TABLE);
            this.putVarInt(shaped.getPriority());
            this.putBoolean(true); // Assume symmetry
            this.putBoolean(false); // No unlock requirement
            this.putUnsignedVarInt(shaped.getNetworkId());
        }

        this.putUnsignedVarInt(shapelessData.size());
        for (Recipe recipe : shapelessData) {
            switch (recipe.getType()) {
                case SHAPELESS:
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    this.putString(shapeless.getRecipeId());
                    List<Item> ingredients = shapeless.getIngredientList();
                    this.putUnsignedVarInt(ingredients.size());
                    for (Item ingredient : ingredients) {
                        this.putRecipeIngredient(ingredient);
                    }
                    this.putUnsignedVarInt(1); // Results length
                    this.putSlot(shapeless.getResult(), true);
                    this.putUUID(shapeless.getId());
                    this.putString(CRAFTING_TAG_CRAFTING_TABLE);
                    this.putVarInt(shapeless.getPriority());
                    this.putBoolean(false); // No unlock requirement
                    this.putUnsignedVarInt(shapeless.getNetworkId());
                    break;
                case FURNACE:
                case FURNACE_DATA:
                    FurnaceRecipe furnace = (FurnaceRecipe) recipe;
                    this.putString(furnace.getId().toString());
                    this.putUnsignedVarInt(1); // Ingredients length
                    this.putRecipeIngredient(furnace.getInput());
                    this.putUnsignedVarInt(1); // Results length
                    this.putSlot(furnace.getResult(), true);
                    this.putUUID(furnace.getId());
                    this.putString(recipe instanceof SmokerRecipe ? CRAFTING_TAG_SMOKER : recipe instanceof BlastFurnaceRecipe ? CRAFTING_TAG_BLAST_FURNACE : CRAFTING_TAG_FURNACE);
                    this.putVarInt(0); // priority
                    this.putBoolean(false); // No unlock requirement
                    this.putUnsignedVarInt(furnace.getNetworkId());
                    break;
            }
        }

        this.putUnsignedVarInt(multiData.size());
        for (MultiRecipe recipe : multiData) {
            this.putUUID(recipe.getId());
            this.putUnsignedVarInt(recipe.getNetworkId());
        }

        this.putUnsignedVarInt(0); // user
        this.putUnsignedVarInt(0); // chemistry shapeless
        this.putUnsignedVarInt(0); // chemistry shaped

        this.putUnsignedVarInt(smithingTransformData.size());
        for (SmithingRecipe smithing : smithingTransformData) {
            this.putString(smithing.getRecipeId());
            this.putRecipeIngredient(smithing.getTemplate());
            this.putRecipeIngredient(smithing.getEquipment());
            this.putRecipeIngredient(smithing.getIngredient());
            this.putSlot(smithing.getResult(), true);
            this.putString(CRAFTING_TAG_SMITHING_TABLE);
            this.putUnsignedVarInt(smithing.getNetworkId());
        }

        this.putUnsignedVarInt(1);
        this.putString("minecraft:smithing_armor_trim"); // Recipe
        this.putTrimRecipeIngredient("minecraft:trim_templates");
        this.putTrimRecipeIngredient("minecraft:trimmable_armors");
        this.putTrimRecipeIngredient("minecraft:trim_materials");
        this.putString(CRAFTING_TAG_SMITHING_TABLE);
        this.putUnsignedVarInt(1); // Network ID (hardcoded in CraftingManager)

        this.putUnsignedVarInt(this.brewingEntries.size());
        for (BrewingRecipe recipe : brewingEntries) {
            this.putVarInt(recipe.getInput().getNetworkId());
            this.putVarInt(recipe.getInput().getDamage());
            this.putVarInt(recipe.getIngredient().getNetworkId());
            this.putVarInt(recipe.getIngredient().getDamage());
            this.putVarInt(recipe.getResult().getNetworkId());
            this.putVarInt(recipe.getResult().getDamage());
        }

        this.putUnsignedVarInt(this.containerEntries.size());
        for (ContainerRecipe recipe : containerEntries) {
            this.putVarInt(recipe.getInput().getNetworkId());
            this.putVarInt(recipe.getIngredient().getNetworkId());
            this.putVarInt(recipe.getResult().getNetworkId());
        }

        this.putUnsignedVarInt(0); // Material reducers size

        this.putBoolean(cleanRecipes);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    private void putTrimRecipeIngredient(String itemTag) {
        this.putUnsignedVarInt(1); // type
        this.putString("item_tag"); // type
        this.putString(itemTag);
        this.putVarInt(32767); // meta
        this.putVarInt(1); // count
    }
}

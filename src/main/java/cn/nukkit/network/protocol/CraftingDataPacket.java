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

    private List<Recipe> entries = new ArrayList<>();
    private final List<BrewingRecipe> brewingEntries = new ArrayList<>();
    private final List<ContainerRecipe> containerEntries = new ArrayList<>();
    public boolean cleanRecipes;

    public void addShapelessRecipe(ShapelessRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addShapedRecipe(ShapedRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addFurnaceRecipe(FurnaceRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addMultiRecipe(MultiRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addBrewingRecipe(BrewingRecipe... recipe) {
        Collections.addAll(brewingEntries, recipe);
    }

    public void addContainerRecipe(ContainerRecipe... recipe) {
        Collections.addAll(containerEntries, recipe);
    }

    @Override
    public DataPacket clean() {
        entries = new ArrayList<>();
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(entries.size());

        int recipeNetworkId = 1;

        for (Recipe recipe : entries) {
            this.putVarInt(recipe.getType().ordinal());
            switch (recipe.getType()) {
                case SHAPELESS:
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    this.putString(shapeless.getRecipeId());
                    List<Item> ingredients = shapeless.getIngredientList();
                    this.putUnsignedVarInt(ingredients.size());
                    for (Item ingredient : ingredients) {
                        this.putRecipeIngredient(ingredient);
                    }
                    this.putUnsignedVarInt(1);
                    this.putSlot(shapeless.getResult(), true);
                    this.putUUID(shapeless.getId());
                    this.putString(CRAFTING_TAG_CRAFTING_TABLE);
                    this.putVarInt(shapeless.getPriority());
                    this.putUnsignedVarInt(recipeNetworkId++);
                    break;
                case SHAPED:
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    this.putString(shaped.getRecipeId());
                    this.putVarInt(shaped.getWidth());
                    this.putVarInt(shaped.getHeight());

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
                    this.putUnsignedVarInt(recipeNetworkId++);
                    break;
                case FURNACE:
                case FURNACE_DATA:
                    FurnaceRecipe furnace = (FurnaceRecipe) recipe;
                    Item input = furnace.getInput();
                    this.putVarInt(input.getId());
                    if (recipe.getType() == RecipeType.FURNACE_DATA) {
                        this.putVarInt(input.getDamage());
                    }
                    this.putSlot(furnace.getResult(), true);
                    this.putString(CRAFTING_TAG_FURNACE);
                    break;
                case MULTI:
                    this.putUUID(((MultiRecipe) recipe).getId());
                    this.putUnsignedVarInt(recipeNetworkId++);
                    break;
            }
        }

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

}

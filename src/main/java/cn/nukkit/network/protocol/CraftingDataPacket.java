package cn.nukkit.network.protocol;

import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingDataPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.CRAFTING_DATA_PACKET;

    public static final String CRAFTING_TAG_CRAFTING_TABLE = "crafting_table";
    public static final String CRAFTING_TAG_CARTOGRAPHY_TABLE = "cartography_table";
    public static final String CRAFTING_TAG_STONECUTTER = "stonecutter";
    public static final String CRAFTING_TAG_FURNACE = "furnace";
    public static final String CRAFTING_TAG_CAMPFIRE = "campfire";
    public static final String CRAFTING_TAG_BLAST_FURNACE = "blast_furnace";
    public static final String CRAFTING_TAG_SMOKER = "smoker";

    public List<Recipe> entries = new ArrayList<>();
    public boolean cleanRecipes;

    public void addRecipes(Recipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addRecipes(Collection<? extends Recipe> recipes) {
        entries.addAll(recipes);
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, entries.size());

        for (Recipe recipe : entries) {
            Binary.writeVarInt(buffer, recipe.getType().ordinal());
            switch (recipe.getType()) {
                case SHAPELESS:
                case SHULKER_BOX:
                case SHAPED_CHEMISTRY:
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    Binary.writeString(buffer, shapeless.getRecipeId());
                    List<Item> ingredients = shapeless.getIngredientList();
                    Binary.writeUnsignedVarInt(buffer, ingredients.size());
                    for (Item ingredient : ingredients) {
                        Binary.writeRecipeIngredient(buffer, ingredient);
                    }
                    Binary.writeUnsignedVarInt(buffer, 1);
                    Binary.writeItem(buffer, shapeless.getResult());
                    Binary.writeUuid(buffer, shapeless.getId());
                    Binary.writeString(buffer, CRAFTING_TAG_CRAFTING_TABLE);
                    Binary.writeVarInt(buffer, shapeless.getPriority());
                    break;
                case SHAPED:
                case SHAPELESS_CHEMISTRY:
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    Binary.writeString(buffer, shaped.getRecipeId());
                    Binary.writeVarInt(buffer, shaped.getWidth());
                    Binary.writeVarInt(buffer, shaped.getHeight());

                    for (int z = 0; z < shaped.getHeight(); ++z) {
                        for (int x = 0; x < shaped.getWidth(); ++x) {
                            Binary.writeRecipeIngredient(buffer, shaped.getIngredient(x, z));
                        }
                    }
                    List<Item> outputs = new ArrayList<>();
                    outputs.add(shaped.getResult());
                    outputs.addAll(shaped.getExtraResults());
                    Binary.writeUnsignedVarInt(buffer, outputs.size());
                    for (Item output : outputs) {
                        Binary.writeItem(buffer, output);
                    }
                    Binary.writeUuid(buffer, shaped.getId());
                    Binary.writeString(buffer, CRAFTING_TAG_CRAFTING_TABLE);
                    Binary.writeVarInt(buffer, shaped.getPriority());
                    break;
                case FURNACE:
                case FURNACE_DATA:
                    FurnaceRecipe furnace = (FurnaceRecipe) recipe;
                    Item input = furnace.getInput();
                    Binary.writeVarInt(buffer, input.getId());
                    if (recipe.getType() == RecipeType.FURNACE_DATA) {
                        Binary.writeVarInt(buffer, input.getDamage());
                    }
                    Binary.writeItem(buffer, furnace.getResult());
                    Binary.writeString(buffer, CRAFTING_TAG_FURNACE);
                    break;
                case MULTI:
                    throw new UnsupportedOperationException();
            }
        }

        buffer.writeBoolean(cleanRecipes);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}

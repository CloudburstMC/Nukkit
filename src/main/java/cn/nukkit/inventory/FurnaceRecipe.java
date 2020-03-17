package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.CraftingData;

import javax.annotation.concurrent.Immutable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Immutable
public class FurnaceRecipe implements Recipe {

    private final Item output;
    private final Item ingredient;
    private final Identifier block;

    public FurnaceRecipe(Item result, Item ingredient, Identifier block) {
        this.output = result.clone();
        this.ingredient = ingredient.clone();
        this.block = block;
    }

    public Item getInput() {
        return this.ingredient.clone();
    }

    @Override
    public Item getResult() {
        return this.output.clone();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerFurnaceRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return this.ingredient.hasMeta() ? RecipeType.FURNACE_DATA : RecipeType.FURNACE;
    }

    @Override
    public Identifier getBlock() {
        return block;
    }

    @Override
    public CraftingData toNetwork() {
        if (this.ingredient.hasMeta()) {
            return CraftingData.fromFurnaceData(ingredient.getNetworkId(), ingredient.getMeta(), output.toNetwork(), block.getName());
        } else {
            return CraftingData.fromFurnace(ingredient.getNetworkId(), output.toNetwork(), block.getName());
        }
    }
}

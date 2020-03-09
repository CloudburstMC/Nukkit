package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.CraftingData;
import lombok.ToString;

@ToString
public abstract class MixRecipe implements Recipe {

    private final Item input;
    private final Item ingredient;
    private final Item output;

    public MixRecipe(Item input, Item ingredient, Item output) {
        this.input = input.clone();
        this.ingredient = ingredient.clone();
        this.output = output.clone();
    }

    public Item getIngredient() {
        return ingredient.clone();
    }

    public Item getInput() {
        return input.clone();
    }

    public Item getResult() {
        return output.clone();
    }

    @Override
    public RecipeType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CraftingData toNetwork() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Identifier getBlock() {
        throw new UnsupportedOperationException();
    }
}
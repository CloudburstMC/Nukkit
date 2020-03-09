package cn.nukkit.inventory;


import cn.nukkit.item.Item;
import com.nukkitx.protocol.bedrock.data.PotionMixData;


public class BrewingRecipe extends MixRecipe {

    public BrewingRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBrewingRecipe(this);
    }

    public PotionMixData toData() {
        return new PotionMixData(getInput().getMeta(), getIngredient().getNetworkId(), getResult().getMeta());
    }
}
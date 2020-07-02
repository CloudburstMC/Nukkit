package cn.nukkit.inventory;


import cn.nukkit.item.Item;
import com.nukkitx.protocol.bedrock.data.inventory.PotionMixData;


public class BrewingRecipe extends MixRecipe {

    public BrewingRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerBrewingRecipe(this);
    }

    public PotionMixData toData() {
        return new PotionMixData(getInput().getNetworkId(), getInput().getMeta(), getIngredient().getNetworkId(), getIngredient().getMeta(), getResult().getNetworkId(), getResult().getMeta());
    }
}
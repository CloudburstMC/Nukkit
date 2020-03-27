package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import com.nukkitx.protocol.bedrock.data.ContainerMixData;

public class ContainerRecipe extends MixRecipe {
    public ContainerRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerContainerRecipe(this);
    }

    public ContainerMixData toData() {
        return new ContainerMixData(getInput().getNetworkId(), getIngredient().getNetworkId(), getResult().getNetworkId());
    }
}

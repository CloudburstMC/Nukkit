package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;

import javax.annotation.concurrent.Immutable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Immutable
public interface Recipe {

    Item getResult();

    void registerToCraftingManager(CraftingManager manager);

    RecipeType getType();

    CraftingData toNetwork();

    Identifier getBlock();
}

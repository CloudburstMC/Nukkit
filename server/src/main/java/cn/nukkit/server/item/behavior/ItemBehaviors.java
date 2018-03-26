package cn.nukkit.server.item.behavior;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;

public class ItemBehaviors implements ItemBehavior {
    private static final ItemBehaviors INSTANCE = new ItemBehaviors();

    private ItemBehaviors() {
    }

    @Override
    public float getMiningEfficiency(Block block, @Nullable ItemInstance item) {
        return 0;
    }
}

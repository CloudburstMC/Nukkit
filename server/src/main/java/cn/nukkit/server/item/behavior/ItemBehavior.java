package cn.nukkit.server.item.behavior;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;

public interface ItemBehavior {

    float getMiningEfficiency(Block block, @Nullable ItemInstance item);

}

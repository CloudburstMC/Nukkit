package cn.nukkit.server.block.behavior;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.util.ItemTypeUtil;

import javax.annotation.Nullable;

public class DirtBlockBehavior extends SimpleBlockBehavior {
    public static final DirtBlockBehavior INSTANCE = new DirtBlockBehavior();

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && ItemTypeUtil.isShovel(item.getItemType());
    }
}

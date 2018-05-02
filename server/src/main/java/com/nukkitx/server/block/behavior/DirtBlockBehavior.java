package com.nukkitx.server.block.behavior;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.util.ItemTypeUtil;

import javax.annotation.Nullable;

public class DirtBlockBehavior extends SimpleBlockBehavior {
    public static final DirtBlockBehavior INSTANCE = new DirtBlockBehavior();

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && ItemTypeUtil.isShovel(item.getItemType());
    }
}

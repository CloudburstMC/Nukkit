package com.nukkitx.server.block.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.session.PlayerSession;

public class CropBlockBehavior extends SimpleBlockBehavior {

    @Override
    public boolean onPlace(PlayerSession session, Block against, ItemInstance withItem) {
        return super.onPlace(session, against, withItem);
    }
}

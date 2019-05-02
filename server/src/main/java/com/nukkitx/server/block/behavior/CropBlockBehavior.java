package com.nukkitx.server.block.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

public class CropBlockBehavior extends SimpleBlockBehavior {

    @Override
    public boolean onPlace(PlayerSession session, Block against, ItemStack withItem) {
        return super.onPlace(session, against, withItem);
    }
}

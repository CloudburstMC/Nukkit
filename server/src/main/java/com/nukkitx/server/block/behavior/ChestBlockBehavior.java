package com.nukkitx.server.block.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

/**
 * @author CreeperFace
 */
public class ChestBlockBehavior extends SimpleBlockBehavior {

    @Override
    public boolean onPlace(NukkitPlayerSession session, Block against, ItemStack withItem) {
        return super.onPlace(session, against, withItem);
    }

    @Override
    public Result onBreak(NukkitPlayerSession session, Block block, ItemStack withItem) {
        return super.onBreak(session, block, withItem);
    }
}

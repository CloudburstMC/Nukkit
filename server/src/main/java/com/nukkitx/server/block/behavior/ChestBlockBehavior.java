package com.nukkitx.server.block.behavior;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

/**
 * @author CreeperFace
 */
public class ChestBlockBehavior extends SimpleBlockBehavior {

    @Override
    public boolean onPlace(NukkitPlayerSession session, Block against, ItemInstance withItem) {
        return super.onPlace(session, against, withItem);
    }

    @Override
    public Result onBreak(NukkitPlayerSession session, Block block, ItemInstance withItem) {
        return super.onBreak(session, block, withItem);
    }
}

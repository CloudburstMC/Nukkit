package cn.nukkit.server.event.redstone;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.block.BlockUpdateEvent;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}


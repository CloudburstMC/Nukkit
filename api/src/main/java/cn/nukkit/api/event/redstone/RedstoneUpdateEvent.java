package cn.nukkit.api.event.redstone;

import cn.nukkit.api.event.block.BlockUpdateEvent;
import cn.nukkit.server.block.Block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}


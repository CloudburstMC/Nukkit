package cn.nukkit.event.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.event.block.BlockUpdateEvent;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}


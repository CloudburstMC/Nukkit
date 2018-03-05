package cn.nukkit.api.event.redstone;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.block.BlockUpdateEvent;

public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}


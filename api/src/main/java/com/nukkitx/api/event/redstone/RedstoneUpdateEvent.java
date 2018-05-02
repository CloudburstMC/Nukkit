package com.nukkitx.api.event.redstone;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.block.BlockUpdateEvent;

public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}


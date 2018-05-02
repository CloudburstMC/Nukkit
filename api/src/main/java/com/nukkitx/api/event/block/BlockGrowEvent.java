package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public interface BlockGrowEvent extends BlockEvent, Cancellable {

    Block getNewState();
}

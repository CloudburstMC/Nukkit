package cn.nukkit.api.event.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;

public interface BlockGrowEvent extends BlockEvent, Cancellable {

    Block getNewState();
}

package cn.nukkit.api.event.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Event;

public interface BlockEvent extends Event {

    Block getBlock();
}

package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Event;

public interface BlockEvent extends Event {

    Block getBlock();
}

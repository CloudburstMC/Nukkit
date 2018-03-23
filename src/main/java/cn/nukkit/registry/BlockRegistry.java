package cn.nukkit.registry;

import cn.nukkit.block.*;

public class BlockRegistry extends AbstractRegistry<Block> implements BlockID {
    @Override
    protected void init() {
        //porktodo: register blocks dynamically
    }

    @Override
    protected void parseArgs(Block obj, Object... args) {
        //porktodo: support block positions
    }
}

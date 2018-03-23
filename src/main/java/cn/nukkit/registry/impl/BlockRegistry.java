package cn.nukkit.registry.impl;

import cn.nukkit.block.*;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;

public class BlockRegistry extends AbstractRegistry<Block> implements BlockID {
    public BlockRegistry() {
        super(RegistryType.BLOCK);
    }

    @Override
    protected void init() {
        //porktodo: register blocks dynamically
    }

    @Override
    protected void parseArgs(Block obj, Object... args) {
        //porktodo: support block positions
    }
}

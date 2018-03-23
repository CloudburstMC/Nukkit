package cn.nukkit.registry.impl;

import cn.nukkit.block.*;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.IntObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public final class BlockRegistry extends AbstractRegistry<Block, IntObjectFunction<Block>> implements BlockID {
    public static final BlockRegistry INSTANCE = new BlockRegistry();

    private BlockRegistry() {
        super(RegistryType.BLOCK);
    }

    @Override
    protected void init() {
        //porktodo: register blocks dynamically
    }

    @Override
    protected Block accept(IntObjectFunction<Block> func, int i, Object... args) {
        if (args.length == 0)   {
            //damage value
            return func.accept(i);
        }
        //porktodo: other combinations (like blockvector3)
        return null;
    }
}

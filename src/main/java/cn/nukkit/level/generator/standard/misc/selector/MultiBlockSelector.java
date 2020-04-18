package cn.nukkit.level.generator.standard.misc.selector;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.registry.BlockRegistry;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;

/**
 * Implementation of {@link BlockSelector} which selects a block from a pool of options.
 *
 * @author DaPorkchop_
 */
public final class MultiBlockSelector implements BlockSelector {
    protected final int[] ids;

    protected MultiBlockSelector(@NonNull ConstantBlock[] blocks) {
        this.ids = Arrays.stream(blocks)
                .mapToInt(ConstantBlock::runtimeId)
                .toArray();
    }

    protected MultiBlockSelector(@NonNull int[] blocks) {
        this.ids = blocks.clone();
        for (int i : this.ids) {
            BlockRegistry.get().getBlock(i); //make sure id is valid
        }
    }

    @Override
    public Block select(@NonNull PRandom random) {
        return BlockRegistry.get().getBlock(this.ids[random.nextInt(this.ids.length)]);
    }

    @Override
    public int selectRuntimeId(@NonNull PRandom random) {
        return this.ids[random.nextInt(this.ids.length)];
    }
}

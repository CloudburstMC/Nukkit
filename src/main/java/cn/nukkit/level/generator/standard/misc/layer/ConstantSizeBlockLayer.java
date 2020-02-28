package cn.nukkit.level.generator.standard.misc.layer;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * A {@link BlockLayer} with a constant size.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class ConstantSizeBlockLayer implements BlockLayer {
    @Getter
    private final int blockId;
    private final int size;

    public ConstantSizeBlockLayer(@NonNull Block block, int size) {
        this.blockId = BlockRegistry.get().getRuntimeId(block);
        this.size = PValidation.ensurePositive(size);
    }

    @Override
    public int size(PRandom random) {
        return this.size;
    }
}

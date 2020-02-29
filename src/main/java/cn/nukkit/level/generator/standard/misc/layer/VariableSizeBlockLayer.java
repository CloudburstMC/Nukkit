package cn.nukkit.level.generator.standard.misc.layer;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * A {@link BlockLayer} with a size that is randomly chosen from a pre-determined range.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class VariableSizeBlockLayer implements BlockLayer {
    @Getter
    private final int runtimeId;
    private final int minSize;
    private final int maxSize;

    public VariableSizeBlockLayer(@NonNull Block block, int minSize, int maxSize) {
        this.runtimeId = BlockRegistry.get().getRuntimeId(block);
        this.minSize = PValidation.ensureNonNegative(minSize);
        this.maxSize = maxSize;
    }

    public VariableSizeBlockLayer(int runtimeId, int minSize, int maxSize) {
        BlockRegistry.get().getBlock(runtimeId); //ensure runtimeId is valid
        this.runtimeId = runtimeId;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    @Override
    public int size(@NonNull PRandom random) {
        return random.nextInt(this.minSize, this.maxSize);
    }
}

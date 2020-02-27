package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.level.generator.standard.misc.BlockMatcher;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Replaces all blocks of a given type with a certain pattern of replacement blocks from the top down.
 *
 * @author DaPorkchop_
 */
public final class SurfaceDecorator implements Decorator {
    private final BlockMatcher target;
    private final int[]        replaceIds;

    public SurfaceDecorator(@NonNull ConfigSection config, @NonNull PRandom random) {
        this.target = StandardGeneratorUtils.parseBlockMatcher(config.getString("target", "stone"));
        this.replaceIds = StandardGeneratorUtils.parseBlockList(config.getString("blocks")).stream()
                .mapToInt(BlockRegistry.get()::getRuntimeId)
                .toArray();
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        boolean done = false;
        for (int y = 255; y >= 0; y--) {
            int id = chunk.getBlockRuntimeIdUnsafe(x, y, z, 0);
            if (id == 0) {
                done = false;
            } else if (this.target.test(id)) {
                if (!done) {
                    done = true;
                    for (int i = 0; i < this.replaceIds.length && y >= 0; y--, i++) {
                        if (!this.target.test(chunk.getBlockRuntimeIdUnsafe(x, y, z, 0))) {
                            done = false;
                            break;
                        }
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.replaceIds[i]);
                    }
                }
            } else {
                //neither air nor a matching replacement block, abort!
                //porktodo: block pattern matcher
                return;
            }
        }
    }
}

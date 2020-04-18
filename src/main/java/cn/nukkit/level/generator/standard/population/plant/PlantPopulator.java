package cn.nukkit.level.generator.standard.population.plant;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Places patches of plants of varying heights in the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class PlantPopulator extends AbstractPlantPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:plant");

    @JsonProperty
    protected BlockSelector block;

    @JsonProperty
    protected BlockFilter water;

    @JsonProperty
    protected IntRange height;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.block, "block must be set!");
        Objects.requireNonNull(this.height, "height must be set!");
    }

    @Override
    protected void placeCluster(PRandom random, ChunkManager level, int x, int y, int z) {
        final BlockFilter on = this.on;
        final BlockFilter water = this.water;
        final BlockFilter replace = this.replace;
        final int block = this.block.selectRuntimeId(random);

        for (int i = this.patchSize - 1; i >= 0; i--) {
            int blockY = y + random.nextInt(4) - random.nextInt(4);
            int height = this.height.rand(random);
            if (blockY < 0 || blockY >= 256 - height) {
                continue;
            }
            int blockX = x + random.nextInt(8) - random.nextInt(8);
            int blockZ = z + random.nextInt(8) - random.nextInt(8);

            IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
            if (!on.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY, blockZ & 0xF, 0))) {
                continue;
            }
            if (water != null && !(water.test(level.getBlockRuntimeIdUnsafe(blockX + 1, blockY, blockZ, 0))
                    || water.test(level.getBlockRuntimeIdUnsafe(blockX - 1, blockY, blockZ, 0))
                    || water.test(level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ + 1, 0))
                    || water.test(level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ - 1, 0)))) {
                continue;
            }
            for (int dy = 1; dy <= height && replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY + dy, blockZ & 0xF, 0))
                    && replace.test(level.getBlockRuntimeIdUnsafe(blockX + 1, blockY + dy, blockZ, 0))
                    && replace.test(level.getBlockRuntimeIdUnsafe(blockX - 1, blockY + dy, blockZ, 0))
                    && replace.test(level.getBlockRuntimeIdUnsafe(blockX, blockY + dy, blockZ + 1, 0))
                    && replace.test(level.getBlockRuntimeIdUnsafe(blockX, blockY + dy, blockZ - 1, 0)); dy++) {
                chunk.setBlockRuntimeIdUnsafe(blockX & 0xF, blockY + dy, blockZ & 0xF, 0, block);
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

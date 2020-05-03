package cn.nukkit.level.generator.standard.population.plant;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;

/**
 * Places patches of 1-block-tall plants in the world.
 * <p>
 * https://i.imgur.com/BUocESm.gif
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ShrubPopulator extends AbstractPlantPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:shrub");

    @JsonProperty
    protected BlockSelector block;

    @JsonProperty
    protected boolean roundDown = true;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.block, "block must be set!");
    }

    @Override
    protected void populate0(PRandom random, ChunkManager level, int blockX, int blockZ) {
        if (this.roundDown) {
            int height = level.getChunk(blockX >> 4, blockZ >> 4).getHighestBlock(blockX & 0xF, blockZ & 0xF);
            this.placeCluster(random, level, blockX, min(height, random.nextInt(height << 1)), blockZ);
        } else {
            super.populate0(random, level, blockX, blockZ);
        }
    }

    @Override
    protected void placeCluster(PRandom random, ChunkManager level, int x, int y, int z) {
        final BlockFilter on = this.on;
        final BlockFilter replace = this.replace;
        final int block = this.block.selectRuntimeId(random);

        for (int i = this.patchSize - 1; i >= 0; i--) {
            int blockY = y + random.nextInt(4) - random.nextInt(4);
            if (blockY < 0 || blockY >= 255) {
                continue;
            }
            int blockX = x + random.nextInt(8) - random.nextInt(8);
            int blockZ = z + random.nextInt(8) - random.nextInt(8);

            IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
            if (on.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY, blockZ & 0xF, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0))) {
                chunk.setBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0, block);
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

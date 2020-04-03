package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.block.BlockCocoa;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class CocoaPopulator extends VinesPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:cocoa");

    public boolean avoidDouble = false;

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        final double chance = this.chance;
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;
        final boolean avoidDouble = this.avoidDouble;

        final int x = chunkX << 4;
        final int z = chunkZ << 4;

        for (int y = this.height.min, max = this.height.max; y < max; y++)   {
            for (int dx = 0; dx < 16; dx++) {
                for (int dz = 0; dz < 16; dz++) {
                    if (random.nextDouble() >= chance || !replace.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0))) {
                        continue;
                    }

                    if (on.test(level.getBlockRuntimeIdUnsafe(x + dx - 1, y, z + dz, 0)))   {
                        if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(x + dx - 2, y, z + dz, 0))) {
                            level.setBlockAt(x + dx, y, z + dz, 0, BlockIds.COCOA, BlockCocoa.EAST);
                        }
                    } else if (on.test(level.getBlockRuntimeIdUnsafe(x + dx + 1, y, z + dz, 0)))   {
                        if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(x + dx + 2, y, z + dz, 0))) {
                            level.setBlockAt(x + dx, y, z + dz, 0, BlockIds.COCOA, BlockCocoa.WEST);
                        }
                    } else if (on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz - 1, 0)))   {
                        if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz - 2, 0))) {
                            level.setBlockAt(x + dx, y, z + dz, 0, BlockIds.COCOA, BlockCocoa.SOUTH);
                        }
                    } else if (on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz + 1, 0)))   {
                        if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz + 2, 0))) {
                            level.setBlockAt(x + dx, y, z + dz, 0, BlockIds.COCOA, BlockCocoa.NORTH);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

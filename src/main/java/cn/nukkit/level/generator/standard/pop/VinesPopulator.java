package cn.nukkit.level.generator.standard.pop;

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
public class VinesPopulator extends ChancePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:vines");

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        final double chance = this.chance;
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        final int x = chunkX << 4;
        final int z = chunkZ << 4;

        for (int y = this.height.min, max = this.height.max; y < max; y++)   {
            for (int dx = 0; dx < 16; dx++) {
                for (int dz = 0; dz < 16; dz++) {
                    if (random.nextDouble() >= chance || !replace.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0))) {
                        continue;
                    }

                    int meta = 0;
                    if (on.test(level.getBlockRuntimeIdUnsafe(x + dx - 1, y, z + dz, 0)))   {
                        meta |= BlockVine.WEST;
                    }
                    if (on.test(level.getBlockRuntimeIdUnsafe(x + dx + 1, y, z + dz, 0)))   {
                        meta |= BlockVine.EAST;
                    }
                    if (on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz - 1, 0)))   {
                        meta |= BlockVine.NORTH;
                    }
                    if (on.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz + 1, 0)))   {
                        meta |= BlockVine.SOUTH;
                    }

                    if (meta != 0)  {
                        level.setBlockAt(x + dx, y, z + dz, 0, BlockIds.VINE, meta);
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

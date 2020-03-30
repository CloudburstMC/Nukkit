package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Places large spikes in the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class BlobPopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:blob");

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @JsonProperty
    @JsonAlias({"types", "block", "blocks"})
    protected BlockSelector type;

    @JsonProperty
    protected IntRange radius;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.type, "type must be set!");
        Objects.requireNonNull(this.radius, "radius must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = level.getChunk(x >> 4, z >> 4).getHighestBlock(x & 0xF, z & 0xF);
        if (y < 0 || !this.on.test(level.getBlockRuntimeIdUnsafe(x, y, z, 0))) {
            return;
        }

        final BlockFilter replace = this.replace;
        final int type = this.type.selectRuntimeId(random);
        final int min = this.radius.min;

        for (int i = 0; i < 3; i++) {
            int vx = this.radius.rand(random);
            int vy = this.radius.rand(random);
            int vz = this.radius.rand(random);
            double g = (vx + vy + vz) * 0.333333333333d + 0.5d;
            g *= g;

            for (int dx = -vx; dx <= vx; dx++)  {
                for (int dy = -vy; dy <= vy; dy++)  {
                    if (y + dy < 0 || y + dy >= 256)    {
                        continue;
                    }
                    for (int dz = -vz; dz <= vz; dz++)  {
                        if (dx * dx + dy * dy + dz * dz <= g && replace.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0))) {
                            level.setBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0, type);
                        }
                    }
                }
            }

            x += random.nextInt(-(min + 1), min + 2);
            y -= min > 0 ? random.nextInt(min) : 0;
            z += random.nextInt(-(min + 1), min + 2);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

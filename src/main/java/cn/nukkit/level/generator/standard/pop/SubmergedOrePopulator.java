package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Generates underwater ore veins.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SubmergedOrePopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:ore_submerged");

    @JsonProperty
    protected BlockFilter replace;

    @JsonProperty
    protected BlockFilter start;

    @JsonProperty
    protected IntRange radius;

    @JsonProperty
    @JsonAlias({"types", "block", "blocks"})
    protected BlockSelector type;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.start, "start must be set!");
        Objects.requireNonNull(this.radius, "radius must be set!");
        Objects.requireNonNull(this.type, "type must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = 254;
        for (IChunk chunk = level.getChunk(x >> 4, z >> 4); y > 0; y--)  {
            if (this.replace.test(chunk.getBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, 0)))   {
                break;
            }
        }
        if (y == 0 || !this.start.test(level.getBlockRuntimeIdUnsafe(x, y + 1, z, 0))) {
            return;
        }

        final int radius = this.radius.rand(random);
        final int radiusSq = radius * radius;
        final int block = this.type.selectRuntimeId(random);

        for (int dx = -radius; dx <= radius; dx++)  {
            for (int dz = -radius; dz <= radius; dz++)  {
                if (dx * dx + dz * dz <= radiusSq)  {
                    for (int dy = -1; dy < 1; dy++) {
                        if (this.replace.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0)))    {
                            level.setBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0, block);
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

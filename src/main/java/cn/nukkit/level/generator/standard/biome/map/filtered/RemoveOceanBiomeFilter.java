package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;
import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class RemoveOceanBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:remove_ocean");

    @JsonProperty
    protected GenerationBiome replacement;
    @JsonProperty
    protected GenerationBiome ocean;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.replacement, "replacement must be set!");
        Objects.requireNonNull(this.ocean, "ocean must be set!");

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return this.next.getAllBiomes();
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowX = x - 1;
        int belowZ = z - 1;
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(belowX, belowZ, belowSizeX, belowSizeZ, alloc);

        final int replacement = this.replacement.getInternalId();
        final int ocean = this.ocean.getInternalId();

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int v0 = below[dx * belowSizeZ + dz + 1];
                int v1 = below[(dx + 1) * belowSizeZ + dz + 2];
                int v2 = below[(dx + 1) * belowSizeZ + dz];
                int v3 = below[(dx + 2) * belowSizeZ + dz + 1];
                int center = below[(dx + 1) * belowSizeZ + dz + 1];

                out[dx * sizeZ + dz] = center == ocean && v0 == ocean && v1 == ocean && v2 == ocean && v3 == ocean
                        && this.random(x + dx, z + dz, 0, 2) == 0 ? replacement : center;
            }
        }
        alloc.release(below);

        return out;
    }
}

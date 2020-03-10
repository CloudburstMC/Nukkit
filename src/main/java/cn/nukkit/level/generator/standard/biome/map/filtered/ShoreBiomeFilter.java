package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ShoreBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:shore");

    protected int oceanId;
    protected int beachId;

    @JsonProperty
    protected GenerationBiome ocean;
    @JsonProperty
    protected GenerationBiome beach;

    @Override
    public void init(long seed, PRandom random) {
        this.oceanId = Objects.requireNonNull(this.ocean, "ocean must be set!").getInternalId();
        this.beachId = Objects.requireNonNull(this.beach, "beach must be set!").getInternalId();

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.ocean);
        biomes.add(this.beach);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        final int oceanId = this.oceanId;
        final int beachId = this.beachId;

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                int v0 = below[(dx + 1) * belowSizeZ + dz];
                int v1 = below[(dx + 1) * belowSizeZ + (dz + 2)];
                int v2 = below[dx * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 2) * belowSizeZ + (dz + 1)];

                out[dx * sizeZ + dz] = center != oceanId && (v0 == oceanId || v1 == oceanId || v2 == oceanId || v3 == oceanId) ? beachId : center;
            }
        }
        alloc.release(below);

        return out;
    }
}

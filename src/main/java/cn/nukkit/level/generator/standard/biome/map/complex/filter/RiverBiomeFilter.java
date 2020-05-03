package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Combines a network of river biomes with the main biome grid.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class RiverBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:river");

    protected int riverId;
    protected int fallbackId;

    @JsonProperty
    protected GenerationBiome river;
    @JsonProperty
    protected GenerationBiome fallback;

    @Override
    public void init(long seed, PRandom random) {
        this.riverId = Objects.requireNonNull(this.river, "river must be set!").getInternalId();
        this.fallbackId = Objects.requireNonNull(this.fallback, "fallback must be set!").getInternalId();

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>();
        biomes.add(this.river);
        biomes.add(this.fallback);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        int[] out = alloc.get(sizeX * sizeZ);

        final int riverId = this.riverId;
        final int fallbackId = this.fallbackId;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                int v0 = below[dx * belowSizeZ + (dz + 1)];
                int v1 = below[(dx + 1) * belowSizeZ + dz];
                int v2 = below[(dx + 2) * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 1) * belowSizeZ + (dz + 2)];

                out[dx * sizeZ + dz] = center == v0 && center == v1 && center == v2 && center == v3 ? fallbackId : riverId;
            }
        }
        alloc.release(below);

        return out;
    }
}

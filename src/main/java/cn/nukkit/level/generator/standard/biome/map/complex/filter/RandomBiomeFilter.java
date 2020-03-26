package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Randomly selects a biome from a pre-defined list.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class RandomBiomeFilter extends AbstractBiomeFilter {
    public static final Identifier ID = Identifier.fromString("nukkitx:random");

    protected int[] biomeIds;

    @JsonProperty
    protected GenerationBiome[] biomes;

    @Override
    public void init(long seed, PRandom random) {
        this.biomeIds = Arrays.stream(Objects.requireNonNull(this.biomes, "biomes must be set!"))
                .mapToInt(GenerationBiome::getInternalId)
                .toArray();

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return ImmutableList.copyOf(this.biomes);
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int[] out = alloc.get(sizeX * sizeZ);

        final int[] biomeIds = this.biomeIds;
        final int biomeIdCount = biomeIds.length;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                out[dx * sizeZ + dz] = biomeIds[this.random(x + dx, z + dz, 0, biomeIdCount)];
            }
        }

        return out;
    }
}

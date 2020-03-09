package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author DaPorkchop_
 */
public class AddSnowBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:add_snow");

    protected int[] ids;

    @JsonProperty
    protected GenerationBiome ocean;

    @JsonProperty
    protected GenerationBiome[] biomes;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.ocean, "ocean must be set!");

        this.ids = Arrays.stream(Objects.requireNonNull(this.biomes, "biomes must be set!"))
                .mapToInt(GenerationBiome::getInternalId)
                .toArray();

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return this.next.getAllBiomes();
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int[] below = this.next.get(x, z, sizeX, sizeZ, alloc);

        final int ocean = this.ocean.getInternalId();

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeZ; dx++) {
            for (int dz = 0; dz < sizeX; dz++) {
                out[dx * sizeZ + dz] = below[dx * sizeZ + dz] == ocean ? ocean
                        : this.ids[this.random(x + dx, z + dz, 0, this.ids.length)];
            }
        }

        return out;
    }
}

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
 * Replaces all biome intersections with rivers, except for those neighboring an ocean.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class BorderRiverBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:border_river");

    protected int oceanId;
    protected int riverId;

    @JsonProperty
    protected GenerationBiome ocean;
    @JsonProperty
    protected GenerationBiome river;

    @Override
    public void init(long seed, PRandom random) {
        this.oceanId = Objects.requireNonNull(this.ocean, "ocean must be set!").getInternalId();
        this.riverId = Objects.requireNonNull(this.river, "river must be set!").getInternalId();

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.ocean);
        biomes.add(this.river);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        final int oceanId = this.oceanId;
        final int riverId = this.riverId;

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                int v0 = below[(dx + 1) * belowSizeZ + dz];
                int v1 = below[(dx + 1) * belowSizeZ + (dz + 2)];
                int v2 = below[dx * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 2) * belowSizeZ + (dz + 1)];

                int id = center;
                if (center != oceanId && v0 != oceanId && v1 != oceanId && v2 != oceanId && v3 != oceanId) {
                    if (v0 != center || v1 != center || v2 != center || v3 != center) {
                        id = riverId;
                    }
                }
                out[dx * sizeZ + dz] = id;
            }
        }
        alloc.release(below);

        return out;
    }
}

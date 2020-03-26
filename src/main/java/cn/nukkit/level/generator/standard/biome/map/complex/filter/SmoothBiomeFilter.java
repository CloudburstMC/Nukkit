package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collection;

/**
 * Replaces all biome intersections with rivers, except for those neighboring an ocean.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SmoothBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:smooth");

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return this.next.getAllBiomes();
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                int v0 = below[(dx + 1) * belowSizeZ + dz];
                int v1 = below[(dx + 1) * belowSizeZ + (dz + 2)];
                int v2 = below[dx * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 2) * belowSizeZ + (dz + 1)];

                int id = center;
                if (v0 == v1 && v2 == v3) {
                    id = this.random(x + dx, z + dz, 0, 2) == 0 ? v0 : v2;
                } else {
                    if (v0 == v1) {
                        id = v0;
                    }

                    if (v2 == v3) {
                        id = v2;
                    }
                }
                out[dx * sizeZ + dz] = id;
            }
        }
        alloc.release(below);

        return out;
    }
}

package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ZoomBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:zoom");

    @JsonProperty
    protected int times = 1;

    @JsonProperty
    protected boolean fuzzy = false;

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return this.next.getAllBiomes();
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        return this.times > 0
                ? this.actuallyDoGet(x, z, sizeX, sizeZ, alloc, this.times)
                : this.next.get(x, z, sizeX, sizeZ, alloc);
    }

    protected int[] actuallyDoGet(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc, int depth) {
        int belowX = x >> 1;
        int belowZ = z >> 1;
        int belowSizeX = (sizeX >> 1) + 2;
        int belowSizeZ = (sizeZ >> 1) + 2;
        int[] values = depth == 1
                ? this.next.get(belowX, belowZ, belowSizeX, belowSizeZ, alloc)
                : this.actuallyDoGet(belowX, belowZ, belowSizeX, belowSizeZ, alloc, depth - 1);

        int zoomSizeX = (belowSizeX - 1) << 1;
        int zoomSizeZ = (belowSizeZ - 1) << 1;
        int[] tmp = alloc.get(zoomSizeX * zoomSizeZ);
        for (int dx = 0; dx < belowSizeX - 1; dx++) {
            for (int dz = 0; dz < belowSizeZ - 1; dz++) {
                int i = (dx << 1) * zoomSizeZ + (dz << 1);

                int rndX = (belowX + dx) << 1;
                int rndZ = (belowZ + dz) << 1;

                int v0 = values[dx * belowSizeZ + dz];
                int v1 = values[dx * belowSizeZ + (dz + 1)];
                int v2 = values[(dx + 1) * belowSizeZ + dz];
                int v3 = values[(dx + 1) * belowSizeZ + (dz + 1)];

                tmp[i] = v0;
                tmp[i + 1] = this.random(rndX, rndZ, 1, 2) == 0 ? v0 : v1;
                tmp[i + zoomSizeZ] = this.random(rndX, rndZ, 2, 2) == 0 ? v0 : v2;
                tmp[i + zoomSizeZ + 1] = this.fuzzy
                        ? this.selectRandom(rndX, rndZ, v0, v1, v2, v3)
                        : this.selectNormal(rndX, rndZ, v0, v1, v2, v3);
            }
        }
        alloc.release(values);

        int[] finalValues = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                finalValues[dx * sizeZ + dz] = tmp[dz + (dx + (z & 1)) * zoomSizeX + (x & 1)];
            }
        }
        alloc.release(tmp);

        return finalValues;
    }

    protected int selectNormal(int x, int z, int v0, int v1, int v2, int v3) {
        if (v1 == v2 && v2 == v3) {
            return v1;
        } else if (v0 == v1 && v0 == v2) {
            return v0;
        } else if (v0 == v1 && v0 == v3) {
            return v0;
        } else if (v0 == v2 && v0 == v3) {
            return v0;
        } else if (v0 == v1 && v2 != v3) {
            return v0;
        } else if (v0 == v2 && v1 != v3) {
            return v0;
        } else if (v0 == v3 && v1 != v2) {
            return v0;
        } else if (v1 == v2 && v0 != v3) {
            return v1;
        } else if (v1 == v3 && v0 != v2) {
            return v1;
        } else if (v2 == v3 && v0 != v1) {
            return v2;
        }

        return this.selectRandom(x, z, v0, v1, v2, v3);
    }

    protected int selectRandom(int x, int z, int v0, int v1, int v2, int v3) {
        switch (this.random(x, z, 0, 4)) {
            case 0:
                return v0;
            case 1:
                return v1;
            case 2:
                return v2;
            case 3:
                return v3;
            default:
                throw new IllegalStateException();
        }
    }
}

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
public class AddIslandBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:add_island");

    @JsonProperty
    protected GenerationBiome fallback;
    @JsonProperty
    protected GenerationBiome special;
    @JsonProperty
    protected GenerationBiome ocean;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.fallback, "fallback must be set!");
        Objects.requireNonNull(this.special, "special must be set!");
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
        int[] out = alloc.get(sizeX * sizeX);

        final int fallback = this.fallback.getInternalId();
        final int special = this.special.getInternalId();
        final int ocean = this.ocean.getInternalId();

        PRandom random = this.random();
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                this.updateSeed(random, x + dx, z + dz);

                int i = dx * sizeZ + dz;

                int v0 = below[dx * belowSizeZ + dz];
                int v1 = below[dx * belowSizeZ + dz + 2];
                int v2 = below[(dx + 2) * belowSizeZ + dz];
                int v3 = below[(dx + 2) * belowSizeZ + dz + 2];
                int center = below[(dx + 1) * belowSizeZ + dz + 1];

                if (center != ocean || v0 == ocean && v1 == ocean && v2 == ocean && v3 == ocean) {
                    if (center != ocean && (v0 == ocean || v1 == ocean || v2 == ocean || v3 == ocean)) {
                        if (random.nextInt(5) == 0) {
                            out[i] = center == special ? special : ocean;
                        } else {
                            out[i] = center;
                        }
                    } else {
                        out[i] = center;
                    }
                } else {
                    int id = fallback;

                    if (v0 != ocean) {
                        id = v0;
                    }

                    if (v1 != ocean && random.nextInt(2) == 0) {
                        id = v1;
                    }

                    if (v2 != ocean && random.nextInt(3) == 0) {
                        id = v2;
                    }

                    if (v3 != ocean && random.nextInt(4) == 0) {
                        id = v3;
                    }

                    if (random.nextInt(3) == 0) {
                        out[i] = id;
                    } else if (id == special) {
                        out[i] = special;
                    } else {
                        out[i] = ocean;
                    }
                }
            }
        }
        alloc.release(below);

        return out;
    }
}

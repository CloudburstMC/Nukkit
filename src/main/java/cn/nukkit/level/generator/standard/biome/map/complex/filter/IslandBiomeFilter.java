package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Generates a random pattern of islands.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class IslandBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:island");

    protected int[] islandIds;
    protected int   oceanId;
    @JsonProperty
    protected int chance = 9;

    @JsonProperty
    protected GenerationBiome[] islandBiomes;
    @JsonProperty
    protected GenerationBiome   ocean;

    @Override
    public void init(long seed, PRandom random) {
        this.islandIds = Arrays.stream(Objects.requireNonNull(this.islandBiomes, "islandBiomes must be set!"))
                .mapToInt(GenerationBiome::getInternalId)
                .toArray();
        this.oceanId = Objects.requireNonNull(this.ocean, "ocean must be set!").getInternalId();
        PValidation.ensurePositive(this.chance++);

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        Collections.addAll(biomes, this.islandBiomes);
        biomes.add(this.ocean);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        int[] out = alloc.get(sizeX * sizeZ);

        final int oceanId = this.oceanId;
        final int chance = this.chance;
        final int[] islandIds = this.islandIds;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                out[dx * sizeZ + dz] = center == oceanId
                        && below[dx * belowSizeZ + (dz + 1)] == oceanId
                        && below[(dx + 1) * belowSizeZ + dz] == oceanId
                        && below[(dx + 2) * belowSizeZ + (dz + 1)] == oceanId
                        && below[(dx + 1) * belowSizeZ + (dz + 2)] == oceanId
                        && this.random(x + dx, z + dz, 0, chance) == 0
                        ? islandIds[this.random(x + dx, z + dz, 1, islandIds.length)] : center;
            }
        }
        alloc.release(below);

        return out;
    }
}

package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static java.lang.Math.*;

/**
 * Generates a random pattern of islands.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class IslandBiomeFilter extends AbstractBiomeFilter {
    public static final Identifier ID = Identifier.fromString("nukkitx:island");

    @JsonProperty
    protected GenerationBiome biome;
    @JsonProperty
    protected int chance = 10;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.biome, "biome must be set!");
        PValidation.ensurePositive(this.chance);

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return Collections.singleton(this.biome);
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int[] arr = alloc.get(sizeX * sizeZ);

        for (int chance = this.chance, dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                arr[dx * sizeZ + dz] = min(this.random(x + dx, z + dz, 0, chance), 1);
            }
        }

        return arr;
    }
}

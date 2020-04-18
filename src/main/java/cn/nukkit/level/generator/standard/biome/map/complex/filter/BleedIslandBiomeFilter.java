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
import java.util.Collection;
import java.util.Objects;

/**
 * "Bleeds" or "smears" biomes into a neighboring replacement biome, and sporadically generates "islands" of a certain type inside of
 * the replacement biome".
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class BleedIslandBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:bleed_island");

    protected int oceanId;
    protected int preserveId;
    protected int islandId;
    @JsonProperty
    protected int chance = 5;
    @JsonProperty
    protected int bleedChance = 3;

    @JsonProperty
    protected GenerationBiome ocean;
    @JsonProperty
    protected GenerationBiome preserve;
    @JsonProperty
    protected GenerationBiome island;

    @Override
    public void init(long seed, PRandom random) {
        this.oceanId = Objects.requireNonNull(this.ocean, "ocean must be set!").getInternalId();
        this.preserveId = Objects.requireNonNull(this.preserve, "preserve must be set!").getInternalId();
        this.islandId = Objects.requireNonNull(this.island, "island must be set!").getInternalId();
        PValidation.ensurePositive(this.chance);
        PValidation.ensurePositive(this.bleedChance);

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.ocean);
        biomes.add(this.preserve);
        biomes.add(this.island);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        int[] out = alloc.get(sizeX * sizeZ);

        final int oceanId = this.oceanId;
        final int preserveId = this.preserveId;
        final int islandId = this.islandId;
        final int chance = this.chance;
        final int bleedChance = this.bleedChance;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];

                int v0 = below[dx * belowSizeZ + (dz + 1)];
                int v1 = below[(dx + 1) * belowSizeZ + dz];
                int v2 = below[(dx + 2) * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 1) * belowSizeZ + (dz + 2)];

                if (center != oceanId || (v0 == oceanId && v1 == oceanId && v2 == oceanId && v3 == oceanId)) {
                    if (center != oceanId && (v0 == oceanId || v1 == oceanId || v2 == oceanId || v3 == oceanId)) {
                        if (this.random(dx + x, dz + z, 0, chance) == 0) {
                            out[dx * sizeZ + dz] = center == preserveId ? center : oceanId;
                            continue;
                        }
                    }
                    out[dx * sizeZ + dz] = center;
                } else {
                    int limit = 1;
                    int next = islandId;

                    if (v0 != oceanId && this.random(dx + x, dz + z, 0, limit++) == 0) {
                        next = v0;
                    }
                    if (v1 != oceanId && this.random(dx + x, dz + z, 1, limit++) == 0) {
                        next = v1;
                    }
                    if (v2 != oceanId && this.random(dx + x, dz + z, 2, limit++) == 0) {
                        next = v2;
                    }
                    if (v3 != oceanId && this.random(dx + x, dz + z, 3, limit) == 0) {
                        next = v3;
                    }

                    if (this.random(dx + x, dz + z, 4, bleedChance) == 0) {
                        out[dx * sizeZ + dz] = next;
                    } else {
                        out[dx * sizeZ + dz] = next == preserveId ? next : oceanId;
                    }
                }
            }
        }
        alloc.release(below);

        return out;
    }
}

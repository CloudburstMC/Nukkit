package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Replaces a specific biome with another one when it and a certain number of its neighbors are that biome.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ReplaceThresholdBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:replace_threshold");

    protected int targetId;
    protected int replacementId;

    @JsonProperty
    protected int threshold = -1;

    @JsonProperty
    protected GenerationBiome target;
    @JsonProperty
    protected GenerationBiome replacement;

    @Override
    public void init(long seed, PRandom random) {
        this.targetId = Objects.requireNonNull(this.target, "target must be set!").getInternalId();
        this.replacementId = Objects.requireNonNull(this.replacement, "replacement must be set!").getInternalId();
        Preconditions.checkState(this.threshold >= 0, "threshold must be set!");

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.target);
        biomes.add(this.replacement);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        int[] out = alloc.get(sizeX * sizeZ);

        final int targetId = this.targetId;
        final int replacementId = this.replacementId;
        final int threshold = this.threshold;

        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];

                if (center == targetId) {
                    int count = 0;
                    if (below[dx * belowSizeZ + (dz + 1)] == targetId) {
                        count++;
                    }
                    if (below[(dx + 1) * belowSizeZ + dz] == targetId) {
                        count++;
                    }
                    if (below[(dx + 2) * belowSizeZ + (dz + 1)] == targetId) {
                        count++;
                    }
                    if (below[(dx + 1) * belowSizeZ + (dz + 2)] == targetId) {
                        count++;
                    }

                    if (count >= threshold) {
                        center = replacementId;
                    }
                }

                out[dx * sizeZ + dz] = center;
            }
        }
        alloc.release(below);

        return out;
    }
}

package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Replaces biomes according to climate patterns.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ClimateBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:climate");

    protected int icyId;
    protected int coolId;
    protected int warmId;
    protected int hotId;
    protected int oceanId;

    @JsonProperty
    protected int specialChance;

    @JsonProperty
    protected GenerationBiome icy;
    @JsonProperty
    protected GenerationBiome cool;
    @JsonProperty
    protected GenerationBiome warm;
    @JsonProperty
    protected GenerationBiome hot;
    @JsonProperty
    protected GenerationBiome ocean;

    @Override
    public void init(long seed, PRandom random) {
        this.icyId = Objects.requireNonNull(this.icy, "icy must be set!").getInternalId();
        this.coolId = Objects.requireNonNull(this.cool, "cool must be set!").getInternalId();
        this.warmId = Objects.requireNonNull(this.warm, "warm must be set!").getInternalId();
        this.hotId = Objects.requireNonNull(this.hot, "hot must be set!").getInternalId();
        this.oceanId = Objects.requireNonNull(this.ocean, "ocean must be set!").getInternalId();
        Preconditions.checkState(this.specialChance > 0, "specialChance must be set!");

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.icy);
        biomes.add(this.cool);
        biomes.add(this.warm);
        biomes.add(this.hot);
        biomes.add(this.ocean);
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 6;
        int belowSizeZ = sizeZ + 6;
        int[] below = this.next.get(x - 3, z - 3, belowSizeX, belowSizeZ, alloc);

        final int icyId = this.icyId;
        final int coolId = this.coolId;
        final int warmId = this.warmId;
        final int hotId = this.hotId;

        //merge hot with icy/cool to make warm
        for (int dx = 0; dx < belowSizeX - 2; dx++) {
            for (int dz = 0; dz < belowSizeZ - 2; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];

                if (center == hotId) {
                    int v0 = below[dx * belowSizeZ + (dz + 1)];
                    int v1 = below[(dx + 1) * belowSizeZ + dz];
                    int v2 = below[(dx + 2) * belowSizeZ + (dz + 1)];
                    int v3 = below[(dx + 1) * belowSizeZ + (dz + 2)];

                    if (v0 == icyId || v0 == coolId || v1 == icyId || v1 == coolId
                            || v2 == icyId || v2 == coolId || v3 == icyId || v3 == coolId) {
                        center = warmId;
                    }
                }

                below[(dx + 1) * belowSizeZ + (dz + 1)] = center;
            }
        }

        //merge icy with warm/hot to make warm
        for (int dx = 1; dx < belowSizeX - 4; dx++) {
            for (int dz = 1; dz < belowSizeZ - 4; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];

                if (center == icyId) {
                    int v0 = below[dx * belowSizeZ + (dz + 1)];
                    int v1 = below[(dx + 1) * belowSizeZ + dz];
                    int v2 = below[(dx + 2) * belowSizeZ + (dz + 1)];
                    int v3 = below[(dx + 1) * belowSizeZ + (dz + 2)];

                    if (v0 == hotId || v0 == warmId || v1 == hotId || v1 == warmId
                            || v2 == hotId || v2 == warmId || v3 == hotId || v3 == warmId) {
                        center = coolId;
                    }
                }

                below[(dx + 1) * belowSizeZ + (dz + 1)] = center;
            }
        }

        final int oceanId = this.oceanId;
        final int specialChance = this.specialChance;

        int[] out = alloc.get(sizeX * sizeZ);
        //do some funky stuff
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 3) * belowSizeZ + (dz + 3)];

                if (center != oceanId && this.random(dx + x, dz + z, 0, specialChance) == 0) {
                    //shift by 16, assume there will never be 65535 biomes in one world lol
                    center |= ((1 + this.random(dx + x, dz + z, 1, 15)) << 16) & 0xF0000;
                }

                out[dx * sizeZ + dz] = center;
            }
        }
        alloc.release(below);


        return out;
    }
}

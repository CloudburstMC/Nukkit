package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ShoreBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:shore");

    protected final IntSet oceanBiomes = new IntOpenHashSet();
    protected final Int2IntMap replacements = new Int2IntOpenHashMap();

    @JsonProperty
    protected GenerationBiome defaultBeach;

    @JsonProperty
    protected GenerationBiome[] oceans;
    @JsonProperty
    protected Map<String, GenerationBiome> beaches;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.defaultBeach, "defaultBeach must be set!");

        for (GenerationBiome biome : Objects.requireNonNull(this.oceans, "oceans must be set!")) {
            this.oceanBiomes.add(biome.getInternalId());
        }

        Objects.requireNonNull(this.beaches, "beaches must be set!").forEach((key, biome) -> {
            int replaceId = StandardGeneratorStores.generationBiome().find(Identifier.fromString(key)).getInternalId();
            this.replacements.put(replaceId, biome.getInternalId());
        });

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.next.getAllBiomes());
        biomes.add(this.defaultBeach);
        biomes.addAll(this.beaches.values());
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int belowSizeX = sizeX + 2;
        int belowSizeZ = sizeZ + 2;
        int[] below = this.next.get(x - 1, z - 1, belowSizeX, belowSizeZ, alloc);

        final int beachId = this.defaultBeach.getInternalId();

        int[] out = alloc.get(sizeX * sizeZ);
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dz = 0; dz < sizeZ; dz++) {
                int center = below[(dx + 1) * belowSizeZ + (dz + 1)];
                int v0 = below[(dx + 1) * belowSizeZ + dz];
                int v1 = below[(dx + 1) * belowSizeZ + (dz + 2)];
                int v2 = below[dx * belowSizeZ + (dz + 1)];
                int v3 = below[(dx + 2) * belowSizeZ + (dz + 1)];

                if (!this.oceanBiomes.contains(center)
                        && (this.oceanBiomes.contains(v0) || this.oceanBiomes.contains(v1) || this.oceanBiomes.contains(v2) || this.oceanBiomes.contains(v3))) {
                    center = this.replacements.getOrDefault(center, beachId);
                }

                out[dx * sizeZ + dz] = center;
            }
        }
        alloc.release(below);

        return out;
    }
}

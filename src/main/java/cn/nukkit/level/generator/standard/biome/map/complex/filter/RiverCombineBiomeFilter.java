package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.complex.BiomeFilter;
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
 * Combines a network of river biomes with the main biome grid.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class RiverCombineBiomeFilter extends AbstractBiomeFilter {
    public static final Identifier ID = Identifier.fromString("nukkitx:river_combine");

    @JsonProperty
    protected BiomeFilter biomeLayer;
    @JsonProperty
    protected BiomeFilter riverLayer;

    protected final IntSet oceanBiomes = new IntOpenHashSet();
    protected final Int2IntMap replacements = new Int2IntOpenHashMap();

    @JsonProperty
    protected GenerationBiome defaultRiver;

    @JsonProperty
    protected GenerationBiome[] oceans;
    @JsonProperty
    protected Map<String, GenerationBiome> riverBiomes;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.biomeLayer, "biomeLayer must be set!").init(seed, random);
        Objects.requireNonNull(this.riverLayer, "riverLayer must be set!").init(seed, random);

        Objects.requireNonNull(this.defaultRiver, "defaultRiver must be set!");

        for (GenerationBiome biome : Objects.requireNonNull(this.oceans, "oceans must be set!")) {
            this.oceanBiomes.add(biome.getInternalId());
        }

        Objects.requireNonNull(this.riverBiomes, "riverBiomes must be set!").forEach((key, biome) -> {
            int replaceId = StandardGeneratorStores.generationBiome().find(Identifier.fromString(key)).getInternalId();
            this.replacements.put(replaceId, biome.getInternalId());
        });

        super.init(seed, random);
    }

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        Collection<GenerationBiome> biomes = new ArrayList<>(this.biomeLayer.getAllBiomes());
        biomes.addAll(this.riverLayer.getAllBiomes());
        biomes.add(this.defaultRiver);
        biomes.addAll(this.riverBiomes.values());
        return biomes;
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        int[] baseLayer = this.biomeLayer.get(x, z, sizeX, sizeZ, alloc);
        int[] riverLayer = this.riverLayer.get(x, z, sizeX, sizeZ, alloc);

        final int riverId = this.defaultRiver.getInternalId();

        int[] out = alloc.get(sizeX * sizeZ);
        for (int i = 0, max = sizeX * sizeZ; i < max; i++) {
            int base = baseLayer[i];
            int river = riverLayer[i];

            if (!this.oceanBiomes.contains(base) && river == riverId) {
                base = this.replacements.getOrDefault(base, riverId);
            }

            out[i] = base;
        }
        alloc.release(baseLayer);
        alloc.release(riverLayer);

        return out;
    }
}

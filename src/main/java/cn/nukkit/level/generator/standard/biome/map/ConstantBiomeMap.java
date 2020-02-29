package cn.nukkit.level.generator.standard.biome.map;

import cn.nukkit.level.generator.standard.biome.BiomeDictionary;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Implementation of {@link BiomeMap} which returns a constant biome.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class ConstantBiomeMap implements BiomeMap {
    public static final Identifier ID = Identifier.fromString("nukkitx:constant");

    @JsonProperty(required = true)
    private GenerationBiome biome;

    @Override
    public GenerationBiome get(int x, int z) {
        return this.biome;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

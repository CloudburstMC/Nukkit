package cn.nukkit.level.generator.standard.biome.map;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * Implementation of {@link BiomeMap} which returns a constant biome.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class ConstantBiomeMap extends AbstractGenerationPass implements BiomeMap {
    public static final Identifier ID = Identifier.fromString("nukkitx:constant");

    @JsonProperty
    private GenerationBiome biome;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.biome, "biome must be set!");
    }

    @Override
    public GenerationBiome get(int x, int z) {
        return this.biome;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

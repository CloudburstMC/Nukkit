package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Implementation of {@link BiomeMap} which uses a series of iterative passes ("filters") to progressively select a biome.
 * <p>
 * This follows a similar pattern to vanilla biome selection.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class FilteredBiomeMap implements BiomeMap {
    public static final Identifier ID = Identifier.fromString("nukkitx:filtered");

    @JsonProperty
    protected BiomeFilter root;

    @JsonProperty
    protected GenerationBiome fallback;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.root, "root must be set!");
        Objects.requireNonNull(this.fallback, "fallback must be set!");
    }

    @Override
    public GenerationBiome get(int x, int z) {
        x = (int) (x * (1.0d / 32.0d));
        z = (int) (z * (1.0d / 32.0d));
        return fallbackIfNull(this.root.get(x, z), this.fallback);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

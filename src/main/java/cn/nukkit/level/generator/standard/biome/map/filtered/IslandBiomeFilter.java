package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Generates islands randomly scattered around the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class IslandBiomeFilter implements BiomeFilter {
    public static final Identifier ID = Identifier.fromString("nukkitx:island");

    protected long seed;

    @JsonProperty
    protected GenerationBiome biome;
    @JsonProperty
    protected int chance = 10;

    @Override
    public void init(long seed, PRandom random) {
        Objects.requireNonNull(this.biome, "biome must be set!");
        PValidation.ensurePositive(this.chance);

        this.seed = random.nextLong();
    }

    @Override
    public GenerationBiome get(int x, int z) {
        int i = (int) (((this.seed + x) * 6364136223846793005L + 1442695040888963407L + z) % this.chance);
        return (i < 0 ? i + this.chance : i) == 0 ? this.biome : null;
    }
}

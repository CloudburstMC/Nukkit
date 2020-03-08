package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

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
    public GenerationBiome get(int x, int z) {
        return this.random(x, z, 0, this.chance) == 0 ? this.biome : null;
    }
}

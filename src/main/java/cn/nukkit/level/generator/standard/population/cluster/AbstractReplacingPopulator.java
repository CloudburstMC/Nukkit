package cn.nukkit.level.generator.standard.population.cluster;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.population.ChancePopulator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class AbstractReplacingPopulator extends ChancePopulator.Column {
    @JsonProperty
    protected BlockFilter replace;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.replace, "replace must be set!");
    }
}

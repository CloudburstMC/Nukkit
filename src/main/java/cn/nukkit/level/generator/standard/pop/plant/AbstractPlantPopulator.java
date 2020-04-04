package cn.nukkit.level.generator.standard.pop.plant;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.cluster.AbstractClusterPopulator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class AbstractPlantPopulator extends AbstractClusterPopulator {
    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected int size = 64;

    public AbstractPlantPopulator() {
        this.replace = BlockFilter.AIR;
        this.height = IntRange.WHOLE_WORLD;
    }

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.on, "on must be set!");
        Preconditions.checkState(this.size > 0, "size must be at least 1!");
    }
}

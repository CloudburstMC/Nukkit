package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.feature.tree.TreeSpecies;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.tree.AbstractTreePopulator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.Math.min;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class TreePopulator extends AbstractTreePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:tree");

    protected WorldFeature[] types;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.types, "type must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    protected void tryPlaceTree(PRandom random, ChunkManager level, int x, int y, int z) {
        this.types[random.nextInt(this.types.length)].place(level, random, x, y + 1, z);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("type")
    private void setType(@NonNull ConfigTree type) {
        this.types = new WorldFeature[]{type.build()};
    }

    @JsonSetter("types")
    private void setTypes(@NonNull ConfigTree[] types) {
        Preconditions.checkArgument(types.length > 0, "types may not be empty!");
        this.types = Arrays.stream(types)
                .map(ConfigTree::build)
                .toArray(WorldFeature[]::new);
    }

    @JsonDeserialize
    private static final class ConfigTree {
        private final TreeSpecies species;
        private       IntRange    height;

        @JsonCreator
        public ConfigTree(String species) {
            this.species = TreeSpecies.valueOf(species.toUpperCase());
        }

        @JsonCreator
        public ConfigTree(
                @JsonProperty(value = "species", required = true) @NonNull String species,
                @JsonProperty(value = "height") IntRange height) {
            this(species);

            this.height = height;
        }

        public WorldFeature build() {
            if (this.height != null) {
                return Preconditions.checkNotNull(this.species.getDefaultGenerator(this.height), "%s does not support huge trees!", this.species.name());
            } else {
                return Preconditions.checkNotNull(this.species.getDefaultGenerator(), "%s does not support normal trees!", this.species.name());
            }
        }
    }
}

package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.feature.tree.TreeSpecies;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.ChancePopulator;
import cn.nukkit.level.generator.standard.pop.RepeatingPopulator;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.Arrays;
import java.util.Objects;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class TreePopulator extends ChancePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:tree");

    @JsonProperty
    private BlockFilter replace = BlockFilter.REPLACEABLE;

    @JsonProperty
    private BlockFilter on;

    private WorldFeature[] types;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.types, "type must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ, int blockX, int blockZ) {
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        for (int x = 0; x < 16; x++)    {
            for (int z = 0; z < 16; z++)    {
                blockX = (chunkX << 4) | x;
                blockZ = (chunkZ << 4) | z;

                for (int y = 254, id, lastId = level.getBlockRuntimeIdUnsafe(blockX, 255, blockZ, 0); y >= 0; y--) {
                    id = level.getBlockRuntimeIdUnsafe(blockX, y, blockZ, 0);

                    if (replace.test(lastId) && on.test(id) && random.nextDouble() < this.chance) {
                        this.types[random.nextInt(this.types.length)].place(level, random, blockX, y + 1, blockZ);
                    }

                    lastId = id;
                }
            }
        }
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
        private WorldFeature feature;

        @JsonCreator
        public ConfigTree(String species) {
            this.feature = TreeSpecies.valueOf(species.toUpperCase()).getDefaultGenerator();
        }

        //porktodo: ways to access the other tree varieties

        public WorldFeature build() {
            return this.feature;
        }
    }
}

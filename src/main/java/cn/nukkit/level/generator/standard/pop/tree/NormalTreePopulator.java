package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.feature.tree.FeatureNormalTree;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.level.generator.standard.pop.RepeatingPopulator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class NormalTreePopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:normal_tree");

    @JsonProperty
    private BlockFilter   on;
    @JsonProperty
    private BlockSelector replaceOn;

    private FeatureNormalTree tree;

    @JsonProperty
    private IntRange      height;
    @JsonProperty
    private ConstantBlock trunk;
    @JsonProperty
    private ConstantBlock leaf;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.on, "on must be set!");
        this.tree = new FeatureNormalTree(
                Objects.requireNonNull(this.height, "height must be set!"),
                Objects.requireNonNull(this.trunk, "trunk must be set!").block(),
                Objects.requireNonNull(this.leaf, "leaf must be set!").block());

        this.height = null;
        this.trunk = this.leaf = null;
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = 255;

        int id;
        while ((id = level.getBlockRuntimeIdUnsafe(x, y, z, 0)) == 0 && --y >= 0) ;
        if (this.on.test(id) && this.tree.place(level, random, x, y + 1, z)) {
            level.setBlockRuntimeIdUnsafe(x, y, z, 0, this.replaceOn.selectRuntimeId(random));
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}

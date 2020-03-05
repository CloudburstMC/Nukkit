package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PorkianV2NoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.impl.FastPRandom;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class SurfaceReplacer implements BlockReplacer {
    public static final Identifier ID = Identifier.fromString("nukkitx:surface");

    //make this global, as it's not bound to the world seed
    public static final NoiseSource DEPTH_NOISE = new ScaleOctavesOffsetFilter(
            new PorkianV2NoiseEngine(new FastPRandom(0xDEADBEEF00001337L)), //porkian noise is really fast, also this will be called a lot and isn't very noticable
            0.0078125d, 0.0d, 0.0078125d,
            4,
            2.3333333333333335d, 3.0d);

    @JsonProperty
    private Block top      = BlockRegistry.get().getBlock(BlockIds.GRASS, 0);
    @JsonProperty
    private Block fill     = BlockRegistry.get().getBlock(BlockIds.DIRT, 0);
    @JsonProperty
    private Block deepFill; //for e.g. sandstone
    @JsonProperty
    private int   seaLevel = 63 - 1;

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        if (prev == null || density < 0.0d || density > 9.0d * abs(gradY)) {
            return prev;
        }

        double depth = DEPTH_NOISE.get(x, z);
        if (density + gradY <= 0.0d) {
            if (y < this.seaLevel) {
                return depth > 0.0d ? this.fill : prev;
            } else {
                return depth > 0.0d ? this.top : null;
            }
        } else {
            if (gradY < 0.0d && density / abs(gradY) < depth + 1.0d - sqrt(gradX * gradX + gradZ * gradZ) / gradY) {
                return this.fill;
            }

            if (this.deepFill != null && depth > 1.0d && y > this.seaLevel - depth) {
                return this.deepFill;
            }
        }
        return prev;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("top")
    private void setTop(ConstantBlock block) {
        this.top = block.block();
    }

    @JsonSetter("fill")
    private void setFill(ConstantBlock block) {
        this.fill = block.block();
    }

    @JsonSetter("deepFill")
    private void setDeepFill(ConstantBlock block) {
        this.deepFill = block.block();
    }

    @JsonSetter
    private void setSeaLevel(int seaLevel) {
        Preconditions.checkArgument(seaLevel >= 0 && seaLevel < 256, "seaLevel (%s) must be in range 0-255!", seaLevel);
        this.seaLevel = seaLevel - 1;
    }
}

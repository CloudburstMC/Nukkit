package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.utils.ConfigSection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Replaces all positive density values with a configured block.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class GroundReplacer implements BlockReplacer {
    @JsonProperty(required = true)
    private Block block;

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        return density > 0.0d ? this.block : prev;
    }

    @JsonSetter("block")
    private void setBlock(ConstantBlock block)  {
        this.block = block.block();
    }
}

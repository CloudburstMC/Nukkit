package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Replaces all positive density values with a configured block.
 *
 * @author DaPorkchop_
 */
public final class GroundReplacer implements BlockReplacer {
    private final Block block;

    public GroundReplacer(@NonNull ConfigSection config, @NonNull PRandom random)   {
        this.block = StandardGeneratorUtils.parseBlock(config.getString("block"));
    }

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        return density > 0.0d ? density + gradY > 0.0d ? this.block : BlockRegistry.get().getBlock(BlockIds.GRASS, 0) : prev;
        //return density > 0.0d ? this.block : prev;
    }
}

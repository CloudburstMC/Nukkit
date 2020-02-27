package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * Replaces air blocks below a configured sea level with a configured block.
 *
 * @author DaPorkchop_
 */
public final class SeaReplacer implements BlockReplacer {
    private final Block block;
    private final int   seaLevel;

    public SeaReplacer(@NonNull ConfigSection config, @NonNull PRandom random) {
        this.block = StandardGeneratorUtils.getBlock(config, "block");
        this.seaLevel = PValidation.ensureNonNegative(config.getInt("seaLevel", -1));
    }

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        return prev == null && y <= this.seaLevel ? this.block : prev;
    }
}

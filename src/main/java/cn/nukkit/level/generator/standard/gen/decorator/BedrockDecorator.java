package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

/**
 * Places a given block type using a vanilla bedrock pattern.
 *
 * @author DaPorkchop_
 */
public final class BedrockDecorator implements Decorator {
    private final int startY;
    private final int runtimeId;
    private final int step;
    private final int fade;
    private final int base;

    public BedrockDecorator(@NonNull ConfigSection config, @NonNull PRandom random) {
        this.runtimeId = BlockRegistry.get().getRuntimeId(StandardGeneratorUtils.parseBlock(config.getString("block", "bedrock")));
        this.startY = config.getInt("startY");
        this.step = config.getBoolean("reverse") ? -1 : 1;
        this.fade = PValidation.ensureNonNegative(config.getInt("fade", -1));
        this.base = PValidation.ensureNonNegative(config.getInt("base", -1));
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        int y = this.startY;
        for (int i = this.base - 1; i >= 0 && (y & 0xFF) == y; i--, y += this.step) {
            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.runtimeId);
        }
        for (int i = 0; i < this.fade && (y & 0xFF) == y; i++, y += this.step) {
            if (random.nextInt(i + 2) == 0) {
                chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.runtimeId);
            }
        }
    }
}

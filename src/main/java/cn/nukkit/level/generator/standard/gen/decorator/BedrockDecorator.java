package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.ConfigSection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import static cn.nukkit.level.generator.standard.StandardGeneratorUtils.*;

/**
 * Places a given block type using a vanilla bedrock pattern.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class BedrockDecorator implements Decorator {
    private final int startY;
    private final int runtimeId;
    private final int step;
    private final int base;
    private final int fade;

    @JsonCreator
    public BedrockDecorator(
            @JsonProperty("startY") int startY,
            @JsonProperty(value = "block", required = true) String block,
            @JsonProperty("reverse") boolean reverse,
            @JsonProperty(value = "base", required = true) int base,
            @JsonProperty(value = "fade", required = true) int fade) {
        Preconditions.checkArgument(startY >= 0 && startY < 256, "startY must be in range 0-255!");
        Preconditions.checkArgument(base >= 0, "base may not be negative!");
        Preconditions.checkArgument(fade >= 0, "fade may not be negative!");

        int bound = reverse ? startY - base - fade : startY + base + fade;
        Preconditions.checkArgument(bound >= 0 && bound < 256, "bedrock must be in range 0-255!");

        this.startY = startY;
        this.runtimeId = BlockRegistry.get().getRuntimeId(parseBlock(block));
        this.step = reverse ? -1 : 1;
        this.base = base;
        this.fade = fade;
    }

    @Deprecated
    public BedrockDecorator(@NonNull ConfigSection config, @NonNull PRandom random) {
        this.runtimeId = BlockRegistry.get().getRuntimeId(parseBlock(config.getString("block", "bedrock")));
        this.startY = config.getInt("startY");
        this.step = config.getBoolean("reverse") ? -1 : 1;
        this.base = PValidation.ensureNonNegative(config.getInt("base", -1));
        this.fade = PValidation.ensureNonNegative(config.getInt("fade", -1));
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

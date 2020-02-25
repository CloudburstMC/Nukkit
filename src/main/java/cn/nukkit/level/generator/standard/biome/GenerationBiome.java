package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
public final class GenerationBiome {
    private final Identifier      id;
    private final BlockReplacer[] replacers;
    //porktodo: populators
    private final int             runtimeId;

    public GenerationBiome(@NonNull ConfigSection config) {
        this.id = StandardGeneratorUtils.getId(config, "id");
        this.runtimeId = PValidation.ensureNonNegative(config.getInt("runtimeId"));

        this.replacers = new BlockReplacer[0]; //porktodo: this
    }

    public Identifier getId() {
        return this.id;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }
}

package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
public final class GenerationBiome {
    private final Identifier      id;
    private final BlockReplacer[] replacers;
    private final Decorator[]     decorators;
    private final Populator[]     populators;
    private final int             runtimeId;

    public GenerationBiome(@NonNull BiomeDictionary dictionary, @NonNull Identifier id) {
        this.id = id;
        this.runtimeId = dictionary.get(id);

        this.replacers = new BlockReplacer[0]; //porktodo: this
        this.decorators = new Decorator[0];
        this.populators = new Populator[0];
    }

    public Identifier getId() {
        return this.id;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }
}

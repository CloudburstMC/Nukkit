package cn.nukkit.level.generator.standard;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.DensitySource;
import cn.nukkit.level.generator.standard.registry.StandardGeneratorRegistries;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Strings;
import net.daporkchop.lib.random.PRandom;

import static cn.nukkit.level.generator.standard.StandardGeneratorUtils.*;

/**
 * Main class of the NukkitX Standard Generator.
 *
 * @author DaPorkchop_
 */
public final class StandardGenerator implements Generator {
    public static final Identifier ID = Identifier.fromString("minecraft:standard");

    private static final String DEFAULT_PRESET = "nukkitx:overworld";

    private final DensitySource   density;
    private final BlockReplacer[] replacers;

    public StandardGenerator(long seed, String options) {
        Identifier presetId = Identifier.fromString(Strings.isNullOrEmpty(options) ? DEFAULT_PRESET : options);
        Config preset = StandardGeneratorUtils.loadUnchecked("preset", presetId);

        ConfigSection noiseConfig = preset.getSection("generation.density");
        this.density = StandardGeneratorRegistries.worldNoise()
                .apply(noiseConfig, computeRandom(seed, "generation.density", noiseConfig));

        this.replacers = preset.<ConfigSection>getList("generation.replacers").stream()
                .map(section -> StandardGeneratorRegistries.blockReplacer()
                        .apply(section, computeRandom(seed, "generation.replacers", section)))
                .toArray(BlockReplacer[]::new);
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        final BlockReplacer[] replacers = this.replacers; //getfield is slow

        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    int blockX = baseX | x;
                    int blockZ = baseZ | z;
                    double density = this.density.get(blockX, y, blockZ, null);
                    Block block = null;
                    for (BlockReplacer replacer : replacers) {
                        block = replacer.replace(block, blockX, y, blockZ, 0.0d, 0.0d, 0.0d, density);
                    }
                    if (block != null) {
                        chunk.setBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
    }
}

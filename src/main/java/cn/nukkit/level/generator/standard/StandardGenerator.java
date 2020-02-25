package cn.nukkit.level.generator.standard;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.registry.StandardGeneratorRegistries;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Strings;
import net.daporkchop.lib.random.PRandom;

/**
 * Main class of the NukkitX Standard Generator.
 *
 * @author DaPorkchop_
 */
public final class StandardGenerator implements Generator {
    public static final Identifier ID = Identifier.fromString("minecraft:standard");

    private static final String DEFAULT_PRESET = "nukkitx:overworld";

    private final BlockReplacer[] replacers;

    public StandardGenerator(long seed, String options) {
        Identifier presetId = Identifier.fromString(Strings.isNullOrEmpty(options) ? DEFAULT_PRESET : options);
        Config preset = StandardGeneratorUtils.load("preset", presetId);

        this.replacers = preset.<ConfigSection>getList("generation.replacers").stream()
                .map(StandardGeneratorRegistries.blockReplacerRegistry()::create)
                .toArray(BlockReplacer[]::new);
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        BlockReplacer[] replacers = this.replacers; //getfield is slow
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = null;
                    for (BlockReplacer replacer : replacers) {
                        block = replacer.replace(block, x + (chunkX << 2), y, z + (chunkZ << 2), 0.0d, 0.0d, 0.0d, 0.0d);
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

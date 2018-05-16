package com.nukkitx.server.level.populator.impl.bedrock;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.server.level.populator.ChunkPopulator;

import java.util.Random;

/**
 * Generates bedrock at y=127 (for the nether)
 *
 * @author DaPorkchop_
 */
public class CeilBedrockPopulator implements ChunkPopulator, BedrockPopulator {
    @Override
    public void populate(Level level, Chunk chunk, Random random) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 5; y++) {
                    if (y == 0 || random.nextInt(y + 1) == 0)   {
                        chunk.setBlock(x, 127 - y, z, BEDROCK);
                    }
                }
            }
        }
    }
}

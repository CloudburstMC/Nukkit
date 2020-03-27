package cn.nukkit.level.generator.feature.generator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.feature.GeneratorFeature;
import cn.nukkit.math.BedrockRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static cn.nukkit.block.BlockIds.BEDROCK;

/**
 * @author DaPorkchop_
 * <p>
 * Places bedrock on the bottom of the world
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BedrockFeature implements GeneratorFeature {
    public static final BedrockFeature INSTANCE = new BedrockFeature();

    @Override
    public void generate(BedrockRandom random, IChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockId(x, 0, z, BEDROCK);
                for (int i = 1; i < 5; i++) {
                    if (random.nextInt(i) == 0) { //decreasing amount
                        chunk.setBlockId(x, i, z, BEDROCK);
                    }
                }
            }
        }
    }
}

package cn.nukkit.level.generator.feature.generator;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.feature.GeneratorFeature;
import cn.nukkit.math.BedrockRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroundCoverFeature implements GeneratorFeature {
    public static final GroundCoverFeature INSTANCE = new GroundCoverFeature();

    @Override
    public void generate(BedrockRandom random, IChunk chunk) {
        //reverse iteration to 0 is faster
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                Biome realBiome = EnumBiome.getBiome(chunk.getBiome(x, z));
                if (realBiome instanceof CoveredBiome) {
                    ((CoveredBiome) realBiome).doCover(x, z, chunk);
                }
            }
        }
    }
}

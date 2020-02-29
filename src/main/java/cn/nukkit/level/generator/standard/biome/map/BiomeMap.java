package cn.nukkit.level.generator.standard.biome.map;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A map of the biomes in a given area.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = BiomeMapDeserializer.class)
public interface BiomeMap extends GenerationPass {
    /**
     * Gets the {@link GenerationBiome} at the given world coordinates.
     *
     * @param x the X coordinate
     * @param z the Z coordinate
     * @return the {@link GenerationBiome} at the given coordinates
     */
    GenerationBiome get(int x, int z);

    @Override
    Identifier getId();
}

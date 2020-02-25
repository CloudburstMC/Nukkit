package cn.nukkit.level.generator.standard.biome;

/**
 * A map of the biomes in a given area.
 *
 * @author DaPorkchop_
 */
public interface BiomeMap {
    /**
     * Gets the {@link GenerationBiome} at the given world coordinates.
     *
     * @param x the X coordinate
     * @param z the Z coordinate
     * @return the {@link GenerationBiome} at the given coordinates
     */
    GenerationBiome get(int x, int z);
}

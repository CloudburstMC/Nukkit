package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.biome.type.CoveredBiome;

/**
 * @author DaPorkchop_
 * <p>
 * Occurs when Extreme hills and variants touch the ocean.
 *
 * Nearly ertical cliffs, but no overhangs. Height difference is 2-7 near ocean, and pretty much flat everywhere else
 */
public class StoneBeachBiome extends CoveredBiome {
    private static final Block AIR = Block.get(BlockIds.AIR);

    public StoneBeachBiome() {
        this.setBaseHeight(0.1f);
        this.setHeightVariation(0.8f);
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public Block getSurface(int x, int y, int z) {
        return AIR;
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return 0;
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return AIR;
    }

    @Override
    public String getName() {
        return "Stone Beach";
    }
}

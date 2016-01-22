package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OceanBiome extends GrassyBiome {

    public OceanBiome() {
        super();

        this.setElevation(46, 58);

        this.temperature = 0.5;
        this.rainfall = 0.5;

        this.setGroundCover(new Block[]{
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0),
                Block.get(Block.DIRT, 0)
        });
    }

    @Override
    public Block[] getGroundCover() {
        return super.getGroundCover();
    }

    @Override
    public String getName() {
        return "Ocean";
    }
}

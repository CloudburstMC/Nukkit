package cn.nukkit.level.generator.biome;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.generator.populator.PopulatorLilyPad;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SwampBiome extends GrassyBiome {

    public SwampBiome() {
        super();
        
        PopulatorLilyPad lilypad = new PopulatorLilyPad();
        lilypad.setBaseAmount(4); 
        
        PopulatorTree trees = new PopulatorTree(BlockSapling.OAK);
        trees.setBaseAmount(2);
        
        this.addPopulator(trees); 
        this.addPopulator(lilypad);
        
        this.setElevation(62, 63);

        this.temperature = 0.8;
        this.rainfall = 0.9;
    }

    @Override
    public String getName() {
        return "Swamp";
    }

    @Override
    public int getColor() {
        return 0x6a7039;
    }
}

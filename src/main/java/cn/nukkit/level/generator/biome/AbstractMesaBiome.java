package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRedSandstone;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockSandstone;
import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;

public abstract class AbstractMesaBiome extends SandyBiome {
    public AbstractMesaBiome() {
        this.setGroundCover(new Block[]{
                new BlockSand(BlockSand.RED),
                new BlockSand(BlockSand.RED),
                new BlockSand(BlockSand.RED),
                new BlockRedSandstone(),
                new BlockRedSandstone()
        });

        PopulatorDeadBush deadBush = new PopulatorDeadBush();
        deadBush.setBaseAmount(2);
        deadBush.setRandomAmount(2);
        this.addPopulator(deadBush);

        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setRandomAmount(1);
        this.addPopulator(cactus);
    }

    @Override
    public String getName() {
        return "Mesa";
    }
}

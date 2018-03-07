package cn.nukkit.level.generator.biome;

import cn.nukkit.block.BlockSand;
import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;

public abstract class AbstractMesaBiome extends SandyBiome {
    public AbstractMesaBiome() {
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

    @Override
    public int getSurfaceDepth() {
        return 3;
    }

    @Override
    public int getSurfaceBlock() {
        return SAND;
    }

    @Override
    public int getSurfaceMeta() {
        return BlockSand.RED;
    }

    @Override
    public int getGroundDepth() {
        return 2;
    }

    @Override
    public int getGroundBlock() {
        return RED_SANDSTONE;
    }
}

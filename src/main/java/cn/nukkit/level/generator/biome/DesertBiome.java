package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DesertBiome extends SandyBiome {
    public DesertBiome() {
        super();
        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(2);

        PopulatorDeadBush deadbush = new PopulatorDeadBush();
        deadbush.setBaseAmount(2);

        this.addPopulator(cactus);
        this.addPopulator(deadbush);

        this.setElevation(67, 71);
        this.temperature = 2;
        this.rainfall = 0;
    }

    @Override
    public String getName() {
        return "Desert";
    }
}

package cn.nukkit.level.biome.impl.desert;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.type.SandyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorCactus;
import cn.nukkit.level.generator.populator.impl.PopulatorDeadBush;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class DesertBiome extends SandyBiome {
    public DesertBiome() {
        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(2);
        this.addPopulator(cactus);

        PopulatorDeadBush deadbush = new PopulatorDeadBush();
        deadbush.setBaseAmount(2);
        this.addPopulator(deadbush);

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Desert";
    }

    @Override
    public boolean canRain() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDry() {
        return true;
    }
}

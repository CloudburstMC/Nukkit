package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.generator.populator.impl.tree.JungleFloorPopulator;

/**
 * @author DaPorkchop_
 */
public class JungleMBiome extends JungleBiome {
    public JungleMBiome() {
        super();

        JungleFloorPopulator floor = new JungleFloorPopulator();
        floor.setBaseAmount(10);
        floor.setRandomAmount(5);
        this.addPopulator(floor);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Jungle M";
    }
}

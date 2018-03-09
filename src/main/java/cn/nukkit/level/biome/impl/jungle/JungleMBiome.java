package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.tree.JungleFloorPopulator;
import cn.nukkit.level.generator.populator.tree.JungleTreePopulator;

/**
 * @author DaPorkchop_
 */
//porktodo: melons + pumpkins (GroundBlockPopulator)
public class JungleMBiome extends JungleBiome {
    public JungleMBiome() {
        super();

        JungleFloorPopulator floor = new JungleFloorPopulator();
        floor.setBaseAmount(10);
        floor.setRandomAmount(5);
        this.addPopulator(floor);

        this.setElevation(73, 90);
    }

    @Override
    public String getName() {
        return "Jungle M";
    }
}

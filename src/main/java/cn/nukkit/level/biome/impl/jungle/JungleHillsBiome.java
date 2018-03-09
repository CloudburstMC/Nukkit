package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.tree.JungleTreePopulator;

/**
 * @author DaPorkchop_
 */
public class JungleHillsBiome extends JungleBiome {
    public JungleHillsBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Jungle Hills";
    }
}

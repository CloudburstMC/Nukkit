package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.tree.JungleTreePopulator;

/**
 * @author DaPorkchpo_
 */
public class JungleEdgeBiome extends JungleBiome {
    public JungleEdgeBiome() {
        super();

        this.setElevation(67, 73);
    }

    @Override
    public String getName() {
        return "Jungle Edge";
    }
}

package cn.nukkit.level.biome.impl.jungle;

/**
 * @author DaPorkchpo_
 */
//porktodo: this biome has steep cliffs and flat plains
public class JungleEdgeMBiome extends JungleEdgeBiome {
    public JungleEdgeMBiome() {
        super();

        this.setElevation(67, 90);
    }

    @Override
    public String getName() {
        return "Jungle Edge M";
    }
}

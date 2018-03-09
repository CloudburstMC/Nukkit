package cn.nukkit.level.biome.impl.jungle;

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

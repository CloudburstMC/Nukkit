package cn.nukkit.server.level.generator.biome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SmallMountainsBiome extends MountainsBiome {

    public SmallMountainsBiome() {
        super();

        this.setElevation(63, 97);

    }

    @Override
    public String getName() {
        return "Small Mountains";
    }
}

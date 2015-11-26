package cn.nukkit.level.generator.normal.biome;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SwampBiome extends GrassyBiome {

    public SwampBiome() {
        super();

        this.setElevation(62, 63);

        this.temperature = 0.8;
        this.rainfall = 0.9;
    }

    @Override
    public String getName() {
        return "Swamp";
    }

    @Override
    public int getColor() {
        return 0x6a7039;
    }
}

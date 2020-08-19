package cn.nukkit.level.biome.impl.plains;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.biome.type.GrassyBiome;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class PlainsBiome extends GrassyBiome {

    public PlainsBiome() {
        super();

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Plains";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDry() {
        return true;
    }
}

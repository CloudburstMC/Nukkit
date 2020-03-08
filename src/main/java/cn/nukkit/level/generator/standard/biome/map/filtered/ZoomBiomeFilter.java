package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ZoomBiomeFilter extends AbstractBiomeFilter {
    public static final Identifier ID = Identifier.fromString("nukkitx:zoom");

    @JsonProperty
    protected boolean fuzzy = false;

    @Override
    public GenerationBiome get(int x, int z) {
        return null;
    }

    protected GenerationBiome selectNormal(int x, int z, GenerationBiome v0, GenerationBiome v1, GenerationBiome v2, GenerationBiome v3)   {
        if (v1 == v2 && v2 == v3) {
            return v1;
        } else if (v0 == v1 && v0 == v2) {
            return v0;
        } else if (v0 == v1 && v0 == v3) {
            return v0;
        } else if (v0 == v2 && v0 == v3) {
            return v0;
        } else if (v0 == v1 && v2 != v3) {
            return v0;
        } else if (v0 == v2 && v1 != v3) {
            return v0;
        } else if (v0 == v3 && v1 != v2) {
            return v0;
        } else if (v1 == v2 && v0 != v3) {
            return v1;
        } else if (v1 == v3 && v0 != v2) {
            return v1;
        } else if (v2 == v3 && v0 != v1) {
            return v2;
        }

        return this.selectRandom(x, z, v0, v1, v2, v3);
    }

    protected GenerationBiome selectRandom(int x, int z, GenerationBiome v0, GenerationBiome v1, GenerationBiome v2, GenerationBiome v3)   {
        switch (this.random(x, z, 0, 4))    {
            case 0:
                return v0;
            case 1:
                return v1;
            case 2:
                return v2;
            case 3:
                return v3;
            default:
                throw new IllegalStateException();
        }
    }
}

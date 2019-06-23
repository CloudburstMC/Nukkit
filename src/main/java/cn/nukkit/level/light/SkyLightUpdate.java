package cn.nukkit.level.light;

import cn.nukkit.level.ChunkManager;

/**
 * author: dktapps
 */
public class SkyLightUpdate extends LightUpdate {

    public SkyLightUpdate(ChunkManager level) {
        super(level);
    }

    public int getLight(int x, int y, int z) {
        return this.currentSection.getBlockSkyLight(x & 0x0f, y & 0x0f, z & 0x0f);
    }

    public void setLight(int x, int y, int z, int level) {
        try {
            this.currentSection.setBlockSkyLight(x & 0x0f, y & 0x0f, z & 0x0f, level);
        } catch (Exception ignore) {

        }
    }
}
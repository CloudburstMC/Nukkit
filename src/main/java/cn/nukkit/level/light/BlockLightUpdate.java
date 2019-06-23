package cn.nukkit.level.light;

import cn.nukkit.level.ChunkManager;

/**
 * author: dktapps
 */
public class BlockLightUpdate extends LightUpdate {

    public BlockLightUpdate(ChunkManager level) {
        super(level);
    }

    @Override
    public int getLight(int x, int y, int z) {
        return this.currentSection.getBlockLight(x & 0x0f, y & 0x0f, z & 0x0f);
    }

    @Override
    public void setLight(int x, int y, int z, int level) {
        try {
            this.currentSection.setBlockLight(x & 0x0f, y & 0x0f, z & 0x0f, level);
        } catch (Exception ignore) {

        }
    }
}
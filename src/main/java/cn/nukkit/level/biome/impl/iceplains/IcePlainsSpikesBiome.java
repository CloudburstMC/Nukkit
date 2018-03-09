package cn.nukkit.level.biome.impl.iceplains;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class IcePlainsSpikesBiome extends IcePlainsBiome {

    public IcePlainsSpikesBiome() {
        super();

        PopulatorIceSpikes iceSpikes = new PopulatorIceSpikes();
        this.addPopulator(iceSpikes);
    }

    @Override
    public int getSurfaceBlock(int y) {
        return SNOW_BLOCK;
    }

    public String getName() {
        return "Ice Plains Spikes";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    /**
     * @author DaPorkchop_
     * <p>
     * Please excuse this mess, but it runs way faster than the correct method
     */
    private static class PopulatorIceSpikes extends Populator {
        private static int PACKED_ICE = BlockID.PACKED_ICE << 4;

        @Override
        public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
            for (int i = 0; i < 8; i++) {
                int x = (chunkX << 4) + random.nextBoundedInt(16);
                int z = (chunkZ << 4) + random.nextBoundedInt(16);
                boolean isTall = random.nextBoundedInt(16) == 0;
                int height = 10 + random.nextBoundedInt(16) + (isTall ? random.nextBoundedInt(31) : 0);
                int startY = getHighestWorkableBlock(x, z, chunk);
                int maxY = startY + height;
                if (isTall) {
                    for (int y = startY; y < maxY; y++) {
                        //center column
                        level.setBlockFullIdAt(x, y, z, PACKED_ICE);
                        //t shape
                        level.setBlockFullIdAt(x + 1, y, z, PACKED_ICE);
                        level.setBlockFullIdAt(x - 1, y, z, PACKED_ICE);
                        level.setBlockFullIdAt(x, y, z + 1, PACKED_ICE);
                        level.setBlockFullIdAt(x, y, z - 1, PACKED_ICE);
                        //additional blocks on the side
                        if (random.nextBoolean()) {
                            level.setBlockFullIdAt(x + 1, y, z + 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockFullIdAt(x + 1, y, z - 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockFullIdAt(x - 1, y, z + 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockFullIdAt(x - 1, y, z - 1, PACKED_ICE);
                        }
                    }
                    //finish with a point
                    level.setBlockFullIdAt(x + 1, maxY, z, PACKED_ICE);
                    level.setBlockFullIdAt(x - 1, maxY, z, PACKED_ICE);
                    level.setBlockFullIdAt(x, maxY, z + 1, PACKED_ICE);
                    level.setBlockFullIdAt(x, maxY, z - 1, PACKED_ICE);
                    for (int y = maxY; y < maxY + 3; maxY++) {
                        level.setBlockFullIdAt(x, y, z, PACKED_ICE);
                    }
                } else {
                    //the maximum possible radius in blocks
                    int baseWidth = random.nextBoundedInt(1) + 4;
                    float shrinkFactor = baseWidth / height;
                    float currWidth = baseWidth;
                    for (int y = startY; y < maxY; y++) {
                        for (int xx = (int) -currWidth; xx < currWidth; xx++) {
                            for (int zz = (int) -currWidth; zz < currWidth; zz++) {
                                int currDist = (int) Math.sqrt(xx * xx + zz * zz);
                                if (random.nextBoundedInt((int) Math.abs(currWidth - currDist)) * 2 == 0) {
                                    level.setBlockFullIdAt(x + xx, y, z + zz, PACKED_ICE);
                                }
                            }
                        }
                        currWidth -= shrinkFactor;
                    }
                }
            }
        }

        public int getHighestWorkableBlock(int x, int z, FullChunk chunk) {
            return chunk.getHighestBlockAt(x & 0xF, z & 0xF) - 5;
        }
    }
}

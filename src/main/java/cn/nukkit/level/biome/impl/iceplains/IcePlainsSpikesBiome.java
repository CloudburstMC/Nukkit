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

        final IcePlainsSpikesBiome.PopulatorIceSpikes iceSpikes = new IcePlainsSpikesBiome.PopulatorIceSpikes();
        this.addPopulator(iceSpikes);
    }

    @Override
    public int getSurfaceBlock(final int y) {
        return BlockID.SNOW_BLOCK;
    }

    @Override
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

        @Override
        public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
            for (int i = 0; i < 8; i++) {
                final int x = (chunkX << 4) + random.nextBoundedInt(16);
                final int z = (chunkZ << 4) + random.nextBoundedInt(16);
                final boolean isTall = random.nextBoundedInt(16) == 0;
                final int height = 10 + random.nextBoundedInt(16) + (isTall ? random.nextBoundedInt(31) : 0);
                final int startY = this.getHighestWorkableBlock(x, z, chunk);
                final int maxY = startY + height;
                if (isTall) {
                    for (int y = startY; y < maxY; y++) {
                        //center column
                        level.setBlockAt(x, y, z, BlockID.PACKED_ICE);
                        //t shape
                        level.setBlockAt(x + 1, y, z, BlockID.PACKED_ICE);
                        level.setBlockAt(x - 1, y, z, BlockID.PACKED_ICE);
                        level.setBlockAt(x, y, z + 1, BlockID.PACKED_ICE);
                        level.setBlockAt(x, y, z - 1, BlockID.PACKED_ICE);
                        //additional blocks on the side
                        if (random.nextBoolean()) {
                            level.setBlockAt(x + 1, y, z + 1, BlockID.PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x + 1, y, z - 1, BlockID.PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x - 1, y, z + 1, BlockID.PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockAt(x - 1, y, z - 1, BlockID.PACKED_ICE);
                        }
                    }
                    //finish with a point
                    level.setBlockAt(x + 1, maxY, z, BlockID.PACKED_ICE);
                    level.setBlockAt(x - 1, maxY, z, BlockID.PACKED_ICE);
                    level.setBlockAt(x, maxY, z + 1, BlockID.PACKED_ICE);
                    level.setBlockAt(x, maxY, z - 1, BlockID.PACKED_ICE);
                    for (int y = maxY; y < maxY + 3; y++) {
                        level.setBlockAt(x, y, z, BlockID.PACKED_ICE);
                    }
                } else {
                    //the maximum possible radius in blocks
                    final int baseWidth = random.nextBoundedInt(1) + 4;
                    final float shrinkFactor = baseWidth / (float) height;
                    float currWidth = baseWidth;
                    for (int y = startY; y < maxY; y++) {
                        for (int xx = (int) -currWidth; xx < currWidth; xx++) {
                            for (int zz = (int) -currWidth; zz < currWidth; zz++) {
                                final int currDist = (int) Math.sqrt(xx * xx + zz * zz);
                                if ((int) currWidth != currDist && random.nextBoolean()) {
                                    level.setBlockAt(x + xx, y, z + zz, BlockID.PACKED_ICE);
                                }
                            }
                        }
                        currWidth -= shrinkFactor;
                    }
                }
            }
        }

        public int getHighestWorkableBlock(final int x, final int z, final FullChunk chunk) {
            return chunk.getHighestBlockAt(x & 0xF, z & 0xF) - 5;
        }

    }

}

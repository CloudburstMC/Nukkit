package cn.nukkit.level.biome.impl.iceplains;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BedrockRandom;

import static cn.nukkit.block.BlockIds.PACKED_ICE;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class IcePlainsSpikesBiome extends IcePlainsBiome {
    private static final Block SNOW_BLOCK = Block.get(BlockIds.SNOW);

    public IcePlainsSpikesBiome() {
        super();

        PopulatorIceSpikes iceSpikes = new PopulatorIceSpikes();
        this.addPopulator(iceSpikes);
    }

    @Override
    public Block getSurface(int x, int y, int z) {
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

        @Override
        public void populate(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk) {
            for (int i = 0; i < 8; i++) {
                int x = (chunkX << 4) + random.nextInt(16);
                int z = (chunkZ << 4) + random.nextInt(16);
                boolean isTall = random.nextInt(16) == 0;
                int height = 10 + random.nextInt(16) + (isTall ? random.nextInt(31) : 0);
                int startY = getHighestWorkableBlock(x, z, chunk);
                int maxY = startY + height;
                if (isTall) {
                    for (int y = startY; y < maxY; y++) {
                        //center column
                        level.setBlockId(x, y, z, PACKED_ICE);
                        //t shape
                        level.setBlockId(x + 1, y, z, PACKED_ICE);
                        level.setBlockId(x - 1, y, z, PACKED_ICE);
                        level.setBlockId(x, y, z + 1, PACKED_ICE);
                        level.setBlockId(x, y, z - 1, PACKED_ICE);
                        //additional blocks on the side
                        if (random.nextBoolean()) {
                            level.setBlockId(x + 1, y, z + 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockId(x + 1, y, z - 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockId(x - 1, y, z + 1, PACKED_ICE);
                        }
                        if (random.nextBoolean()) {
                            level.setBlockId(x - 1, y, z - 1, PACKED_ICE);
                        }
                    }
                    //finish with a point
                    level.setBlockId(x + 1, maxY, z, PACKED_ICE);
                    level.setBlockId(x - 1, maxY, z, PACKED_ICE);
                    level.setBlockId(x, maxY, z + 1, PACKED_ICE);
                    level.setBlockId(x, maxY, z - 1, PACKED_ICE);
                    for (int y = maxY; y < maxY + 3; maxY++) {
                        level.setBlockId(x, y, z, PACKED_ICE);
                    }
                } else {
                    //the maximum possible radius in blocks
                    int baseWidth = random.nextInt(1) + 4;
                    float shrinkFactor = baseWidth / (float) height;
                    float currWidth = baseWidth;
                    for (int y = startY; y < maxY; y++) {
                        for (int xx = (int) -currWidth; xx < currWidth; xx++) {
                            for (int zz = (int) -currWidth; zz < currWidth; zz++) {
                                int currDist = (int) Math.sqrt(xx * xx + zz * zz);
                                if ((int) currWidth != currDist && random.nextBoolean()) {
                                    level.setBlockId(x + xx, y, z + zz, PACKED_ICE);
                                }
                            }
                        }
                        currWidth -= shrinkFactor;
                    }
                }
            }
        }

        public int getHighestWorkableBlock(int x, int z, IChunk chunk) {
            return chunk.getHighestBlock(x & 0xF, z & 0xF) - 5;
        }
    }
}

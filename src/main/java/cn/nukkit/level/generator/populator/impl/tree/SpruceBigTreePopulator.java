package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.object.tree.ObjectBigSpruceTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;


public class SpruceBigTreePopulator extends Populator {
    private int randomAmount;
    private int baseAmount;

    private final int type;

    public SpruceBigTreePopulator() {
        this(BlockSapling.SPRUCE);
    }

    private SpruceBigTreePopulator(int type) {
        this.type = type;
    }

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk) {
        int amount = random.nextInt(this.randomAmount + 1) + this.baseAmount;
        Vector3f v = new Vector3f();

        for (int i = 0; i < amount; ++i) {
            int x = random.nextInt(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextInt(chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(level, x, z);
            if (y == -1) {
                continue;
            }
            new ObjectBigSpruceTree(3 / 4f, 4).placeObject(level, (int) (v.x = x), (int) (v.y = y), (int) (v.z = z), random);
        }
    }

    private int getHighestWorkableBlock(ChunkManager level, int x, int z) {
        int y;
        for (y = 255; y > 0; --y) {
            Identifier b = level.getBlockIdAt(x, y, z);
            if (b == DIRT || b == GRASS) {
                break;
            } else if (b != AIR && b != SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}

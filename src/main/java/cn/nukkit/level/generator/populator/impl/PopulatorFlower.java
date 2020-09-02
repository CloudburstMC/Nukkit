package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47, Niall Lindsay (Niall7459), Nukkit Project
 */
public class PopulatorFlower extends PopulatorSurfaceBlock {
    private final List<int[]> flowerTypes = new ArrayList<>();

    public void addType(int a, int b) {
        int[] c = new int[2];
        c[0] = a;
        c[1] = b;
        this.flowerTypes.add(c);
    }

    public List<int[]> getTypes() {
        return this.flowerTypes;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, FullChunk chunk, NukkitRandom random) {
        if (flowerTypes.size() != 0) {
            int[] type = flowerTypes.get(ThreadLocalRandom.current().nextInt(flowerTypes.size()));
            chunk.setFullBlockId(x, y, z, (type[0] << Block.DATA_BITS) | type[1]);
            if (type[0] == DOUBLE_PLANT) {
                chunk.setFullBlockId(x, y + 1, z, (type[0] << Block.DATA_BITS) | (8 | type[1]));
            }
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return 0;
    }
}

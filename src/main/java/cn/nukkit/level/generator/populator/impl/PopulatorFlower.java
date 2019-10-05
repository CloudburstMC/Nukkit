package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47, Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
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
    protected void placeBlock(int x, int y, int z, int id, Chunk chunk, NukkitRandom random) {
        if (flowerTypes.size() != 0) {
            int[] type = flowerTypes.get(ThreadLocalRandom.current().nextInt(flowerTypes.size()));
            chunk.setFullBlock(x, y, z, (type[0] << 4) | type[1]);
            if (type[0] == DOUBLE_PLANT) {
                chunk.setFullBlock(x, y + 1, z, (type[0] << 4) | (8 | type[1]));
            }
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, Chunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, Chunk chunk) {
        return 0;
    }
}

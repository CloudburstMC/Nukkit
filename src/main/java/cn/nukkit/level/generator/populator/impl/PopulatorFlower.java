package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
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

    public void addType(final int a, final int b) {
        final int[] c = new int[2];
        c[0] = a;
        c[1] = b;
        this.flowerTypes.add(c);
    }

    public List<int[]> getTypes() {
        return this.flowerTypes;
    }

    @Override
    protected boolean canStay(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(final int x, final int z, final NukkitRandom random, final FullChunk chunk) {
        return 0;
    }

    @Override
    protected void placeBlock(final int x, final int y, final int z, final int id, final FullChunk chunk, final NukkitRandom random) {
        if (this.flowerTypes.size() != 0) {
            final int[] type = this.flowerTypes.get(ThreadLocalRandom.current().nextInt(this.flowerTypes.size()));
            chunk.setFullBlockId(x, y, z, type[0] << 4 | type[1]);
            if (type[0] == BlockID.DOUBLE_PLANT) {
                chunk.setFullBlockId(x, y + 1, z, type[0] << 4 | 8 | type[1]);
            }
        }
    }

}

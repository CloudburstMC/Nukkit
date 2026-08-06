package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

public class ObjectCherryTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.CHERRY_LOG:
            case BlockID.CHERRY_LEAVES:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.CHERRY_LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.CHERRY_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int freeTreeHeight = 7 + random.nextRange(0, 1);

        level.setBlockAt(x, y - 1, z, Block.DIRT);

        int branchStart1 = Math.max(0,
                freeTreeHeight - 1 + random.nextRange(-4, -3));

        int branchStart2 = Math.max(0,
                freeTreeHeight - 1 + random.nextRange(-1, 0));

        if (branchStart2 >= branchStart1) {
            branchStart2++;
        }

        int branchCount = random.nextRange(1, 3);

        boolean threeBranches = branchCount == 3;
        boolean twoBranches = branchCount >= 2;

        int trunkTop;

        if (threeBranches) {
            trunkTop = freeTreeHeight;
        } else if (twoBranches) {
            trunkTop = Math.max(branchStart1, branchStart2) + 1;
        } else {
            trunkTop = branchStart1 + 1;
        }

        for (int i = 0; i < trunkTop; i++) {
            level.setBlockAt(x, y + i, z, Block.CHERRY_LOG, 0);
        }

        if (threeBranches) {
            createLeaves(level, x, y + trunkTop, z, random);
        }

        BlockFace dir = BlockFace.Plane.HORIZONTAL.random(random);

        generateBranch(
                level,
                x,
                y,
                z,
                freeTreeHeight,
                branchStart1,
                dir,
                branchStart1 < trunkTop - 1,
                random
        );

        if (twoBranches) {
            generateBranch(
                    level,
                    x,
                    y,
                    z,
                    freeTreeHeight,
                    branchStart2,
                    dir.getOpposite(),
                    branchStart2 < trunkTop - 1,
                    random
            );
        }
    }

    private static void createLeaves(
            ChunkManager level,
            int x,
            int y,
            int z,
            NukkitRandom random
    ) {
        int radius = 3;

        placeLeavesRow(level, x, y, z, radius - 2, 2, random);
        placeLeavesRow(level, x, y, z, radius - 1, 1, random);

        placeLeavesRow(level, x, y, z, radius, 0, random);

        placeLeavesRowWithHangingLeavesBelow(
                level,
                x,
                y,
                z,
                radius,
                -1,
                0.25f,
                0.5f,
                random
        );

        placeLeavesRowWithHangingLeavesBelow(
                level,
                x,
                y,
                z,
                radius - 1,
                -2,
                0.16666667f,
                0.33333334f,
                random
        );
    }

    private static void placeLeavesRow(
            ChunkManager level,
            int centerX,
            int centerY,
            int centerZ,
            int range,
            int localY,
            NukkitRandom random
    ) {
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {

                if (shouldSkipLocationSigned(random, dx, localY, dz, range)) {
                    continue;
                }

                int x = centerX + dx;
                int y = centerY + localY;
                int z = centerZ + dz;

                if (level.getBlockIdAt(x, y, z) == Block.AIR) {
                    level.setBlockAt(x, y, z, Block.CHERRY_LEAVES);
                }
            }
        }
    }

    private static boolean shouldSkipLocationSigned(
            NukkitRandom random,
            int localX,
            int localY,
            int localZ,
            int range
    ) {
        int x = Math.min(
                Math.abs(localX),
                Math.abs(localX)
        );

        int z = Math.min(
                Math.abs(localZ),
                Math.abs(localZ)
        );

        return shouldSkipLocation(random, x, localY, z, range);
    }

    private static boolean shouldSkipLocation(
            NukkitRandom random,
            int localX,
            int localY,
            int localZ,
            int range
    ) {
        if (localY == -1
                && (localX == range || localZ == range)
                && random.nextFloat() < 0.25f) {
            return true;
        } else {
            boolean corner = localX == range && localZ == range;
            boolean wide = range > 2;

            return wide
                    ? corner
                      || (localX + localZ > range * 2 - 2
                          && random.nextFloat() < 0.33333334f)
                    : corner
                      && random.nextFloat() < 0.33333334f;
        }
    }

    private static void placeLeavesRowWithHangingLeavesBelow(
            ChunkManager level,
            int centerX,
            int centerY,
            int centerZ,
            int range,
            int localY,
            float hangingLeavesChance,
            float hangingLeavesExtensionChance,
            NukkitRandom random
    ) {
        placeLeavesRow(
                level,
                centerX,
                centerY,
                centerZ,
                range,
                localY,
                random
        );

        int logX = centerX;
        int logY = centerY;
        int logZ = centerZ;

        for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {

            BlockFace clockwise = rotateClockwise(direction);

            int clockwiseOffset =
                    range;

            int startX = centerX;
            int startY = centerY + localY - 1;
            int startZ = centerZ;

            int x = startX
                    + clockwise.getXOffset() * clockwiseOffset
                    + direction.getXOffset() * (-range);

            int y = startY;

            int z = startZ
                    + clockwise.getZOffset() * clockwiseOffset
                    + direction.getZOffset() * (-range);


            for (int i = -range; i < range; i++) {
                boolean isSet =
                        level.getBlockIdAt(x, y + 1, z)
                                == Block.CHERRY_LEAVES;

                if (isSet) {
                    if (tryPlaceExtension(
                            level,
                            x,
                            y,
                            z,
                            logX,
                            logY,
                            logZ,
                            hangingLeavesChance,
                            random
                    )) {

                        tryPlaceExtension(
                                level,
                                x,
                                y - 1,
                                z,
                                logX,
                                logY,
                                logZ,
                                hangingLeavesExtensionChance,
                                random
                        );
                    }
                }

                x += direction.getXOffset();
                z += direction.getZOffset();
            }
        }
    }

    private static boolean tryPlaceExtension(
            ChunkManager level,
            int x,
            int y,
            int z,
            int logX,
            int logY,
            int logZ,
            float chance,
            NukkitRandom random
    ) {
        int distance =
                Math.abs(x - logX)
                        + Math.abs(y - logY)
                        + Math.abs(z - logZ);

        if (distance >= 7) {
            return false;
        }

        if (random.nextFloat() > chance) {
            return false;
        }

        return tryPlaceLeaf(level, x, y, z);
    }

    private static boolean tryPlaceLeaf(
            ChunkManager level,
            int x,
            int y,
            int z
    ) {
        if (level.getBlockIdAt(x, y, z) != Block.AIR) {
            return false;
        }

        level.setBlockAt(
                x,
                y,
                z,
                Block.CHERRY_LEAVES
        );

        return true;
    }

    private static BlockFace rotateClockwise(BlockFace face) {
        switch (face) {
            case NORTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.NORTH;
            default:
                return face;
        }
    }

    private static void generateBranch(
            ChunkManager level,
            int x,
            int y,
            int z,
            int freeTreeHeight,
            int branchStartOffsetFromTop,
            BlockFace direction,
            boolean doubleBranch,
            NukkitRandom random
    ) {
        int cx = x;
        int cy = y + branchStartOffsetFromTop;
        int cz = z;

        int endY = y + freeTreeHeight - 1 + random.nextRange(2, 4);

        boolean extraLength = doubleBranch || endY < cy;

        int horizontalLength = random.nextRange(2, 4);

        if (extraLength) {
            horizontalLength++;
        }

        int endX = x + direction.getXOffset() * horizontalLength;
        int endZ = z + direction.getZOffset() * horizontalLength;

        int initialBlocks = extraLength ? 2 : 1;

        int axisMeta = 0;
        switch (direction.getAxis()) {
            case X:
                axisMeta = 1;
                break;
            case Z:
                axisMeta = 2;
                break;
        }

        for (int i = 0; i < initialBlocks; i++) {
            cx += direction.getXOffset();
            cz += direction.getZOffset();

            level.setBlockAt(
                    cx,
                    cy,
                    cz,
                    Block.CHERRY_LOG,
                    axisMeta
            );
        }

        int yDirection = endY > cy ? 1 : -1;

        while (true) {
            int distance =
                    Math.abs(endX - cx)
                            + Math.abs(endY - cy)
                            + Math.abs(endZ - cz);


            if (distance == 0) {
                createLeaves(
                        level,
                        endX,
                        endY + 1,
                        endZ,
                        random
                );

                return;
            }

            float verticalChance =
                    (float) Math.abs(endY - cy) / distance;

            boolean yChange = false;
            if (random.nextFloat() < verticalChance) {

                cy += yDirection;
                yChange = true;

            } else {

                cx += direction.getXOffset();
                cz += direction.getZOffset();
            }

            level.setBlockAt(
                    cx,
                    cy,
                    cz,
                    Block.CHERRY_LOG,
                    yChange ? 0 : axisMeta
            );
        }
    }
}

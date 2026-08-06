package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

public class ObjectMangroveTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.MANGROVE_LOG:
            case BlockID.MANGROVE_PROPAGULE:
            case BlockID.MANGROVE_LEAVES:
            case BlockID.MANGROVE_ROOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.MANGROVE_LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.MANGROVE_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int freeTreeHeight = 2 + random.nextRange(0, 1) + random.nextRange(0, 4);

        generateRoots(level, x, y, z, random);

        for (int i = 0; i < freeTreeHeight; i++) {
            level.setBlockAt(
                    x,
                    y + i,
                    z,
                    Block.MANGROVE_LOG,
                    0
            );
        }

        int branchCount = random.nextRange(1, 4);

        for (int i = 0; i < branchCount; i++) {
            BlockFace direction = BlockFace.Plane.HORIZONTAL.random(random);

            generateBranch(
                    level,
                    x,
                    y,
                    z,
                    freeTreeHeight,
                    direction,
                    random
            );
        }

        createLeaves(
                level,
                x,
                y + freeTreeHeight,
                z,
                random
        );
    }

    private static void generateBranch(
            ChunkManager level,
            int x,
            int y,
            int z,
            int freeTreeHeight,
            BlockFace direction,
            NukkitRandom random
    ) {

        int start = random.nextRange(1,4);

        int cx = x;
        int cy = y + start;
        int cz = z;


        int length = random.nextRange(1,4);

        int axisMeta = 0;
        switch (direction.getAxis()) {
            case X:
                axisMeta = 1;
                break;
            case Z:
                axisMeta = 2;
                break;
        }

        for (int i = 0; i < length; i++) {
            cx += direction.getXOffset();
            cz += direction.getZOffset();

            cy++;

            level.setBlockAt(
                    cx,
                    cy,
                    cz,
                    Block.MANGROVE_LOG,
                    axisMeta
            );
        }


        createLeaves(
                level,
                cx,
                cy + 1,
                cz,
                random
        );
    }

    private static void createLeaves(
            ChunkManager level,
            int x,
            int y,
            int z,
            NukkitRandom random
    ) {
        int foliageRadius = 3;
        int foliageHeight = 2;

        for (int i = 0; i < 70; i++) {

            int dx =
                    random.nextRange(0, foliageRadius - 1)
                            - random.nextRange(0, foliageRadius - 1);

            int dy =
                    random.nextRange(0, foliageHeight - 1)
                            - random.nextRange(0, foliageHeight - 1);

            int dz =
                    random.nextRange(0, foliageRadius - 1)
                            - random.nextRange(0, foliageRadius - 1);


            int lx = x + dx;
            int ly = y + dy;
            int lz = z + dz;

            if (level.getBlockIdAt(lx, ly, lz) == Block.AIR) {
                level.setBlockAt(
                        lx,
                        ly,
                        lz,
                        Block.MANGROVE_LEAVES
                );
            }
        }
    }

    private static void generateRoots(
            ChunkManager level,
            int trunkX,
            int trunkY,
            int trunkZ,
            NukkitRandom random
    ) {
        List<int[]> roots = new ArrayList<>();

        roots.add(new int[]{
                trunkX,
                trunkY - 1,
                trunkZ
        });

        for (BlockFace direction : BlockFace.Plane.HORIZONTAL) {
            int startX = trunkX + direction.getXOffset();
            int startY = trunkY;
            int startZ = trunkZ + direction.getZOffset();

            List<int[]> branchRoots = new ArrayList<>();

            boolean success = simulateRoots(
                    level,
                    random,
                    startX,
                    startY,
                    startZ,
                    direction,
                    trunkX,
                    trunkY,
                    trunkZ,
                    branchRoots,
                    0
            );

            if (!success) {
                continue;
            }

            roots.addAll(branchRoots);

            roots.add(new int[]{
                    startX,
                    startY,
                    startZ
            });
        }

        for (int[] root : roots) {
            placeRoot(
                    level,
                    random,
                    root[0],
                    root[1],
                    root[2]
            );
        }
    }

    private static boolean simulateRoots(
            ChunkManager level,
            NukkitRandom random,
            int x,
            int y,
            int z,
            BlockFace direction,
            int trunkX,
            int trunkY,
            int trunkZ,
            List<int[]> roots,
            int length
    ) {
        final int maxRootLength = 15;

        if (length == maxRootLength
                || roots.size() > maxRootLength) {
            return false;
        }

        List<int[]> candidates = potentialRootPositions(
                x,
                y,
                z,
                direction,
                random,
                trunkX,
                trunkY,
                trunkZ
        );

        for (int[] candidate : candidates) {
            int cx = candidate[0];
            int cy = candidate[1];
            int cz = candidate[2];


            if (canPlaceRoot(level, cx, cy, cz)) {
                roots.add(new int[]{
                        cx,
                        cy,
                        cz
                });


                if (!simulateRoots(
                        level,
                        random,
                        cx,
                        cy,
                        cz,
                        direction,
                        trunkX,
                        trunkY,
                        trunkZ,
                        roots,
                        length + 1
                )) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<int[]> potentialRootPositions(
            int x,
            int y,
            int z,
            BlockFace direction,
            NukkitRandom random,
            int trunkX,
            int trunkY,
            int trunkZ
    ) {
        List<int[]> result = new ArrayList<>();

        int belowX = x;
        int belowY = y - 1;
        int belowZ = z;

        int forwardX = x + direction.getXOffset();
        int forwardY = y;
        int forwardZ = z + direction.getZOffset();


        int distance =
                Math.abs(x - trunkX)
                        + Math.abs(y - trunkY)
                        + Math.abs(z - trunkZ);


        final int maxRootWidth = 8;
        final float randomSkewChance = 0.2f;

        if (distance > maxRootWidth - 3
                && distance <= maxRootWidth) {

            if (random.nextFloat() < randomSkewChance) {

                result.add(new int[]{
                        belowX,
                        belowY,
                        belowZ
                });

                result.add(new int[]{
                        forwardX,
                        forwardY - 1,
                        forwardZ
                });

            } else {

                result.add(new int[]{
                        belowX,
                        belowY,
                        belowZ
                });
            }

        } else if (distance > maxRootWidth) {

            result.add(new int[]{
                    belowX,
                    belowY,
                    belowZ
            });

        } else if (random.nextFloat() < randomSkewChance) {

            result.add(new int[]{
                    belowX,
                    belowY,
                    belowZ
            });

        } else {

            if (random.nextBoolean()) {

                result.add(new int[]{
                        forwardX,
                        forwardY,
                        forwardZ
                });

            } else {

                result.add(new int[]{
                        belowX,
                        belowY,
                        belowZ
                });
            }
        }

        return result;
    }

    private static boolean canPlaceRoot(
            ChunkManager level,
            int x,
            int y,
            int z
    ) {
        int id = level.getBlockIdAt(x, y, z);

        return id == Block.AIR
                || id == Block.WATER
                || id == Block.MUD
                || id == Block.MUDDY_MANGROVE_ROOTS;
    }

    private static void placeRoot(
            ChunkManager level,
            NukkitRandom random,
            int x,
            int y,
            int z
    ) {
        int existing = level.getBlockIdAt(x, y, z);

        if (!canPlaceRoot(level, x, y, z)) {
            return;
        }

        if (existing == Block.MUD) {
            level.setBlockAt(
                    x,
                    y,
                    z,
                    Block.MUDDY_MANGROVE_ROOTS
            );
        } else {
            level.setBlockAt(
                    x,
                    y,
                    z,
                    Block.MANGROVE_ROOTS
            );
        }

        if (random.nextFloat() < 0.5f) {

            if (level.getBlockIdAt(
                    x,
                    y + 1,
                    z
            ) == Block.AIR) {

                level.setBlockAt(
                        x,
                        y + 1,
                        z,
                        Block.MOSS_CARPET
                );
            }
        }
    }
}

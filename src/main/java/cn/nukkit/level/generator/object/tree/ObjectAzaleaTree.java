package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

public class ObjectAzaleaTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.LOG:
            case BlockID.AZALEA:
            case BlockID.FLOWERING_AZALEA:
            case BlockID.AZALEA_LEAVES:
            case BlockID.AZALEA_LEAVES_FLOWERED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getLeafBlock() {
        return BlockID.AZALEA_LEAVES;
    }

    @Override
    public void placeObject(
            ChunkManager level,
            int x,
            int y,
            int z,
            NukkitRandom random
    ) {
        level.setBlockAt(
                x,
                y - 1,
                z,
                Block.ROOTED_DIRT
        );

        int freeTreeHeight =
                4 + random.nextRange(0, 2);

        BlockFace direction =
                BlockFace.Plane.HORIZONTAL.random(random);

        int trunkTop = freeTreeHeight - 1;

        int cx = x;
        int cy = y;
        int cz = z;

        List<int[]> foliageAttachments = new ArrayList<>();

        for (int i = 0; i <= trunkTop; i++) {
            if (i + 1 >= trunkTop + random.nextRange(0, 1)) {

                cx += direction.getXOffset();
                cz += direction.getZOffset();
            }

            if (overridable(level.getBlockIdAt(cx, cy, cz))) {
                level.setBlockAt(
                        cx,
                        cy,
                        cz,
                        Block.LOG,
                        0
                );
            }

            if (i >= 3) {

                foliageAttachments.add(new int[]{
                        cx,
                        cy,
                        cz
                });
            }


            cy++;
        }

        int bendLength = random.nextRange(1, 2);

        for (int i = 0; i <= bendLength; i++) {

            int axisMeta = 0;
            switch (direction.getAxis()) {
                case X:
                    axisMeta = 4;
                    break;
                case Z:
                    axisMeta = 8;
                    break;
            }

            if (level.getBlockIdAt(cx, cy, cz) == Block.AIR) {
                level.setBlockAt(
                        cx,
                        cy,
                        cz,
                        Block.LOG,
                        axisMeta
                );
            }

            foliageAttachments.add(new int[]{
                    cx,
                    cy,
                    cz
            });

            cx += direction.getXOffset();
            cz += direction.getZOffset();
        }

        for (int[] attachment : foliageAttachments) {
            createAzaleaLeaves(
                    level,
                    attachment[0],
                    attachment[1],
                    attachment[2],
                    random
            );
        }
    }

    private static void createAzaleaLeaves(
            ChunkManager level,
            int x,
            int y,
            int z,
            NukkitRandom random
    ) {
        int radius = 3;
        int height = 2;

        for (int i = 0; i < 50; i++) {

            int dx =
                    random.nextRange(0, radius - 1)
                            - random.nextRange(0, radius - 1);

            int dy =
                    random.nextRange(0, height - 1)
                            - random.nextRange(0, height - 1);

            int dz =
                    random.nextRange(0, radius - 1)
                            - random.nextRange(0, radius - 1);


            int px = x + dx;
            int py = y + dy;
            int pz = z + dz;

            if (level.getBlockIdAt(px, py, pz) == Block.AIR) {

                if (random.nextRange(1, 4) == 1) {
                    level.setBlockAt(
                            px,
                            py,
                            pz,
                            Block.AZALEA_LEAVES_FLOWERED
                    );
                } else {
                    level.setBlockAt(
                            px,
                            py,
                            pz,
                            Block.AZALEA_LEAVES
                    );
                }
            }
        }
    }
}

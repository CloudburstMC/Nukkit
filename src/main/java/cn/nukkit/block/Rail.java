package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public class Rail extends Flowable {

    public Rail() {
        this(0);
    }

    public Rail(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Rail";
    }

    @Override
    public int getId() {
        return RAIL;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.RAIL, 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(Vector3.SIDE_DOWN);
        if (down == null) return false;
        if (down.isTransparent()) return false;
        int[][] arrayXZ = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        int[] arrayY = new int[]{0, 1, -1};
        List<Vector3> connected = new ArrayList<>();
        for (int[] xz : arrayXZ) {
            int x = xz[0];
            int z = xz[1];
            for (int y : arrayY) {
                Vector3 v3 = new Vector3(x, y, z).add(this);
                Block v3block = this.getLevel().getBlock(v3);
                if (v3block == null) continue;
                if (!isRailBlock(v3block.getId()) || !isValidRailMeta(v3block.getDamage())) continue;
                if (!(v3block instanceof Rail)) continue;
                this.connectRail(v3block);
                connected.add(v3block);
            }
            if (connected.size() >= 2) break;
        }

        if (connected.size() == 1) {
            Vector3 v3 = connected.get(0).subtract(this);
            this.meta = (v3.y != 1) ? (v3.x == 0 ? 0 : 1) : (int) (v3.z == 0 ? (v3.x / -2) + 2.5 : (v3.z / 2) + 4.5);
        } else if (connected.size() == 2) {
            Vector3[] subtract = new Vector3[2];
            for (int i = 0; i < connected.size(); i++) {
                subtract[i] = connected.get(i).subtract(this);
            }
            if (Math.abs(subtract[0].x) == Math.abs(subtract[1].z) && Math.abs(subtract[1].x) == Math.abs(subtract[0].z)) {
                Vector3 v3 = connected.get(0).subtract(this).add(connected.get(1).subtract(this));
                this.meta = v3.x == 1 ? (v3.z == 1 ? 6 : 9) : (v3.z == 1 ? 7 : 8);
            } else if (subtract[0].y == 1 || subtract[1].y == 1) {
                Vector3 v3 = subtract[0].y == 1 ? subtract[0] : subtract[1];
                this.meta = v3.x == 0 ? (v3.z == -1 ? 4 : 5) : (v3.x == 1 ? 2 : 3);
            } else {
                this.meta = subtract[0].x == 0 ? 0 : 1;
            }
        }
        this.getLevel().setBlock(this, Block.get(this.getId(), this.getDamage()), true, true);
        return true;
    }

    /************ Rail Connecting Part ***********/
    /****
     * todo: too complex, need to simplify
     ****/

    protected Vector3[] canConnectRail(Block block) {
        if (!(block instanceof Rail)) return null;
        if (this.distanceSquared(block) > 2) return null;
        Vector3[] result = checkRail(this);
        if (result.length == 2) return null;
        return result;
    }

    protected void connectRail(Block rail) {
        Vector3[] connected = canConnectRail(rail);
        if (connected == null) return;
        if (connected.length == 1) {
            Vector3 v3 = connected[0].subtract(this);
            this.meta = (v3.y != 1) ? (v3.x == 0 ? 0 : 1) : (int) (v3.z == 0 ? (v3.x / -2) + 2.5 : (v3.z / 2) + 4.5);
        } else if (connected.length == 2) {
            Vector3[] subtract = new Vector3[2];
            for (int i = 0; i < connected.length; i++) {
                subtract[i] = connected[i].subtract(this);
            }
            if (Math.abs(subtract[0].x) == Math.abs(subtract[1].z) && Math.abs(subtract[1].x) == Math.abs(subtract[0].z)) {
                Vector3 v3 = connected[0].subtract(this).add(connected[1].subtract(this));
                this.meta = v3.x == 1 ? (v3.z == 1 ? 6 : 9) : (v3.z == 1 ? 7 : 8);
            } else if (subtract[0].y == 1 || subtract[1].y == 1) {
                Vector3 v3 = subtract[0].y == 1 ? subtract[0] : subtract[1];
                this.meta = v3.x == 0 ? (v3.z == -1 ? 4 : 5) : (v3.x == 1 ? 2 : 3);
            } else {
                this.meta = subtract[0].x == 0 ? 0 : 1;
            }
        }
        this.getLevel().setBlock(this, Block.get(this.getId(), this.getDamage()), true, true);
    }

    protected static Vector3[] checkRail(Block rail) {
        if (!(rail instanceof Rail)) return null;
        int damage = rail.getDamage();
        if (damage < 0 || damage > 10) return null;
        int[][][] delta = new int[][][]{
                {{0, 1}, {0, -1}},
                {{1, 0}, {-1, 0}},
                {{1, 0}, {-1, 0}},
                {{1, 0}, {-1, 0}},
                {{0, 1}, {0, -1}},
                {{0, 1}, {0, -1}},
                {{1, 0}, {0, 1}},
                {{0, 1}, {-1, 0}},
                {{-1, 0}, {0, -1}},
                {{0, -1}, {1, 0}}
        };
        int[] deltaY = new int[]{0, 1, -1};
        int[][] blocks = delta[damage];
        List<Vector3> connected = new ArrayList<>();
        for (int y : deltaY) {
            Vector3 v3 = new Vector3(
                    rail.getFloorX() + blocks[0][0],
                    rail.getFloorY() + y,
                    rail.getFloorZ() + blocks[0][1]
            );
            int idToConnect = rail.getLevel().getBlockIdAt(v3.getFloorX(), v3.getFloorY(), v3.getFloorZ());
            int metaToConnect = rail.getLevel().getBlockDataAt(v3.getFloorX(), v3.getFloorY(), v3.getFloorZ());
            if (!isRailBlock(idToConnect) || !isValidRailMeta(metaToConnect)) continue;
            int xDiff = damage - v3.getFloorX();
            int zDiff = damage - v3.getFloorZ();
            for (int[] xz : blocks) {
                if (xz[0] != xDiff || xz[1] != zDiff) continue;
                connected.add(v3);
            }
        }
        for (int y : deltaY) {
            Vector3 v3 = new Vector3(
                    rail.getFloorX() + blocks[1][0],
                    rail.getFloorY() + y,
                    rail.getFloorZ() + blocks[1][1]
            );
            int idToConnect = rail.getLevel().getBlockIdAt(v3.getFloorX(), v3.getFloorY(), v3.getFloorZ());
            int metaToConnect = rail.getLevel().getBlockDataAt(v3.getFloorX(), v3.getFloorY(), v3.getFloorZ());
            if (!isRailBlock(idToConnect) || !isValidRailMeta(metaToConnect)) continue;
            int xDiff = damage - v3.getFloorX();
            int zDiff = damage - v3.getFloorZ();
            for (int[] xz : blocks) {
                if (xz[0] != xDiff || xz[1] != zDiff) continue;
                connected.add(v3);
            }
        }
        return connected.toArray(new Vector3[connected.size()]);
    }

    protected static boolean isRailBlock(int id) {
        switch (id) {
            case RAIL:
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }

    protected static boolean isValidRailMeta(int meta) {
        return !(meta < 0 || meta > 10);
    }

}

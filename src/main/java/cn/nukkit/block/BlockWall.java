package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWall extends BlockTransparent {
    public static final int NONE_MOSSY_WALL = 0;
    public static final int MOSSY_WALL = 1;


    public BlockWall() {
        this(0);
    }

    public BlockWall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_WALL;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public String getName() {
        if (this.meta == 0x01) {
            return "Mossy Cobblestone Wall";
        }

        return "Cobblestone Wall";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        boolean north = this.canConnect(this.getSide(Vector3.SIDE_NORTH));
        boolean south = this.canConnect(this.getSide(Vector3.SIDE_SOUTH));
        boolean west = this.canConnect(this.getSide(Vector3.SIDE_WEST));
        boolean east = this.canConnect(this.getSide(Vector3.SIDE_EAST));

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;

        if (north && south && !west && !east) {
            w = 0.3125;
            e = 0.6875;
        } else if (!north && !south && west && east) {
            n = 0.3125;
            s = 0.6875;
        }

        return new AxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    public boolean canConnect(Block block) {
        return (!(block.getId() != COBBLE_WALL && block.getId() != FENCE_GATE)) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }
}

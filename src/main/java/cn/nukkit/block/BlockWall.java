package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockWall extends BlockTransparentMeta {

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
        switch (this.getDamage()) {
            case 0:
                return "Cobblestone Wall";
            case 1:
                return "Mossy Cobblestone Wall";
            case 2:
                return "Granite Wall";
            case 3:
                return "Diorite Wall";
            case 4:
                return "Andesite Wall";
            case 5:
                return "Sandstone Wall";
            case 6:
                return "Brick Wall";
            case 7:
                return "Stone Brick Wall";
            case 8:
                return "Mossy Stone Brick Wall";
            case 9:
                return "Nether Brick Wall";
            case 10:
                return "End Stone Brick Wall";
            case 11:
                return "Prismarine Wall";
            case 12:
                return "Red Sandstone Wall";
            case 13:
                return "Red Nether Brick Wall";
        }

        return "Wall";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.getSide(BlockFace.NORTH));
        boolean south = this.canConnect(this.getSide(BlockFace.SOUTH));
        boolean west = this.canConnect(this.getSide(BlockFace.WEST));
        boolean east = this.canConnect(this.getSide(BlockFace.EAST));

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

        return new SimpleAxisAlignedBB(
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
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}

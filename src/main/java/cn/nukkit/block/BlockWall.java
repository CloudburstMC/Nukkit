package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.COBBLESTONE_WALL;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWall extends BlockTransparent {
    public static final int NONE_MOSSY_WALL = 0;
    public static final int MOSSY_WALL = 1;


    public BlockWall(Identifier id) {
        super(id);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        boolean north = this.canConnect(this.getSide(BlockFace.NORTH));
        boolean south = this.canConnect(this.getSide(BlockFace.SOUTH));
        boolean west = this.canConnect(this.getSide(BlockFace.WEST));
        boolean east = this.canConnect(this.getSide(BlockFace.EAST));

        float n = north ? 0 : 0.25f;
        float s = south ? 1 : 0.75f;
        float w = west ? 0 : 0.25f;
        float e = east ? 1 : 0.75f;

        if (north && south && !west && !east) {
            w = 0.3125f;
            e = 0.6875f;
        } else if (!north && !south && west && east) {
            n = 0.3125f;
            s = 0.6875f;
        }

        return new SimpleAxisAlignedBB(
                this.getX() + w,
                this.getY(),
                this.getZ() + n,
                this.getX() + e,
                this.getY() + 1.5f,
                this.getZ() + s
        );
    }

    public boolean canConnect(Block block) {
        return (!(block.getId() != COBBLESTONE_WALL && block instanceof BlockFence)) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}

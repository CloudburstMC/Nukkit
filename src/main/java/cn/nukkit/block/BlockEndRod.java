package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparent {

    public BlockEndRod() {
        this(0);
    }

    public BlockEndRod(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "End Rod";
    }

    @Override
    public int getId() {
        return END_ROD;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x + 0.4,
                this.y,
                this.z + 0.4,
                this.x + 0.6,
                this.y + 1,
                this.z + 0.6
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {0, 1, 3, 2, 5, 4};
        this.meta = faces[player != null ? face.getIndex() : 0];
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

}

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockStairs extends BlockTransparent {

    protected BlockStairs(int meta) {
        super(meta);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if ((this.getDamage() & 0x04) > 0) {
            return new AxisAlignedBB(
                    this.x,
                    this.y + 0.5,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else {
            return new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + 0.5,
                    this.z + 1
            );
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = new int[]{2, 1, 3, 0};
        this.meta = faces[player != null ? player.getDirection().getHorizontalIndex() : 0];
        if ((fy > 0.5 && face != BlockFace.UP) || face == BlockFace.DOWN) {
            this.meta |= 0x04; //Upside-down stairs
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        Item item = super.toItem();
        item.setDamage(0);
        return item;
    }
}

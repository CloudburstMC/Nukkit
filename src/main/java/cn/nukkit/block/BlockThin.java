package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.LevelException;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockThin extends BlockTransparent implements ConnectionBlock {

    protected int damage;

    protected BlockThin() {
    }

    protected BlockThin(int damage) {
        this.damage = damage;
    }

    @Override
    public int getFullId() {
        return (this.getId() << DATA_BITS) | this.getDamage();
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    @Override
    public void setDamage(int meta) {
        this.damage = meta;
    }

    @Override
    public int getConnectionShift() {
        return 0;
    }

    @Override
    public int getConnectionTypeCount() {
        return 1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.updateConnections();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.updateConnections()) {
                this.level.setBlock(this, this, true, false);
            }
            return type;
        }
        return 0;
    }

    @Override
    public Item toItem() {
        int type = this.getTypeDamage();
        return new ItemBlock(Block.get(this.getId(), type), type, 1);
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        double f = 0.4375;
        double f1 = 0.5625;
        double f2 = 0.4375;
        double f3 = 0.5625;
        try {
            boolean flag = this.canConnect(this.north(), BlockFace.NORTH);
            boolean flag1 = this.canConnect(this.south(), BlockFace.SOUTH);
            boolean flag2 = this.canConnect(this.west(), BlockFace.WEST);
            boolean flag3 = this.canConnect(this.east(), BlockFace.EAST);
            if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
                if (flag2) {
                    f = 0;
                } else if (flag3) {
                    f1 = 1;
                }
            } else {
                f = 0;
                f1 = 1;
            }
            if ((!flag || !flag1) && (flag || flag1 || flag2 || flag3)) {
                if (flag) {
                    f2 = 0;
                } else if (flag1) {
                    f3 = 1;
                }
            } else {
                f2 = 0;
                f3 = 1;
            }
        } catch (LevelException ignore) {}
        return new SimpleAxisAlignedBB(
                this.x + f,
                this.y,
                this.z + f2,
                this.x + f1,
                this.y + 1,
                this.z + f3
        );
    }

    @Override
    public boolean canConnect(Block block) {
        return block instanceof BlockThin
                || block instanceof BlockGlass || block instanceof BlockGlassStained || block instanceof BlockGlassTinted
                || (!(block instanceof BlockStairs) && block.isSolid() && !block.isTransparent());
    }

    @Override
    public boolean canConnect(Block block, BlockFace face) {
        return this.canConnect(block) || ConnectionBlock.connectsToStairs(block, face);
    }
}

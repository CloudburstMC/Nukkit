package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.LevelException;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockThin extends BlockTransparent {

    protected BlockThin() {
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        double f = 0.4375;
        double f1 = 0.5625;
        double f2 = 0.4375;
        double f3 = 0.5625;
        try {
            boolean flag = this.canConnect(this.north());
            boolean flag1 = this.canConnect(this.south());
            boolean flag2 = this.canConnect(this.west());
            boolean flag3 = this.canConnect(this.east());
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
            if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
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

    public boolean canConnect(Block block) {
        return block.isSolid() || block.getId() == this.getId() || block.getId() == GLASS_PANE || block.getId() == GLASS;
    }
}

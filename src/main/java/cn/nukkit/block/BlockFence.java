package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockFence extends BlockTransparent {

    public BlockFence(Identifier id) {
        super(id);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.north());
        boolean south = this.canConnect(this.south());
        boolean west = this.canConnect(this.west());
        boolean east = this.canConnect(this.east());
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    public abstract boolean canConnect(Block block);

    @Override
    public Item toItem() {
        return Item.get(id, this.getDamage());
    }
}

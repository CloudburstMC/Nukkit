package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
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
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case 1: //OAK
                return BlockColor.WOOD_BLOCK_COLOR;
            case 2: //SPRUCE
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case 3: //BIRCH
                return BlockColor.SAND_BLOCK_COLOR;
            case 4: //JUNGLE
                return BlockColor.DIRT_BLOCK_COLOR;
            case 5: //ACACIA
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 6: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

    @Override
    public Item toItem() {
        return Item.get(id, this.getDamage());
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}

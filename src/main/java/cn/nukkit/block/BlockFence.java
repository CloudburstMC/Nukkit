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
        float n = north ? 0 : 0.375f;
        float s = south ? 1 : 0.625f;
        float w = west ? 0 : 0.375f;
        float e = east ? 1 : 0.625f;
        return new SimpleAxisAlignedBB(
                this.getX() + w,
                this.getY(),
                this.getZ() + n,
                this.getX() + e,
                this.getY() + 1.5f,
                this.getZ() + s
        );
    }

    public abstract boolean canConnect(Block block);

    @Override
    public BlockColor getColor() {
        switch (this.getMeta() & 0x07) {
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
        return Item.get(id, this.getMeta());
    }
}

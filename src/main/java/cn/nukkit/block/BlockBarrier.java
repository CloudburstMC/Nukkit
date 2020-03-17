package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockBarrier extends BlockSolid {

    public BlockBarrier(Identifier id) {
        super(id);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public float getHardness() {
        return -1;
    }

    @Override
    public float getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

}

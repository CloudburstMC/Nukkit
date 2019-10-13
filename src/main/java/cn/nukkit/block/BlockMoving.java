package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockMoving extends Block {

    public BlockMoving() {
        this(0);
    }

    public BlockMoving(int meta) {
        super();
    }

    @Override
    public String getName() {
        return "MovingBlock";
    }

    @Override
    public int getId() {
        return BlockID.MOVING_BLOCK;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }
}

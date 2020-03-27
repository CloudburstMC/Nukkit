package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

public class BlockMoving extends BlockTransparent {

    public BlockMoving(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
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

    @Override
    public boolean isSolid() {
        return false;
    }
}

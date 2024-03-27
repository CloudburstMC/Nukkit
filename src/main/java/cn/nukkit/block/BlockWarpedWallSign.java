package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockWarpedWallSign extends BlockWallSign {

    public BlockWarpedWallSign() {
        this(0);
    }

    public BlockWarpedWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Wall Sign";
    }

    @Override
    public int getId() {
        return WARPED_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.WARPED_SIGN);
    }
}

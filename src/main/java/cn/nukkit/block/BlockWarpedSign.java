package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockWarpedSign extends BlockSignPost {

    public BlockWarpedSign() {
        this(0);
    }

    public BlockWarpedSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Sign";
    }

    @Override
    public int getId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.WARPED_SIGN);
    }

    @Override
    protected int getPostId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return WARPED_WALL_SIGN;
    }
}

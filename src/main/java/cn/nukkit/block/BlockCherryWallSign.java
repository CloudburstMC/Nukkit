package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockCherryWallSign extends BlockWallSign {

    public BlockCherryWallSign() {
        this(0);
    }

    public BlockCherryWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Wall Sign";
    }

    @Override
    public int getId() {
        return CHERRY_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CHERRY_SIGN);
    }

    @Override
    protected int getPostId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return CHERRY_WALL_SIGN;
    }
}

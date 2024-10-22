package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockBirchWallSign extends BlockWallSign {

    public BlockBirchWallSign() {
        this(0);
    }

    public BlockBirchWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Birch Wall Sign";
    }

    @Override
    public int getId() {
        return BIRCH_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.BIRCH_SIGN);
    }

    @Override
    protected int getPostId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return BIRCH_WALL_SIGN;
    }
}

package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockBambooWallSign extends BlockWallSign {

    public BlockBambooWallSign() {
        this(0);
    }

    public BlockBambooWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Wall Sign";
    }

    @Override
    public int getId() {
        return BAMBOO_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.BAMBOO_SIGN);
    }

    @Override
    protected int getPostId() {
        return BAMBOO_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return BAMBOO_WALL_SIGN;
    }
}

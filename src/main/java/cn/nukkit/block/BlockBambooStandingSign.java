package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockBambooStandingSign extends BlockSignPost {

    public BlockBambooStandingSign() {
        this(0);
    }

    public BlockBambooStandingSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Sign Post";
    }

    @Override
    public int getId() {
        return BAMBOO_STANDING_SIGN;
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

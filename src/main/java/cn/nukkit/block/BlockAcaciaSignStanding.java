package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockAcaciaSignStanding extends BlockSignPost {

    public BlockAcaciaSignStanding() {
        this(0);
    }

    public BlockAcaciaSignStanding(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Sign Post";
    }

    @Override
    public int getId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.ACACIA_SIGN);
    }

    @Override
    protected int getPostId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return ACACIA_WALL_SIGN;
    }
}

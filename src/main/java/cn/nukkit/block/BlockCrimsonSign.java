package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockCrimsonSign extends BlockSignPost {

    public BlockCrimsonSign() {
        this(0);
    }

    public BlockCrimsonSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Sign";
    }

    @Override
    public int getId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CRIMSON_SIGN);
    }

    @Override
    protected int getPostId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return CRIMSON_WALL_SIGN;
    }
}

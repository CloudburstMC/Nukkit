package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockJungleSignStanding extends BlockSignPost {

    public BlockJungleSignStanding() {
        this(0);
    }

    public BlockJungleSignStanding(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Jungle Sign Post";
    }

    @Override
    public int getId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.JUNGLE_SIGN);
    }

    @Override
    protected int getPostId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return JUNGLE_WALL_SIGN;
    }
}

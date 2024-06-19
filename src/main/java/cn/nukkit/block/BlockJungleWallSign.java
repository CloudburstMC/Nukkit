package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockJungleWallSign extends BlockWallSign {

    public BlockJungleWallSign() {
        this(0);
    }

    public BlockJungleWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Jungle Wall Sign";
    }

    @Override
    public int getId() {
        return JUNGLE_WALL_SIGN;
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

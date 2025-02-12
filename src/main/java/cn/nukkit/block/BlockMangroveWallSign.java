package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockMangroveWallSign extends BlockWallSign {

    public BlockMangroveWallSign() {
        this(0);
    }

    public BlockMangroveWallSign(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Wall Sign";
    }

    @Override
    public int getId() {
        return MANGROVE_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.MANGROVE_SIGN);
    }

    @Override
    protected int getPostId() {
        return MANGROVE_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return MANGROVE_WALL_SIGN;
    }
}

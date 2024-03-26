package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class BlockSpruceSignStanding extends BlockSignPost {

    public BlockSpruceSignStanding() {
        this(0);
    }

    public BlockSpruceSignStanding(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Sign Post";
    }

    @Override
    public int getId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SPRUCE_SIGN);
    }

    @Override
    protected int getPostId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    protected int getWallId() {
        return SPRUCE_WALL_SIGN;
    }
}

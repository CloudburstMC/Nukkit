package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpruceSign;

public class BlockSpruceWallSign extends BlockWallSign {
    public BlockSpruceWallSign() {
        this(0);
    }

    public BlockSpruceWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Spruce Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemSpruceSign();
    }
}

package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;

@PowerNukkitOnly
public class BlockDarkOakWallSign extends BlockWallSign {
    public BlockDarkOakWallSign() {
        this(0);
    }

    public BlockDarkOakWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARKOAK_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemDarkOakSign();
    }
}

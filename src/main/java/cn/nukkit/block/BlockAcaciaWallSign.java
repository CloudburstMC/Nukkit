package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;

@PowerNukkitOnly
public class BlockAcaciaWallSign extends BlockWallSign {
    public BlockAcaciaWallSign() {
        this(0);
    }

    public BlockAcaciaWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemAcaciaSign();
    }
}

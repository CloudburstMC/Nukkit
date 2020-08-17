package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;

@PowerNukkitOnly
public class BlockAcaciaSignPost extends BlockSignPost {
    public BlockAcaciaSignPost() {
    }

    public BlockAcaciaSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemAcaciaSign();
    }
}

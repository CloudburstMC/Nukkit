package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDarkOakSign;

@PowerNukkitOnly
public class BlockDarkOakSignPost extends BlockSignPost {
    @PowerNukkitOnly
    public BlockDarkOakSignPost() {
    }

    @PowerNukkitOnly
    public BlockDarkOakSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARKOAK_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return DARKOAK_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Dark Oak Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemDarkOakSign();
    }
}

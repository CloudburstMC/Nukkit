package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBirchSign;

@PowerNukkitOnly
public class BlockBirchSignPost extends BlockSignPost {
    @PowerNukkitOnly
    public BlockBirchSignPost() {
    }

    @PowerNukkitOnly
    public BlockBirchSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return BIRCH_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Birch Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemBirchSign();
    }
}

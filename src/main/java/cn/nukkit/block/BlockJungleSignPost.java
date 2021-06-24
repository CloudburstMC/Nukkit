package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemJungleSign;

@PowerNukkitOnly
public class BlockJungleSignPost extends BlockSignPost {
    @PowerNukkitOnly
    public BlockJungleSignPost() {
    }

    @PowerNukkitOnly
    public BlockJungleSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return JUNGLE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Jungle Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemJungleSign();
    }
}

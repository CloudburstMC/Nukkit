package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWarpedSign;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockWarpedSignPost extends BlockSignPost {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWarpedSignPost() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWarpedSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return WARPED_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Warped Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemWarpedSign();
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}

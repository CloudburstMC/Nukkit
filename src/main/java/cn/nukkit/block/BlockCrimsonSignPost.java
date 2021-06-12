package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockCrimsonSignPost extends BlockSignPost {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCrimsonSignPost() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCrimsonSignPost(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public int getWallId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Sign Post";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CRIMSON_SIGN);
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

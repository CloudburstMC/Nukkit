package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockCrimsonWallSign extends BlockWallSign {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCrimsonWallSign() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCrimsonWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    protected int getPostId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Wall Sign";
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

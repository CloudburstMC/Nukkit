package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrimsonSign;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockCrimsonWallSign extends BlockWallSign {
    public BlockCrimsonWallSign() {
        this(0);
    }

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
        return new ItemCrimsonSign();
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

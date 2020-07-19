package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrimsonSign;

public class BlockCrimsonSignPost extends BlockSignPost {
    public BlockCrimsonSignPost() {
    }

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

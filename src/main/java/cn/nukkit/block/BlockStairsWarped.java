package cn.nukkit.block;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStairsWarped extends BlockStairsWood {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Wood Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
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

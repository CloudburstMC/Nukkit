package cn.nukkit.block;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStairsCrimson extends BlockStairsWood {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsCrimson() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStairsCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_STAIRS;
    }

    @Override
    public String getName() {
        return "Crimson Wood Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
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

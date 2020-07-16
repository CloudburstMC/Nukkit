package cn.nukkit.block;


import cn.nukkit.utils.BlockColor;

public class BlockStairsWarped extends BlockStairsWood {

    public BlockStairsWarped() {
        this(0);
    }

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

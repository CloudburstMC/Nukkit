package cn.nukkit.block;

import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * @author Erik Miller | EinBexiii | Bex
 */
public class BlockStairsStone extends BlockStairs{

    public BlockStairsStone(Identifier id){
        super(id);
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}

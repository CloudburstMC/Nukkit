package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockIcePacked extends BlockIce {

    public BlockIcePacked(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        return 0; //not being melted
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(AIR), true); //no water
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}

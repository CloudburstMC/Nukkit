package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;

public class BlockBlueIce extends BlockIce {

    public BlockBlueIce(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2.8;
    }

    @Override
    public double getResistance() {
        return 14;
    }

    @Override
    public double getFrictionFactor() {
        return 0.989;
    }

    @Override
    public int onUpdate(int type){
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(AIR), true);
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }


    @Override
    public int getLightLevel() {
        return 4;
    }
}

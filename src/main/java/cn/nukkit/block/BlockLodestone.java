package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockLodestone extends BlockSolid {

    @Override
    public String getName() {
        return "Lodestone";
    }

    @Override
    public int getId() {
        return LODESTONE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}

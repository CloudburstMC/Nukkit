package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

public class BlockStairsPurpur extends BlockStairs {

    public BlockStairsPurpur(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}

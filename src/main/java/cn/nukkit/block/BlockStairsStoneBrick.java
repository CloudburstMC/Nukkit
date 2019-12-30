package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsStoneBrick extends BlockStairs {
    public BlockStairsStoneBrick(Identifier id) {
        super(id);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }
}

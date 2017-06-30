package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcretePowder extends BlockFallable {

    public BlockConcretePowder() {
        this(0);
    }

    public BlockConcretePowder(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Concrete Powder";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }
}

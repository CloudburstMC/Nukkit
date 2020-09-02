package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockConcrete extends BlockSolidMeta {

    public BlockConcrete() {
        this(0);
    }

    public BlockConcrete(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CONCRETE;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public double getHardness() {
        return 1.8;
    }

    @Override
    public String getName() {
        return "Concrete";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getDamage()).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}

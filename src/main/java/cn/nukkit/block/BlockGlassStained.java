package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassStained extends BlockGlass {

    private int meta;

    public BlockGlassStained() {
        this(0);
    }

    public BlockGlassStained(int meta) {
        this.meta = meta;
    }

    @Override
    public int getId() {
        return STAINED_GLASS;
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Stained Glass";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getDamage()).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }
}

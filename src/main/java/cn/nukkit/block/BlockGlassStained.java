package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassStained extends BlockGlass {

    public BlockGlassStained(Identifier id) {
        super(id);
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getDamage()).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }
}

package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassPaneStained extends BlockGlassPane {

    public BlockGlassPaneStained(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " stained glass pane";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}

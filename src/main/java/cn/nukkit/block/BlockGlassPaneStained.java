package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassPaneStained extends BlockGlassPane {

    public BlockGlassPaneStained(Identifier id) {
        super(id);
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}

package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassPaneStained extends BlockGlassPane {

    public BlockGlassPaneStained() {
        this(0);
    }

    public BlockGlassPaneStained(int meta) {
        super(meta);
    }

    @Override
    public int getConnectionShift() {
        return 4;
    }

    @Override
    public int getConnectionTypeCount() {
        return 16;
    }

    @Override
    public int getId() {
        return STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Stained Glass Pane";
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(this.getTypeDamage());
    }
}

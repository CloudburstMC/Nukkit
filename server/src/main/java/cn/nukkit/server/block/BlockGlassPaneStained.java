package cn.nukkit.server.block;

import cn.nukkit.server.util.BlockColor;
import cn.nukkit.server.util.DyeColor;

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
    public int getId() {
        return STAINED_GLASS_PANE;
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
        return DyeColor.getByWoolData(meta);
    }
}

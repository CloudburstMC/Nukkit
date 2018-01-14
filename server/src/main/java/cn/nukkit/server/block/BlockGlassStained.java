package cn.nukkit.server.block;

import cn.nukkit.server.util.BlockColor;
import cn.nukkit.server.util.DyeColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassStained extends BlockGlass {

    public BlockGlassStained() {
        this(0);
    }

    public BlockGlassStained(int meta) {
        super(meta);
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
        return DyeColor.getByWoolData(meta).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(meta);
    }
}

package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassPaneStained extends BlockGlassPane {

    public BlockGlassPaneStained() {
        // Does nothing
    }

    public BlockGlassPaneStained(int meta) {
        getMutableState().setDataStorageFromInt(meta);
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_PROPERTIES;
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
        return DyeColor.getByWoolData(getDamage());
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}

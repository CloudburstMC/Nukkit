package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.COLOR;

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
public class BlockGlassStained extends BlockGlass {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.COLOR_BLOCK_PROPERTIES;

    public BlockGlassStained() {
        // Does nothing
    }

    public BlockGlassStained(int meta) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
        return getDyeColor().getColor();
    }

    @Nonnull
    public DyeColor getDyeColor() {
        return getPropertyValue(COLOR);
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setDyeColor(@Nonnull DyeColor color) {
        setPropertyValue(COLOR, color);
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}

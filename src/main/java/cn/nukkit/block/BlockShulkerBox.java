package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;

/**
 * Created by PetteriM1
 */
public class BlockShulkerBox extends BlockUndyedShulkerBox {

    public BlockShulkerBox() {
        // Does nothing
    }

    public BlockShulkerBox(int meta) {
        getMutableState().setDataStorageFromInt(meta);
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_PROPERTIES;
    }

    @Override
    public int getId() {
        return SHULKER_BOX;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(this.getDamage());
    }
}

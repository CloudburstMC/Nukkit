package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

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

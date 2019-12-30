package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

/**
 * Created by PetteriM1
 */
public class BlockShulkerBox extends BlockUndyedShulkerBox {

    public BlockShulkerBox(Identifier id) {
        super(id);
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(this.getDamage());
    }
}

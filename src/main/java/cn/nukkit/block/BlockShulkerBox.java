package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by PetteriM1
 */
public class BlockShulkerBox extends BlockUndyedShulkerBox {

    private int meta;

    public BlockShulkerBox() {
        this(0);
    }

    public BlockShulkerBox(int meta) {
        super();
        this.meta = meta;
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

    @Override
    public int getFullId() {
        return (this.getId() << 4) + this.getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public void setDamage(int meta) {
        this.meta = meta;
    }
}

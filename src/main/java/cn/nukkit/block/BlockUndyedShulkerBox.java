package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockUndyedShulkerBox extends BlockShulkerBox {

    public BlockUndyedShulkerBox() {
        super(0);
    }

    @Override
    public int getId() {
        return UNDYED_SHULKER_BOX;
    }

    @Override
    public String getName() {
        return "Shulker Box";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public DyeColor getDyeColor() {
        return null;
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity be = this.getLevel().getBlockEntity(this);

        if (!(be instanceof InventoryHolder)) {
            return 0;
        }

        return ContainerInventory.calculateRedstone(((InventoryHolder) be).getInventory());
    }
}
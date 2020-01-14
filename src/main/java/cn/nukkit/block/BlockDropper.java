package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDropper;
import cn.nukkit.dispenser.DefaultDispenseBehavior;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;

public class BlockDropper extends BlockDispenser {

    public BlockDropper() {
        this(0);
    }

    public BlockDropper(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dropper";
    }

    @Override
    public int getId() {
        return DROPPER;
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    protected void createBlockEntity() {
        BlockEntity.createBlockEntity(BlockEntity.DROPPER, this);
    }

    @Override
    protected InventoryHolder getBlockEntity() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (!(blockEntity instanceof BlockEntityDropper)) {
            return null;
        }

        return (InventoryHolder) blockEntity;
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DefaultDispenseBehavior();
    }
}

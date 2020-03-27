package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.Dropper;
import cn.nukkit.blockentity.impl.DropperBlockEntity;
import cn.nukkit.dispenser.DefaultDispenseBehavior;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Identifier;

public class BlockDropper extends BlockDispenser {

    private BlockEntityType<? extends Dropper> dropperEntity;

    public BlockDropper(Identifier id) {
        super(id);
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    protected void createBlockEntity() {
        BlockEntityRegistry.get().newEntity(BlockEntityTypes.DROPPER, getChunk(), position);
    }

    @Override
    protected InventoryHolder getBlockEntity() {
        BlockEntity blockEntity = this.level.getBlockEntity(position);

        if (!(blockEntity instanceof DropperBlockEntity)) {
            return null;
        }

        return (InventoryHolder) blockEntity;
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DefaultDispenseBehavior();
    }
}

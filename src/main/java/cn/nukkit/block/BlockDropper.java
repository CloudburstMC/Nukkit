package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDropper;
import cn.nukkit.dispenser.DefaultDispenseBehavior;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    protected BlockEntityDropper createBlockEntity() {
        return createBlockEntity(BlockEntityDropper.class, BlockEntity.DROPPER);
    }

    @Override
    public BlockEntityDropper getBlockEntity() {
        return getTypedBlockEntity(BlockEntityDropper.class);
    }

    @Nonnull
    @Override
    public BlockEntityDropper getOrCreateBlockEntity() {
        return (BlockEntityDropper) super.getOrCreateBlockEntity();
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DefaultDispenseBehavior();
    }
}

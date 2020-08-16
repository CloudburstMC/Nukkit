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

    @Nonnull
    @Override
    public Class<? extends BlockEntityDropper> getBlockEntityClass() {
        return BlockEntityDropper.class;
    }

    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.DROPPER;
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DefaultDispenseBehavior();
    }
}

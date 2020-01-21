package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockSmokerBurning extends BlockFurnaceBurning {
    public BlockSmokerBurning() {
        this(0);
    }

    public BlockSmokerBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_SMOKER;
    }

    @Override
    public String getName() {
        return "Burning Smoker";
    }

    @Override
    protected String getEntityName() {
        return BlockEntity.SMOKER;
    }

    @Override
    protected BlockEntitySmoker createEntity(Position position, CompoundTag nbt) {
        return (BlockEntitySmoker) BlockEntity.createBlockEntity(BlockEntity.SMOKER, position, nbt);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSmoker());
    }
}

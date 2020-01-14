package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockBlastFurnaceBurning extends BlockFurnaceBurning {
    public BlockBlastFurnaceBurning() {
        this(0);
    }

    public BlockBlastFurnaceBurning(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIT_BLAST_FURNACE;
    }

    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }

    @Override
    protected String getEntityName() {
        return BlockEntity.BLAST_FURNACE;
    }

    @Override
    protected BlockEntityBlastFurnace createEntity(Position position, CompoundTag nbt) {
        return (BlockEntityBlastFurnace) BlockEntity.createBlockEntity(BlockEntity.BLAST_FURNACE, position, nbt);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}

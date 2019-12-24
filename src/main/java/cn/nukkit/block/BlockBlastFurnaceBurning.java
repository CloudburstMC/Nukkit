package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.level.format.FullChunk;
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
    protected BlockEntityFurnace createEntity(FullChunk chunk, CompoundTag nbt) {
        return new BlockEntityBlastFurnace(chunk, nbt);
    }
}

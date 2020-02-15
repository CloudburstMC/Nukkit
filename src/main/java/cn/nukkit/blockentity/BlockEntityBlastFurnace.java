package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

public class BlockEntityBlastFurnace extends BlockEntityFurnace {
    public BlockEntityBlastFurnace(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.BLAST_FURNACE;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Blast Furnace";
    }

    @Override
    public boolean isBlockEntityValid() {
        Identifier id = getBlock().getId();
        return id == BlockIds.BLAST_FURNACE || id == BlockIds.LIT_BLAST_FURNACE;
    }

    @Override
    public float getBurnRate() {
        return 2.0f;
    }

    @Override
    protected void extinguishFurnace() {
        this.getLevel().setBlock(this, Block.get(BlockIds.BLAST_FURNACE, this.getBlock().getDamage()), true);
    }

    @Override
    protected void lightFurnace() {
        this.getLevel().setBlock(this, Block.get(BlockIds.LIT_BLAST_FURNACE, this.getBlock().getDamage()), true);
    }
}

package cn.nukkit.blockentity.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlastFurnace;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

public class BlastFurnaceBlockEntity extends FurnaceBlockEntity implements BlastFurnace {

    public BlastFurnaceBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position, InventoryType.SMOKER);
    }

    @Override
    public boolean isValid() {
        Identifier id = getBlock().getId();
        return id == BlockIds.BLAST_FURNACE || id == BlockIds.LIT_BLAST_FURNACE;
    }

    @Override
    public float getBurnRate() {
        return 2.0f;
    }

    @Override
    protected void extinguishFurnace() {
        this.getLevel().setBlock(this.getPosition(), Block.get(BlockIds.BLAST_FURNACE, this.getBlock().getMeta()), true);
    }

    @Override
    protected void lightFurnace() {
        this.getLevel().setBlock(this.getPosition(), Block.get(BlockIds.LIT_BLAST_FURNACE, this.getBlock().getMeta()), true);
    }
}

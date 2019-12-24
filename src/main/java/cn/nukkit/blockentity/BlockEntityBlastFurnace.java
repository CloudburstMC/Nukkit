package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.SmeltingRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBlastFurnace extends BlockEntityFurnace {
    public BlockEntityBlastFurnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected String getFurnaceName() {
        return "Blast Furnace";
    }

    @Override
    protected String getClientName() {
        return BLAST_FURNACE;
    }

    @Override
    protected int getIdleBlockId() {
        return Block.BLAST_FURNACE;
    }

    @Override
    protected int getBurningBlockId() {
        return Block.LIT_BLAST_FURNACE;
    }

    @Override
    protected InventoryType getInventoryType() {
        return InventoryType.BLAST_FURNACE;
    }

    @Override
    protected SmeltingRecipe matchRecipe(Item raw) {
        return this.server.getCraftingManager().matchBlastFurnaceRecipe(raw);
    }

    @Override
    protected int getSpeedMultiplier() {
        return 2;
    }
}

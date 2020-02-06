package cn.nukkit.blockentity;

import cn.nukkit.block.BlockIds;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

public class BlockEntitySmoker extends BlockEntityFurnace {
    public BlockEntitySmoker(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.SMOKER;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Smoker";
    }

    @Override
    public boolean isBlockEntityValid() {
        Identifier id = getBlock().getId();
        return id == BlockIds.SMOKER || id == BlockIds.LIT_SMOKER;
    }
}

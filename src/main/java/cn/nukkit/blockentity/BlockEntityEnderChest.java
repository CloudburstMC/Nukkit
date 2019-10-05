package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable {

    public BlockEntityEnderChest(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.ENDER_CHEST;
    }

    @Override
    public String getName() {
        return "EnderChest";
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.ENDER_CHEST)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
    }
}

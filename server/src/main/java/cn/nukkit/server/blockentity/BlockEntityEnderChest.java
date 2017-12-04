package cn.nukkit.server.blockentity;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable {

    public BlockEntityEnderChest(FullChunk chunk, CompoundTag nbt) {
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

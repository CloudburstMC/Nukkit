package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.*;

public class BlockEntitySpawner extends BlockEntitySpawnable {

    public BlockEntitySpawner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.MOB_SPAWNER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == Block.MONSTER_SPAWNER;
    }
}

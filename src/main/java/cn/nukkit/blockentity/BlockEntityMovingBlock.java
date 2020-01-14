package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3i;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.BlockRegistry;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    public Block block;

    public Vector3i piston;
    public int progress;

    public BlockEntityMovingBlock(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("movingBlockData") && namedTag.contains("movingBlockId")) {
            this.block = Block.get(namedTag.getInt("movingBlockId"), namedTag.getInt("movingBlockData"));
        } else {
            this.close();
        }

        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            this.piston = new Vector3i(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.close();
        }

        super.initBlockEntity();
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public boolean isBlockEntityValid() {
        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, MOVING_BLOCK)
                .putFloat("movingBlockId", BlockRegistry.get().getLegacyId(this.block.getId()))
                .putFloat("movingBlockData", this.block.getDamage())
                .putInt("pistonPosX", this.piston.x)
                .putInt("pistonPosY", this.piston.y)
                .putInt("pistonPosZ", this.piston.z);
    }
}

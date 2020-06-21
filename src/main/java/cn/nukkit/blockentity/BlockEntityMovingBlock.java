package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    public Block block;

    public BlockVector3 piston;
    public int progress;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
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
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
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
                .putFloat("movingBlockId", this.block.getId())
                .putFloat("movingBlockData", this.block.getDamage())
                .putInt("pistonPosX", this.piston.x)
                .putInt("pistonPosY", this.piston.y)
                .putInt("pistonPosZ", this.piston.z);
    }
}
